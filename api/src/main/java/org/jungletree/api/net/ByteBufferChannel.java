package org.jungletree.api.net;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public final class ByteBufferChannel implements ReadableByteChannel, WritableByteChannel {

    private final ByteBuf buf;

    public ByteBufferChannel(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int read(ByteBuffer dst) {
        return buf.read(dst);
    }

    @Override
    public int write(ByteBuffer src) {
        return buf.write(src);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() {}
}
