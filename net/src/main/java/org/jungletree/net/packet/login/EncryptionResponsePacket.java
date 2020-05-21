package org.jungletree.net.packet.login;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;

@Value
@Builder
public class EncryptionResponsePacket implements Packet {
    byte[] sharedSecret;
    byte[] verifyToken;
}