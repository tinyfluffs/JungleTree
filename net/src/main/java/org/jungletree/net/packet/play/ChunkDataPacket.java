package org.jungletree.net.packet.play;

import lombok.*;
import org.jungletree.api.world.Chunk;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChunkDataPacket implements Packet {

    int chunkX;
    int chunkZ;
    boolean fullChunk;
    Chunk chunk;

    @Override
    public void encode(FriendlyByteBuf buf) {}

    @Override
    public void decode(FriendlyByteBuf buf) {}
}
