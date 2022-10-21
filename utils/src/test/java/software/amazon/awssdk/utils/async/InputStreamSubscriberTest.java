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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputStreamSubscriberTest {
    private SimplePublisher<ByteBuffer> publisher;
    private InputStreamSubscriber subscriber;

    @BeforeEach
    public void setup() {
        publisher = new SimplePublisher<>();
        subscriber = new InputStreamSubscriber();
    }

    @Test
    public void onComplete_returnsEndOfStream_onRead() {
        publisher.subscribe(subscriber);
        publisher.complete();
        assertThat(subscriber.read()).isEqualTo(-1);
        assertThat(subscriber.read(new byte[0])).isEqualTo(-1);
        assertThat(subscriber.read(new byte[0], 0, 0)).isEqualTo(-1);
    }

    @Test
    public void onError_throws_onRead() {
        IllegalStateException exception = new IllegalStateException();

        publisher.subscribe(subscriber);
        publisher.error(exception);
        assertThatThrownBy(() -> subscriber.read()).isEqualTo(exception);
        assertThatThrownBy(() -> subscriber.read(new byte[0])).isEqualTo(exception);
        assertThatThrownBy(() -> subscriber.read(new byte[0], 0, 0)).isEqualTo(exception);
    }

    @Test
    public void onComplete_afterOnNext_returnsEndOfStream() {
        publisher.subscribe(subscriber);
        publisher.send(byteBufferOfLength(1));
        publisher.complete();
        assertThat(subscriber.read()).isEqualTo(0);
        assertThat(subscriber.read()).isEqualTo(-1);
    }

    @Test
    public void onComplete_afterEmptyOnNext_returnsEndOfStream() {
        publisher.subscribe(subscriber);
        publisher.send(byteBufferOfLength(0));
        publisher.send(byteBufferOfLength(0));
        publisher.send(byteBufferOfLength(0));
        publisher.complete();
        assertThat(subscriber.read()).isEqualTo(-1);
    }

    @Test
    public void read_afterOnNext_returnsData() {
        publisher.subscribe(subscriber);
        publisher.send(byteBufferWithByte(10));
        assertThat(subscriber.read()).isEqualTo(10);
    }

    @Test
    public void readBytes_afterOnNext_returnsData() {
        publisher.subscribe(subscriber);
        publisher.send(byteBufferWithByte(10));
        publisher.send(byteBufferWithByte(20));

        byte[] bytes = new byte[2];
        assertThat(subscriber.read(bytes)).isEqualTo(2);
        assertThat(bytes[0]).isEqualTo((byte) 10);
        assertThat(bytes[1]).isEqualTo((byte) 20);
    }

    @Test
    public void readBytesWithOffset_afterOnNext_returnsData() {
        publisher.subscribe(subscriber);
        publisher.send(byteBufferWithByte(10));
        publisher.send(byteBufferWithByte(20));

        byte[] bytes = new byte[3];
        assertThat(subscriber.read(bytes, 1, 2)).isEqualTo(2);
        assertThat(bytes[1]).isEqualTo((byte) 10);
        assertThat(bytes[2]).isEqualTo((byte) 20);
    }

    @Test
    public void read_afterClose_fails() {
        publisher.subscribe(subscriber);
        subscriber.close();
        assertThatThrownBy(() -> subscriber.read()).hasRootCauseInstanceOf(CancellationException.class);
        assertThatThrownBy(() -> subscriber.read(new byte[0])).hasRootCauseInstanceOf(CancellationException.class);
        assertThatThrownBy(() -> subscriber.read(new byte[0], 0, 0)).hasRootCauseInstanceOf(CancellationException.class);
    }



    private ByteBuffer byteBufferOfLength(int length) {
        return ByteBuffer.allocate(length);
    }

    public ByteBuffer byteBufferWithByte(int b) {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) b);
        buffer.flip();
        return buffer;
    }

}