package org.jungletree.net;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.world.chunk.JungleChunk;
import org.jungletree.world.chunk.JungleChunkSection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jungletree.api.world.World.CHUNK_SECTION_HEIGHT;
import static org.jungletree.api.world.World.CHUNK_WIDTH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ByteBufUtils {

    public static String readString(ByteBuf buf) throws IOException {
        final int len = readVarInt(buf);
        final byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeString(ByteBuf buf, String value) throws IOException {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= Short.MAX_VALUE) {
            throw new IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuf!");
        }
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static int readVarInt(ByteBuf buf) throws IOException {
        int out = 0;
        int bytes = 0;
        byte in;
        do {
            in = buf.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > 5) {
                throw new IOException("Attempt to read int bigger than allowed for a varint!");
            }
        } while ((in & 0x80) == 0x80);
        return out;
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        byte part;
        do {
            part = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            buf.writeByte(part);
        } while (value != 0);
    }

    public static long readVarLong(ByteBuf buf) throws IOException {
        long out = 0;
        int bytes = 0;
        byte in;
        do {
            in = buf.readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > 10) {
                throw new IOException("Attempt to read long bigger than allowed for a varlong!");
            }
        } while ((in & 0x80) == 0x80);
        return out;
    }

    public static void writeVarLong(ByteBuf buf, long value) {
        byte part;
        do {
            part = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            buf.writeByte(part);
        } while (value != 0);
    }

    public static byte[] readByteArray(ByteBuf buf) throws IOException {
        var result = new byte[readVarInt(buf)];
        buf.readBytes(result);
        return result;
    }

    public static void writeByteArray(ByteBuf buf, byte[] value) {
        writeVarInt(buf, value.length);
        buf.writeBytes(value);
    }

    public static ChatMessage readChatMessage(ByteBuf buf) throws IOException {
        return ChatMessage.fromJson(readString(buf));
    }

    public static void writeChatMessage(ByteBuf buf, ChatMessage value) throws IOException {
        writeString(buf, value.toJson().toString());
    }

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUuid(ByteBuf buf, UUID value) {
        buf.writeLong(value.getMostSignificantBits());
        buf.writeLong(value.getLeastSignificantBits());
    }

    public static void writeChunk(ByteBuf buf, JungleChunk chunk, boolean fullChunk) {
        JungleChunkSection[] sections = chunk.getSections();
        List<JungleChunkSection> usedSections = new ArrayList<>();
        int avail = 0;
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] != null && !(fullChunk && sections[i].isEmpty())) {
                avail |= 1 << i;
                usedSections.add(sections[i]);
            }
        }
        buf.writeShort((short) (avail & 0xFFFF));

        byte[] chunkData = new byte[chunkSize(Integer.bitCount(avail), fullChunk)];

        int pos = 0;
        for (JungleChunkSection section : usedSections) {
            buf.writeShort(section.getLazyBlockCount());

            for (char block : section.getBlocks()) {
                chunkData[pos++] = (byte) (block & 0xFF);
                chunkData[pos++] = (byte) (block >> 8 & 0xFF);
            }
        }

        if (fullChunk) {
            byte[] biomes = chunk.getBiomes();
            System.arraycopy(biomes, 0, chunkData, pos, biomes.length);
            pos += biomes.length;
        }

        writeVarInt(buf, chunkData.length);
        buf.writeBytes(chunkData);

        writeVarInt(buf, 0); // Block entity count
    }

    private static int chunkSize(int sections, boolean biomes) {
        int blocksSize = sections * 2 * CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_SECTION_HEIGHT;
        int biomesSize = biomes ? CHUNK_WIDTH * CHUNK_WIDTH : 0;
        return blocksSize + biomesSize;
    }
}
