package org.jungletree.net;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Codec<T extends Packet> {

    ByteBuf encode(ByteBuf buf, T p) throws IOException;

    T decode(ByteBuf buf) throws IOException;

    class CodecRegistration {
        private final int opcode;
        private final Codec<?> codec;

        public CodecRegistration(int opcode, Codec<?> codec) {
            this.opcode = opcode;
            this.codec = codec;
        }

        public int getOpcode() {
            return opcode;
        }

        public <M extends Packet> Codec<M> getCodec() {
            return (Codec<M>) codec;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + this.opcode;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            return this.opcode == ((CodecRegistration) obj).opcode;
        }
    }
}
