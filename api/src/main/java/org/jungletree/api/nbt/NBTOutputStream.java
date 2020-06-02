/*
 * This file is part of Flow NBT, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2011 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jungletree.api.nbt;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class NBTOutputStream implements Closeable {

    private final EndianSwitchableOutputStream os;

    public NBTOutputStream(OutputStream os) throws IOException {
        this(os, true, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
        this(os, compressed, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream os, boolean compressed, ByteOrder endianness) throws IOException {
        this.os = new EndianSwitchableOutputStream(compressed ? new GZIPOutputStream(os) : os, endianness);
    }

    public void writeTag(Tag<?> tag) throws IOException {
        os.writeByte(tag.getType().getId());
        if (tag instanceof NamedTag) {
            String name = ((NamedTag) tag).getName();
            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            os.writeShort(nameBytes.length);
            os.write(nameBytes);
        }
        if (tag.getType() == TagType.TAG_END) {
            throw new IOException("Named TAG_End not permitted.");
        }
        writeTagPayload(tag);
    }

    private void writeTagPayload(Tag<?> tag) throws IOException {
        switch (tag.getType()) {
            case TAG_END -> writeEndTagPayload((EndTag) tag);
            case TAG_BYTE -> writeByteTagPayload((ByteTag) tag);
            case TAG_SHORT -> writeShortTagPayload((ShortTag) tag);
            case TAG_INT -> writeIntTagPayload((IntTag) tag);
            case TAG_LONG -> writeLongTagPayload((LongTag) tag);
            case TAG_FLOAT -> writeFloatTagPayload((FloatTag) tag);
            case TAG_DOUBLE -> writeDoubleTagPayload((DoubleTag) tag);
            case TAG_BYTE_ARRAY -> writeByteArrayTagPayload((ByteArrayTag) tag);
            case TAG_STRING -> writeStringTagPayload((StringTag) tag);
            case TAG_LIST -> writeListTagPayload((ListTag<?>) tag);
            case TAG_COMPOUND -> writeCompoundTagPayload((CompoundTag) tag);
            case TAG_INT_ARRAY -> writeIntArrayTagPayload((IntArrayTag) tag);
            case TAG_LONG_ARRAY -> writeLongArrayTagPayload((LongArrayTag) tag);
            default -> throw new IOException("Invalid tag type: " + tag.getType() + ".");
        }
    }

    private void writeByteTagPayload(ByteTag tag) throws IOException {
        os.writeByte(tag.getValue());
    }

    private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
        byte[] bytes = tag.getValue();
        os.writeInt(bytes.length);
        os.write(bytes);
    }

    private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
        for (Tag<?> childTag : tag.getValue().values()) {
            writeTag(childTag);
        }
        os.writeByte(TagType.TAG_END.getId()); // end tag - better way?
    }

    private void writeListTagPayload(ListTag<?> tag) throws IOException {
        Class<? extends Tag<?>> clazz = tag.getElementType();
        List<Tag<?>> tags = (List<Tag<?>>) tag.getValue();
        int size = tags.size();

        os.writeByte(TagType.getByTagClass(clazz).getId());
        os.writeInt(size);
        for (Tag<?> tag1 : tags) {
            writeTagPayload(tag1);
        }
    }

    private void writeStringTagPayload(StringTag tag) throws IOException {
        byte[] bytes = tag.getValue().getBytes(StandardCharsets.UTF_8);
        os.writeShort(bytes.length);
        os.write(bytes);
    }

    private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
        os.writeDouble(tag.getValue());
    }

    private void writeFloatTagPayload(FloatTag tag) throws IOException {
        os.writeFloat(tag.getValue());
    }

    private void writeLongTagPayload(LongTag tag) throws IOException {
        os.writeLong(tag.getValue());
    }

    private void writeIntTagPayload(IntTag tag) throws IOException {
        os.writeInt(tag.getValue());
    }

    private void writeShortTagPayload(ShortTag tag) throws IOException {
        os.writeShort(tag.getValue());
    }

    private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
        int[] ints = tag.getValue();
        os.writeInt(ints.length);
        for (int anInt : ints) {
            os.writeInt(anInt);
        }
    }

    private void writeLongArrayTagPayload(LongArrayTag tag) throws IOException {
        long[] longs = tag.getValue();
        os.writeInt(longs.length);
        for (long aLong : longs) {
            os.writeLong(aLong);
        }
    }

    private void writeEndTagPayload(EndTag ignored) {}

    public void close() throws IOException {
        os.close();
    }

    public ByteOrder getEndianness() {
        return os.getEndianness();
    }

    public void flush() throws IOException {
        os.flush();
    }
}
