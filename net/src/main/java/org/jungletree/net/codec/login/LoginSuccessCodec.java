package org.jungletree.net.codec.login;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.login.LoginSuccessPacket;

import java.io.IOException;
import java.util.UUID;

import static org.jungletree.net.ByteBufUtils.*;

public class LoginSuccessCodec implements Codec<LoginSuccessPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, LoginSuccessPacket p) throws IOException {
        writeString(buf, p.getUuid().toString());
        writeString(buf, p.getUsername());
        return buf;
    }

    @Override
    public LoginSuccessPacket decode(ByteBuf buf) throws IOException {
        return LoginSuccessPacket.builder()
                .uuid(UUID.fromString(readString(buf)))
                .username(readString(buf))
                .build();
    }
}
