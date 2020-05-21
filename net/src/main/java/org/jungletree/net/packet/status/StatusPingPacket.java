package org.jungletree.net.packet.status;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;

@Value
@Builder
public class StatusPingPacket implements Packet {
    long time;
}
