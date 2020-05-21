package org.jungletree.net;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

public interface Codec<T extends Packet> {

    ByteBuf encode(ByteBuf buf, T p) throws IOException;

    T decode(ByteBuf buf) throws IOException;

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    class CodecRegistration {
        @Getter int opcode;
        Codec<?> codec;

        public CodecRegistration(int opcode, Codec<?> codec) {
            this.opcode = opcode;
            this.codec = codec;
        }

        public <M extends Packet> Codec<M> getCodec() {
            return (Codec<M>) codec;
        }
    }
}
