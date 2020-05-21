package org.jungletree.net.packet.handshake;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;
import org.jungletree.net.protocol.Protocols;

@Value
@Builder
public class HandshakePacket implements Packet {
    int protocolVersion;
    String address;
    int port;
    Protocols nextState;
}
