package org.jungletree.net.packet.login;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;

import java.util.UUID;

@Value
@Builder
public class LoginSuccessPacket implements Packet {
    UUID uuid;
    String username;
}
