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

package software.amazon.awssdk.utils.async;

import static software.amazon.awssdk.utils.CompletableFutureUtils.joinInterruptibly;
import static software.amazon.awssdk.utils.CompletableFutureUtils.joinInterruptiblyIgnoringFailures;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class InputStreamConsumingPublisher implements Publisher<ByteBuffer> {
    private static final int BUFFER_SIZE = 16 * 1024; // 16 KB

    private final SimplePublisher<ByteBuffer> delegate = new SimplePublisher<>();

    public long doBlockingWrite(InputStream inputStream) {
        try {
            long dataWritten = 0;
            while (true) {
                byte[] data = new byte[BUFFER_SIZE];
                int dataLength = inputStream.read(data);
                if (dataLength > 0) {
                    dataWritten += dataLength;
                    joinInterruptibly(delegate.send(ByteBuffer.wrap(data, 0, dataLength)));
                } else if (dataLength < 0) {
                    joinInterruptibly(delegate.complete());
                    break;
                }
            }
            return dataWritten;
        } catch (IOException e) {
            joinInterruptiblyIgnoringFailures(delegate.error(e));
            throw new UncheckedIOException(e);
        } catch (RuntimeException | Error e) {
            joinInterruptiblyIgnoringFailures(delegate.error(e));
            throw e;
        }
    }

    public void cancel() {
        delegate.error(new CancellationException("Input stream has been cancelled."));
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        delegate.subscribe(s);
    }
}
