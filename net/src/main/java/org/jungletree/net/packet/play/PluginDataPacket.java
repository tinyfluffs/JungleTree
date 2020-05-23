package org.jungletree.net.packet.play;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;

@Value
@Builder
public class PluginDataPacket implements Packet {
    String channel;
    byte[] data;
}
