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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.async.ByteBufferStoringSubscriber.TransferResult;

public class InputStreamSubscriber extends InputStream implements Subscriber<ByteBuffer>, SdkAutoCloseable {
    private static final int BUFFER_SIZE = 4 * 1024 * 1024; // 4 MB

    private final ByteBufferStoringSubscriber delegate;
    private final ByteBuffer singleByte = ByteBuffer.allocate(1);

    private final AtomicReference<State> state = new AtomicReference<>(State.INITIAL);
    private Subscription subscription;

    public InputStreamSubscriber() {
        this.delegate = new ByteBufferStoringSubscriber(BUFFER_SIZE);
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (state.compareAndSet(State.INITIAL, State.SUBSCRIBED)) {
            this.subscription = s;
            delegate.onSubscribe(s);
        } else {
            close();
        }
    }

    @Override
    public void onNext(ByteBuffer byteBuffer) {
        delegate.onNext(byteBuffer);
    }

    @Override
    public void onError(Throwable t) {
        delegate.onError(t);
    }

    @Override
    public void onComplete() {
        delegate.onComplete();
    }

    @Override
    public int read() {
        singleByte.clear();
        TransferResult transferResult = delegate.blockingTransferTo(singleByte);

        if (singleByte.hasRemaining()) {
            assert transferResult == TransferResult.END_OF_STREAM;
            return -1;
        }

        return singleByte.get(0);
    }

    @Override
    public int read(byte[] b) {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] bytes, int off, int len) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, off, len);
        TransferResult transferResult = delegate.blockingTransferTo(byteBuffer);
        int dataTransferred = byteBuffer.position() - off;

        if (dataTransferred == 0) {
            assert transferResult == TransferResult.END_OF_STREAM;
            return -1;
        }

        return dataTransferred;
    }

    @Override
    public void close() {
        if (state.compareAndSet(State.SUBSCRIBED, State.COMPLETED)) {
            subscription.cancel();
            // No need to signal delegate.onError, it doesn't use it for anything.
        }
    }

    private enum State {
        INITIAL,
        SUBSCRIBED,
        COMPLETED
    }
}
