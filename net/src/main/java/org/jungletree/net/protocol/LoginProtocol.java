package org.jungletree.net.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jungletree.net.codec.login.DisconnectCodec;
import org.jungletree.net.codec.login.EncryptionResponseCodec;
import org.jungletree.net.codec.login.EncryptionRequestCodec;
import org.jungletree.net.codec.login.LoginStartCodec;
import org.jungletree.net.http.HttpClient;
import org.jungletree.net.packet.login.DisconnectPacket;
import org.jungletree.net.packet.login.EncryptionRequestPacket;
import org.jungletree.net.packet.login.EncryptionResponsePacket;
import org.jungletree.net.packet.login.LoginStartPacket;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoginProtocol extends Protocol {

    @Getter HttpClient httpClient;

    public LoginProtocol(HttpClient httpClient) {
        super("LOGIN", 5);
        this.httpClient = httpClient;

        inbound(0x00, LoginStartPacket.class, LoginStartCodec.class);
        inbound(0x01, EncryptionResponsePacket.class, EncryptionResponseCodec.class);

        outbound(0x00, DisconnectPacket.class, DisconnectCodec.class);
        outbound(0x01, EncryptionRequestPacket.class, EncryptionRequestCodec.class);
    }
}
