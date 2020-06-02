package org.jungletree.net.packet.play;

import lombok.*;
import org.jungletree.api.nbt.CompoundTag;
import org.jungletree.api.world.Chunk;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChunkDataPacket implements Packet {

    public static final int NO_CHANGES = 0x00;
    public static final int ALL_CHANGED = 0xFFFF;

    private int x;
    private int z;

    private int availableSections;
    private CompoundTag heightmaps;

    int chunkX;
    int chunkZ;
    boolean fullChunk;
    Chunk chunk;

    @Override
    public void encode(FriendlyByteBuf buf) {}

    @Override
    public void decode(FriendlyByteBuf buf) {}
}
