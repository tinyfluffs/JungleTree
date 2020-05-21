package org.jungletree.net.codec.login;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.login.EncryptionRequestPacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.*;

public class EncryptionRequestCodec implements Codec<EncryptionRequestPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, EncryptionRequestPacket p) throws IOException {
        writeString(buf, p.getSessionId());
        writeByteArray(buf, p.getPublicKey());
        writeByteArray(buf, p.getVerifyToken());
        return buf;
    }

    @Override
    public EncryptionRequestPacket decode(ByteBuf buf) throws IOException {
        return EncryptionRequestPacket.builder()
                .sessionId(readString(buf))
                .publicKey(readByteArray(buf))
                .verifyToken(readByteArray(buf))
                .build();
    }
}
