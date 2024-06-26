package org.jungletree.net.protocol;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jungletree.net.packet.DisconnectPacket;
import org.jungletree.net.packet.login.EncryptionRequestPacket;
import org.jungletree.net.packet.login.EncryptionResponsePacket;
import org.jungletree.net.packet.login.LoginStartPacket;
import org.jungletree.net.packet.login.LoginSuccessPacket;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoginProtocol extends Protocol {

    public LoginProtocol() {
        super("LOGIN");

        inbound(0x00, LoginStartPacket.class);
        inbound(0x01, EncryptionResponsePacket.class);

        outbound(0x00, DisconnectPacket.class);
        outbound(0x01, EncryptionRequestPacket.class);
        outbound(0x02, LoginSuccessPacket.class);
    }
}
