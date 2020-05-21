package org.jungletree.net.codec.login;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.login.EncryptionResponsePacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.readByteArray;
import static org.jungletree.net.ByteBufUtils.writeByteArray;

public class EncryptionResponseCodec implements Codec<EncryptionResponsePacket> {
    
    @Override
    public ByteBuf encode(ByteBuf buf, EncryptionResponsePacket p) throws IOException {
        writeByteArray(buf, p.getSharedSecret());
        writeByteArray(buf, p.getVerifyToken());
        return buf;
    }

    @Override
    public EncryptionResponsePacket decode(ByteBuf buf) throws IOException {
        return EncryptionResponsePacket.builder()
                .sharedSecret(readByteArray(buf))
                .verifyToken(readByteArray(buf))
                .build();
    }
}
