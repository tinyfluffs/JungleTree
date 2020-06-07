package org.jungletree.net.packet.play;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.nbt.CompoundTag;
import org.jungletree.api.world.biome.Biome;
import org.jungletree.api.world.biome.BiomeType;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.net.Packet;
import org.jungletree.world.chunk.JungleChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChunkDataPacket implements Packet {

    public static final int NO_CHANGES = 0x00;
    public static final int ALL_CHANGED = 0xFFFF;

    private static final int MAX_READ_SIZE = 16 * 1024 * 128; // 16MiB

    int x;
    int z;

    int availableSections;
    CompoundTag heightmaps;
    Biome[] biomes;
    byte[] buffer;
    List<CompoundTag> blockEntities;
    boolean fullChunk;

    public ChunkDataPacket(JungleChunk chunk, int sectionFilter) {
        this.x = chunk.getX();
        this.z = chunk.getZ();

        this.fullChunk = sectionFilter == ALL_CHANGED;
        this.heightmaps = new CompoundTag();

        if (this.fullChunk) {
            this.biomes = chunk.getBiomes();
        }
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
        buf.writeBoolean(this.fullChunk);
        buf.writeVarInt(this.availableSections);
        buf.writeNbt(this.heightmaps);
        if (this.biomes != null) {
            encodeBiomes(buf);
        }

        buf.writeByteArray(this.buffer);

        buf.writeVarInt(this.blockEntities.size());
        for (CompoundTag blockEntity : this.blockEntities) {
            buf.writeNbt(blockEntity);
        }
    }

    @Override
    public void decode(ByteBuf buf) {
        this.x = buf.readInt();
        this.z = buf.readInt();
        this.fullChunk = buf.readBoolean();
        this.availableSections = buf.readVarInt();
        this.heightmaps = buf.readNbt();

        if (!isFullChunk()) {
            decodeBiomes(buf);
        }

        int dataLength = buf.readVarInt();
        if (dataLength > MAX_READ_SIZE) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        } else {
            this.buffer = new byte[dataLength];
            buf.readBytes(buffer);
            int entityLength = buf.readVarInt();
            this.blockEntities = new ArrayList<>();
            IntStream.range(0, entityLength).forEach(i -> this.blockEntities.add(buf.readNbt()));
        }
    }

    private void encodeBiomes(ByteBuf buf) {
        for (Biome biome : this.biomes) {
            buf.writeInt(biome.getId());
        }
    }

    private void decodeBiomes(ByteBuf buf) {
        for (int i = 0; i < this.biomes.length; i++) {
            this.biomes[i] = BiomeType.fromId(buf.readInt()).orElseThrow().get();
        }
    }
}
