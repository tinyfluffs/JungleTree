package org.jungletree.net;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungletree.api.nbt.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUuid(ByteBuf buf, UUID value) {
        buf.writeLong(value.getMostSignificantBits());
        buf.writeLong(value.getLeastSignificantBits());
    }

    public static void writeTag(ByteBuf buf, Tag value) throws IOException {
        TagType type = value.type();
        buf.writeByte(type.ordinal());

        switch (type) {
            case END -> {}
            case BYTE -> buf.writeByte(((ByteTag) value).getValue());
            case SHORT -> buf.writeShort(((ShortTag) value).getValue());
            case INT -> buf.writeInt(((IntTag) value).getValue());
            case LONG -> buf.writeLong(((LongTag) value).getValue());
            case FLOAT -> buf.writeFloat(((LongTag) value).getValue());
            case DOUBLE -> buf.writeDouble(((LongTag) value).getValue());
            case BYTE_ARRAY -> {
                byte[] bytes = ((ByteArrayTag) value).getValue();
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
            }
            case STRING -> {
                byte[] bytes = ((StringTag) value).getValue().getBytes(StandardCharsets.UTF_8);
                buf.writeShort(bytes.length);
                buf.writeBytes(bytes);
            }
            case LIST -> {
                ListTag<Tag> list = (ListTag<Tag>) value;
                List<Tag> tags = list.getValue();

                buf.writeByte(list.itemType().ordinal());
                buf.writeInt(tags.size());
                for (Tag t : tags) {
                    writeTag(buf, t);
                }
            }
            case COMPOUND -> {
                Map<String, Tag> values = ((CompoundTag) value).getValue();
                for (Map.Entry<String, Tag> e : values.entrySet()) {
                    if (e.getValue() instanceof EndTag) {
                        throw new IOException("EndTag not permitted in CompoundTag");
                    }

                    byte[] name = e.getKey().getBytes(StandardCharsets.UTF_8);
                    Tag tag = e.getValue();

                    buf.writeByte(tag.type().ordinal());
                    buf.writeShort(name.length);
                    buf.writeBytes(name);

                    writeTag(buf, tag);
                }
            }
            case INT_ARRAY -> {
                int[] values = ((IntArrayTag) value).getValue();
                buf.writeInt(values.length);
                for (int v : values) {
                    buf.writeInt(v);
                }
            }
            case LONG_ARRAY -> {
                long[] values = ((LongArrayTag) value).getValue();
                buf.writeInt(values.length);
                for (long v : values) {
                    buf.writeLong(v);
                }
            }
        }
    }
}
