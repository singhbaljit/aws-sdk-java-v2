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

package software.amazon.awssdk.core.internal.async;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.internal.util.NoopSubscription;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class InputStreamWithExecutorAsyncRequestBody implements AsyncRequestBody {
    private static final Logger log = Logger.loggerFor(InputStreamWithExecutorAsyncRequestBody.class);

    private final BlockingInputStreamAsyncRequestBody delegate;
    private final InputStream inputStream;
    private final ExecutorService executor;

    public InputStreamWithExecutorAsyncRequestBody(InputStream inputStream,
                                                   Long contentLength,
                                                   ExecutorService executor) {
        this.delegate = new BlockingInputStreamAsyncRequestBody(contentLength);
        this.inputStream = inputStream;
        this.executor = executor;
    }

    @Override
    public Optional<Long> contentLength() {
        return delegate.contentLength();
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        try {
            executor.submit(this::doBlockingWrite);
            delegate.subscribe(s);
        } catch (Throwable t) {
            s.onSubscribe(new NoopSubscription(s));
            s.onError(t);
        }
    }

    private void doBlockingWrite() {
        try {
            delegate.doBlockingWrite(inputStream);
        } catch (Throwable t) {
            log.error(() -> "Encountered error while writing input stream to service.", t);
        }
    }
}
