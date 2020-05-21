package org.jungletree.net.codec.login;

import io.netty.buffer.ByteBuf;
import org.jungletree.net.Codec;
import org.jungletree.net.packet.login.LoginStartPacket;

import java.io.IOException;

import static org.jungletree.net.ByteBufUtils.readString;
import static org.jungletree.net.ByteBufUtils.writeString;

public class LoginStartCodec implements Codec<LoginStartPacket> {

    @Override
    public ByteBuf encode(ByteBuf buf, LoginStartPacket p) throws IOException {
        writeString(buf, p.getUsername());
        return buf;
    }

    @Override
    public LoginStartPacket decode(ByteBuf buf) throws IOException {
        return LoginStartPacket.builder()
                .username(readString(buf))
                .build();
    }
}
