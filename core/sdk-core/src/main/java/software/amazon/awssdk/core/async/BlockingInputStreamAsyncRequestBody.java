/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.core.async;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.utils.async.InputStreamConsumingPublisher;

public class BlockingInputStreamAsyncRequestBody implements AsyncRequestBody {
    private final InputStreamConsumingPublisher delegate = new InputStreamConsumingPublisher();
    private final CountDownLatch subscribedLatch = new CountDownLatch(0);
    private final Long contentLength;

    public BlockingInputStreamAsyncRequestBody(Long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.ofNullable(contentLength);
    }

    public void doBlockingWrite(InputStream inputStream) {
        try {
            waitForSubscriptionIfNeeded();
            delegate.doBlockingWrite(inputStream);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            delegate.cancel();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        delegate.subscribe(s);
        subscribedLatch.countDown();
    }

    private void waitForSubscriptionIfNeeded() throws InterruptedException {
        if (!subscribedLatch.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("The service request was not made within 10 seconds of doBlockingWrite being "
                                            + "invoked. Make sure to invoke the service request BEFORE invoking doBlockingWrite "
                                            + "if your caller is single-threaded.");
        }
    }
}
