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

import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.CancellableOutputStream;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class OutputStreamPublisher extends CancellableOutputStream implements Publisher<ByteBuffer> {
    private final SimplePublisher<ByteBuffer> delegate = new SimplePublisher<>();
    private final CountDownLatch subscribedLatch = new CountDownLatch(0);
    private final AtomicBoolean done = new AtomicBoolean(false);

    private ByteBuffer singleByteBuffer;

    @Override
    public void write(int b) {
        Validate.validState(!done.get(), "Output stream is cancelled or closed.");

        if (singleByteBuffer != null && !singleByteBuffer.hasRemaining()) {
            flush();
        }

        if (singleByteBuffer == null) {
            singleByteBuffer = ByteBuffer.allocate(4 * 1024); // 4 KB
        }

        singleByteBuffer.put((byte) b);
    }

    @Override
    public void write(byte[] b) {
        flush();
        send(ByteBuffer.wrap(b));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        flush();
        send(ByteBuffer.wrap(b, off, len));
    }

    @Override
    public void flush() {
        if (singleByteBuffer != null && singleByteBuffer.position() > 0) {
            singleByteBuffer.flip();
            send(singleByteBuffer);
            singleByteBuffer = null;
        }
    }

    @Override
    public void cancel() {
        if (done.compareAndSet(false, true)) {
            delegate.error(new CancellationException("Output stream has been cancelled."));
        }
    }

    @Override
    public void close() {
        if (done.compareAndSet(false, true)) {
            flush();
            joinInterruptibly(delegate.complete());
        }
    }

    private void send(ByteBuffer bytes) {
        joinInterruptibly(delegate.send(bytes));
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        delegate.subscribe(s);
        subscribedLatch.countDown();
    }
}
