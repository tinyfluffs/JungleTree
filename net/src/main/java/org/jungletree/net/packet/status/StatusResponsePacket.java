package org.jungletree.net.packet.status;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;

@Value
@Builder
public class StatusResponsePacket implements Packet {
    String json;
}
