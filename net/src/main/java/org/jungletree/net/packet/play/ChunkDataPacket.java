package org.jungletree.net.packet.play;

import lombok.Builder;
import lombok.Value;
import org.jungletree.net.Packet;
import org.jungletree.world.chunk.JungleChunk;

@Value
@Builder
public class ChunkDataPacket implements Packet {
    int chunkX;
    int chunkZ;
    boolean fullChunk;
    JungleChunk chunk;
}
