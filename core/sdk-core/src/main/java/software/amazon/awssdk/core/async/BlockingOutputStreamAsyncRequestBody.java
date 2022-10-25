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

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.CancellableOutputStream;
import software.amazon.awssdk.utils.async.OutputStreamPublisher;

@SdkPublicApi
public class BlockingOutputStreamAsyncRequestBody implements AsyncRequestBody {
    private final OutputStreamPublisher delegate = new OutputStreamPublisher();
    private final CountDownLatch subscribedLatch = new CountDownLatch(0);
    private final Long contentLength;

    public BlockingOutputStreamAsyncRequestBody(Long contentLength) {
        this.contentLength = contentLength;
    }

    public CancellableOutputStream outputStream() {
        waitForSubscriptionIfNeeded();
        return delegate;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.ofNullable(contentLength);
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        delegate.subscribe(s);
        subscribedLatch.countDown();
    }

    private void waitForSubscriptionIfNeeded() {
        try {
            if (!subscribedLatch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("The service request was not made within 10 seconds of outputStream being "
                                                + "invoked. Make sure to invoke the service request BEFORE invoking outputStream "
                                                + "if your caller is single-threaded.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for subscription.", e);
        }
    }
}
