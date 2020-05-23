package org.jungletree.net.packet.play;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;

@Value
@Builder
public class KeepAlivePacket implements Packet {
    long id;
}
