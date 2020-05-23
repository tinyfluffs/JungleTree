package org.jungletree.net.packet.play;

import lombok.Builder;
import lombok.Value;
import org.jungletree.api.world.Chunk;
import org.jungletree.net.Packet;

@Value
@Builder
public class ChunkDataPacket implements Packet {
    int chunkX;
    int chunkZ;
    boolean fullChunk;
    Chunk chunk;
}
