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
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class NBTInputStream implements Closeable {

    private final EndianSwitchableInputStream is;

    public NBTInputStream(InputStream is) throws IOException {
        this(is, true, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream is, boolean compressed) throws IOException {
        this(is, compressed, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream is, boolean compressed, ByteOrder endianness) throws IOException {
        this.is = new EndianSwitchableInputStream(compressed ? new GZIPInputStream(is) : is, endianness);
    }

    public Tag readTag() throws IOException {
        return readTag(0);
    }

    private Tag readTag(int depth) throws IOException {
        int typeId = is.readByte() & 0xFF;
        TagType type = TagType.getById(typeId);

        String name;
        if (type != TagType.TAG_END) {
            int nameLength = is.readShort() & 0xFFFF;
            byte[] nameBytes = new byte[nameLength];
            is.readFully(nameBytes);
            name = new String(nameBytes, StandardCharsets.UTF_8);
        } else {
            name = "";
        }

        return readTagPayload(type, name, depth);
    }

    private Tag readTagPayload(TagType type, String name, int depth) throws IOException {
        switch (type) {
            case TAG_END: {
                if (depth == 0) {
                    throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
                } else {
                    return new EndTag();
                }
            }
            case TAG_BYTE: return new ByteTag(name, is.readByte());
            case TAG_SHORT: return new ShortTag(name, is.readShort());
            case TAG_INT: return new IntTag(name, is.readInt());
            case TAG_LONG: return new LongTag(name, is.readLong());
            case TAG_FLOAT: return new FloatTag(name, is.readFloat());
            case TAG_DOUBLE: return new DoubleTag(name, is.readDouble());
            case TAG_BYTE_ARRAY: {
                int length = is.readInt();
                byte[] bytes = new byte[length];
                is.readFully(bytes);
                return new ByteArrayTag(name, bytes);
            }

            case TAG_STRING: {
                int length = is.readShort();
                byte[] bytes = new byte[length];
                is.readFully(bytes);
                return new StringTag(name, new String(bytes, StandardCharsets.UTF_8));
            }
            case TAG_LIST: {
                TagType childType = TagType.getById(is.readByte());
                int length = is.readInt();

                Class<? extends Tag> clazz = childType.getTagClass();
                List<Tag> tagList = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    Tag tag = readTagPayload(childType, "", depth + 1);
                    if (tag instanceof EndTag) {
                        throw new IOException("TAG_End not permitted in a list.");
                    } else {
                        if (!clazz.isInstance(tag)) {
                            throw new IOException("Mixed tag types within a list.");
                        }
                    }
                    tagList.add(tag);
                }
                return new ListTag(name, clazz, tagList);
            }
            case TAG_COMPOUND: {
                SortedMap<String, Tag<?>> compoundTagList = new TreeMap<>();
                while (true) {
                    Tag tag = readTag(depth + 1);
                    if (tag instanceof EndTag) {
                        break;
                    } else {
                        compoundTagList.put(name, tag);
                    }
                }
                return new CompoundTag(compoundTagList);
            }
            case TAG_INT_ARRAY: {
                int length = is.readInt();
                int[] ints = new int[length];
                for (int i = 0; i < length; i++) {
                    ints[i] = is.readInt();
                }
                return new IntArrayTag(name, ints);
            }
            case TAG_LONG_ARRAY: {
                int length = is.readInt();
                long[] longs = new long[length];
                for (int i = 0; i < length; i++) {
                    longs[i] = is.readLong();
                }
                return new LongArrayTag(name, longs);
            }
            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    public void close() throws IOException {
        is.close();
    }

    public ByteOrder getByteOrder() {
        return is.getEndianness();
    }
}
