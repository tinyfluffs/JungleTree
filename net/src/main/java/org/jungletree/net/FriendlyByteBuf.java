package org.jungletree.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.api.nbt.CompoundTag;
import org.jungletree.api.nbt.NBTInputStream;
import org.jungletree.api.nbt.NBTOutputStream;
import org.jungletree.api.nbt.TagType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class FriendlyByteBuf extends ByteBuf {

    private static final int MAX_VARINT_SIZE = 5;
    private static final int MAX_VARLONG_SIZE = 10;

    private static final short MAX_STRING_LENGTH = Short.MAX_VALUE;

    private final ByteBuf source;

    public FriendlyByteBuf(ByteBuf source) {
        this.source = source;
    }

    public static int getVarIntSize(int value) {
        for (int i = 1; i < MAX_VARINT_SIZE; i++) {
            if ((value & (0xFFFFFFFF << (i * 7))) == 0) {
                return i;
            }
        }
        return MAX_VARINT_SIZE;
    }

    public FriendlyByteBuf writeByteArray(byte[] bytes) {
        writeVarInt(bytes.length);
        writeBytes(bytes);
        return this;
    }

    public byte[] readByteArray() {
        return readByteArray(readableBytes());
    }

    public byte[] readByteArray(int value) {
        int length = this.readVarInt();
        if (length > value) {
            throw new DecoderException("ByteArray with size " + length + " is bigger than allowed " + value);
        } else {
            byte[] result = new byte[length];
            this.readBytes(result);
            return result;
        }
    }

    public FriendlyByteBuf writeVarIntArray(int[] value) {
        this.writeVarInt(value.length);
        for (int val : value) {
            this.writeVarInt(val);
        }
        return this;
    }

    public int[] readVarIntArray() {
        return readVarIntArray(readableBytes());
    }

    public int[] readVarIntArray(int maxLength) {
        int length = this.readVarInt();
        if (length > maxLength) {
            throw new DecoderException("VarIntArray with size " + length + " is bigger than allowed " + maxLength);
        } else {
            int[] result = new int[length];
            for (int i = 0; i < result.length; i++) {
                result[i] = readVarInt();
            }
            return result;
        }
    }

    public FriendlyByteBuf writeLongArray(long[] value) {
        writeVarInt(value.length);
        for (long v : value) {
            this.writeLong(v);
        }
        return this;
    }

    public <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    public FriendlyByteBuf writeEnum(Enum<?> clazz) {
        return this.writeVarInt(clazz.ordinal());
    }

    public int readVarInt() {
        int out = 0;
        int bytes = 0;
        byte in;
        do {
            in = readByte();
            out |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > MAX_VARINT_SIZE) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((in & 0x80) == 0x80);
        return out;
    }

    public long readVarLong() {
        long out = 0;
        int bytes = 0;
        byte in;
        do {
            in = readByte();
            out |= (long) (in & 0x7F) << (bytes++ * 7);
            if (bytes > MAX_VARLONG_SIZE) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((in & 0x80) == 0x80);
        return out;
    }

    public FriendlyByteBuf writeUUID(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public FriendlyByteBuf writeVarInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeByte(value);
                return this;
            }
            writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    public FriendlyByteBuf writeVarLong(long value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeByte((int) value);
                return this;
            }
            writeByte(((int) (value & 0x7F)) | 0x80);
            value >>>= 7;
        }
    }

    public String readString() {
        return readString(Short.MAX_VALUE);
    }

    public String readString(int maxLength) {
        int length = readVarInt();
        if (length > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + maxLength * 4 + ")");
        } else {
            if (length < 0) {
                throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
            } else {
                String result = toString(readerIndex(), length, StandardCharsets.UTF_8);
                readerIndex(readerIndex() + length);
                if (result.length() > maxLength) {
                    throw new DecoderException("The received string length is longer than maximum allowed (" + length + " > " + maxLength + ")");
                } else {
                    return result;
                }
            }
        }
    }

    public FriendlyByteBuf writeString(String value) {
        return writeString(value, MAX_STRING_LENGTH);
    }

    public FriendlyByteBuf writeString(String value, int maxLength) {
        byte[] var3 = value.getBytes(StandardCharsets.UTF_8);
        if (var3.length > maxLength) {
            throw new EncoderException("String too big (was " + var3.length + " bytes encoded, max " + maxLength + ")");
        } else {
            this.writeVarInt(var3.length);
            this.writeBytes(var3);
            return this;
        }
    }

    public Date readDate() {
        return new Date(readLong());
    }

    public FriendlyByteBuf writeDate(Date date) {
        writeLong(date.getTime());
        return this;
    }

    public ChatMessage readChatMessage() {
        return ChatMessage.fromJson(readString());
    }

    public void writeChatMessage(ChatMessage value) {
        writeString(value.toJson().toString());
    }

    public CompoundTag readNbt() {
        int index = this.readerIndex();
        byte nextTag = this.readByte();
        if (nextTag == TagType.TAG_END.getId()) {
            return null;
        } else {
            this.readerIndex(index);

            try {
                return (CompoundTag) new NBTInputStream(new ByteBufInputStream(this)).readTag();
            } catch (IOException ex) {
                throw new DecoderException(ex);
            }
        }
    }

    public void writeNbt(CompoundTag tag) {
        if (tag == null) {
            this.writeByte(TagType.TAG_END.getId());
        } else {
            try {
                new NBTOutputStream(new ByteBufOutputStream(this)).writeTag(tag);
            } catch (IOException ex) {
                throw new EncoderException(ex);
            }
        }
    }

    public int capacity() {
        return this.source.capacity();
    }

    public ByteBuf capacity(int var1) {
        return this.source.capacity(var1);
    }

    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    public ByteOrder order() {
        return this.source.order();
    }

    public ByteBuf order(ByteOrder var1) {
        return this.source.order(var1);
    }

    public ByteBuf unwrap() {
        return this.source.unwrap();
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }

    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.source.asReadOnly();
    }

    public int readerIndex() {
        return this.source.readerIndex();
    }

    public ByteBuf readerIndex(int var1) {
        return this.source.readerIndex(var1);
    }

    public int writerIndex() {
        return this.source.writerIndex();
    }

    public ByteBuf writerIndex(int var1) {
        return this.source.writerIndex(var1);
    }

    public ByteBuf setIndex(int var1, int var2) {
        return this.source.setIndex(var1, var2);
    }

    public int readableBytes() {
        return this.source.readableBytes();
    }

    public int writableBytes() {
        return this.source.writableBytes();
    }

    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.source.isReadable();
    }

    public boolean isReadable(int var1) {
        return this.source.isReadable(var1);
    }

    public boolean isWritable() {
        return this.source.isWritable();
    }

    public boolean isWritable(int var1) {
        return this.source.isWritable(var1);
    }

    public ByteBuf clear() {
        return this.source.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.source.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.source.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.source.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.source.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.source.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.source.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int var1) {
        return this.source.ensureWritable(var1);
    }

    public int ensureWritable(int var1, boolean var2) {
        return this.source.ensureWritable(var1, var2);
    }

    public boolean getBoolean(int var1) {
        return this.source.getBoolean(var1);
    }

    public byte getByte(int var1) {
        return this.source.getByte(var1);
    }

    public short getUnsignedByte(int var1) {
        return this.source.getUnsignedByte(var1);
    }

    public short getShort(int var1) {
        return this.source.getShort(var1);
    }

    public short getShortLE(int var1) {
        return this.source.getShortLE(var1);
    }

    public int getUnsignedShort(int var1) {
        return this.source.getUnsignedShort(var1);
    }

    public int getUnsignedShortLE(int var1) {
        return this.source.getUnsignedShortLE(var1);
    }

    public int getMedium(int var1) {
        return this.source.getMedium(var1);
    }

    public int getMediumLE(int var1) {
        return this.source.getMediumLE(var1);
    }

    public int getUnsignedMedium(int var1) {
        return this.source.getUnsignedMedium(var1);
    }

    public int getUnsignedMediumLE(int var1) {
        return this.source.getUnsignedMediumLE(var1);
    }

    public int getInt(int var1) {
        return this.source.getInt(var1);
    }

    public int getIntLE(int var1) {
        return this.source.getIntLE(var1);
    }

    public long getUnsignedInt(int var1) {
        return this.source.getUnsignedInt(var1);
    }

    public long getUnsignedIntLE(int var1) {
        return this.source.getUnsignedIntLE(var1);
    }

    public long getLong(int var1) {
        return this.source.getLong(var1);
    }

    public long getLongLE(int var1) {
        return this.source.getLongLE(var1);
    }

    public char getChar(int var1) {
        return this.source.getChar(var1);
    }

    public float getFloat(int var1) {
        return this.source.getFloat(var1);
    }

    public double getDouble(int var1) {
        return this.source.getDouble(var1);
    }

    public ByteBuf getBytes(int var1, ByteBuf var2) {
        return this.source.getBytes(var1, var2);
    }

    public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
        return this.source.getBytes(var1, var2, var3);
    }

    public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
        return this.source.getBytes(var1, var2, var3, var4);
    }

    public ByteBuf getBytes(int var1, byte[] var2) {
        return this.source.getBytes(var1, var2);
    }

    public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
        return this.source.getBytes(var1, var2, var3, var4);
    }

    public ByteBuf getBytes(int var1, ByteBuffer var2) {
        return this.source.getBytes(var1, var2);
    }

    public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
        return this.source.getBytes(var1, var2, var3);
    }

    public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
        return this.source.getBytes(var1, var2, var3);
    }

    public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
        return this.source.getBytes(var1, var2, var3, var5);
    }

    public CharSequence getCharSequence(int var1, int var2, Charset var3) {
        return this.source.getCharSequence(var1, var2, var3);
    }

    public ByteBuf setBoolean(int var1, boolean var2) {
        return this.source.setBoolean(var1, var2);
    }

    public ByteBuf setByte(int var1, int var2) {
        return this.source.setByte(var1, var2);
    }

    public ByteBuf setShort(int var1, int var2) {
        return this.source.setShort(var1, var2);
    }

    public ByteBuf setShortLE(int var1, int var2) {
        return this.source.setShortLE(var1, var2);
    }

    public ByteBuf setMedium(int var1, int var2) {
        return this.source.setMedium(var1, var2);
    }

    public ByteBuf setMediumLE(int var1, int var2) {
        return this.source.setMediumLE(var1, var2);
    }

    public ByteBuf setInt(int var1, int var2) {
        return this.source.setInt(var1, var2);
    }

    public ByteBuf setIntLE(int var1, int var2) {
        return this.source.setIntLE(var1, var2);
    }

    public ByteBuf setLong(int var1, long var2) {
        return this.source.setLong(var1, var2);
    }

    public ByteBuf setLongLE(int var1, long var2) {
        return this.source.setLongLE(var1, var2);
    }

    public ByteBuf setChar(int var1, int var2) {
        return this.source.setChar(var1, var2);
    }

    public ByteBuf setFloat(int var1, float var2) {
        return this.source.setFloat(var1, var2);
    }

    public ByteBuf setDouble(int var1, double var2) {
        return this.source.setDouble(var1, var2);
    }

    public ByteBuf setBytes(int var1, ByteBuf var2) {
        return this.source.setBytes(var1, var2);
    }

    public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
        return this.source.setBytes(var1, var2, var3);
    }

    public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
        return this.source.setBytes(var1, var2, var3, var4);
    }

    public ByteBuf setBytes(int var1, byte[] var2) {
        return this.source.setBytes(var1, var2);
    }

    public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
        return this.source.setBytes(var1, var2, var3, var4);
    }

    public ByteBuf setBytes(int var1, ByteBuffer var2) {
        return this.source.setBytes(var1, var2);
    }

    public int setBytes(int var1, InputStream var2, int var3) throws IOException {
        return this.source.setBytes(var1, var2, var3);
    }

    public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
        return this.source.setBytes(var1, var2, var3);
    }

    public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
        return this.source.setBytes(var1, var2, var3, var5);
    }

    public ByteBuf setZero(int var1, int var2) {
        return this.source.setZero(var1, var2);
    }

    public int setCharSequence(int var1, CharSequence var2, Charset var3) {
        return this.source.setCharSequence(var1, var2, var3);
    }

    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    public byte readByte() {
        return this.source.readByte();
    }

    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    public short readShort() {
        return this.source.readShort();
    }

    public short readShortLE() {
        return this.source.readShortLE();
    }

    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.source.readMedium();
    }

    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.source.readInt();
    }

    public int readIntLE() {
        return this.source.readIntLE();
    }

    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    public long readLong() {
        return this.source.readLong();
    }

    public long readLongLE() {
        return this.source.readLongLE();
    }

    public char readChar() {
        return this.source.readChar();
    }

    public float readFloat() {
        return this.source.readFloat();
    }

    public double readDouble() {
        return this.source.readDouble();
    }

    public ByteBuf readBytes(int var1) {
        return this.source.readBytes(var1);
    }

    public ByteBuf readSlice(int var1) {
        return this.source.readSlice(var1);
    }

    public ByteBuf readRetainedSlice(int var1) {
        return this.source.readRetainedSlice(var1);
    }

    public ByteBuf readBytes(ByteBuf var1) {
        return this.source.readBytes(var1);
    }

    public ByteBuf readBytes(ByteBuf var1, int var2) {
        return this.source.readBytes(var1, var2);
    }

    public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
        return this.source.readBytes(var1, var2, var3);
    }

    public ByteBuf readBytes(byte[] var1) {
        return this.source.readBytes(var1);
    }

    public ByteBuf readBytes(byte[] var1, int var2, int var3) {
        return this.source.readBytes(var1, var2, var3);
    }

    public ByteBuf readBytes(ByteBuffer var1) {
        return this.source.readBytes(var1);
    }

    public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
        return this.source.readBytes(var1, var2);
    }

    public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
        return this.source.readBytes(var1, var2);
    }

    public CharSequence readCharSequence(int var1, Charset var2) {
        return this.source.readCharSequence(var1, var2);
    }

    public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
        return this.source.readBytes(var1, var2, var4);
    }

    public ByteBuf skipBytes(int var1) {
        return this.source.skipBytes(var1);
    }

    public ByteBuf writeBoolean(boolean var1) {
        return this.source.writeBoolean(var1);
    }

    public ByteBuf writeByte(int var1) {
        return this.source.writeByte(var1);
    }

    public ByteBuf writeShort(int var1) {
        return this.source.writeShort(var1);
    }

    public ByteBuf writeShortLE(int var1) {
        return this.source.writeShortLE(var1);
    }

    public ByteBuf writeMedium(int var1) {
        return this.source.writeMedium(var1);
    }

    public ByteBuf writeMediumLE(int var1) {
        return this.source.writeMediumLE(var1);
    }

    public ByteBuf writeInt(int var1) {
        return this.source.writeInt(var1);
    }

    public ByteBuf writeIntLE(int var1) {
        return this.source.writeIntLE(var1);
    }

    public ByteBuf writeLong(long var1) {
        return this.source.writeLong(var1);
    }

    public ByteBuf writeLongLE(long var1) {
        return this.source.writeLongLE(var1);
    }

    public ByteBuf writeChar(int var1) {
        return this.source.writeChar(var1);
    }

    public ByteBuf writeFloat(float var1) {
        return this.source.writeFloat(var1);
    }

    public ByteBuf writeDouble(double var1) {
        return this.source.writeDouble(var1);
    }

    public ByteBuf writeBytes(ByteBuf var1) {
        return this.source.writeBytes(var1);
    }

    public ByteBuf writeBytes(ByteBuf var1, int var2) {
        return this.source.writeBytes(var1, var2);
    }

    public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
        return this.source.writeBytes(var1, var2, var3);
    }

    public ByteBuf writeBytes(byte[] var1) {
        return this.source.writeBytes(var1);
    }

    public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
        return this.source.writeBytes(var1, var2, var3);
    }

    public ByteBuf writeBytes(ByteBuffer var1) {
        return this.source.writeBytes(var1);
    }

    public int writeBytes(InputStream var1, int var2) throws IOException {
        return this.source.writeBytes(var1, var2);
    }

    public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
        return this.source.writeBytes(var1, var2);
    }

    public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
        return this.source.writeBytes(var1, var2, var4);
    }

    public ByteBuf writeZero(int var1) {
        return this.source.writeZero(var1);
    }

    public int writeCharSequence(CharSequence var1, Charset var2) {
        return this.source.writeCharSequence(var1, var2);
    }

    public int indexOf(int var1, int var2, byte var3) {
        return this.source.indexOf(var1, var2, var3);
    }

    public int bytesBefore(byte var1) {
        return this.source.bytesBefore(var1);
    }

    public int bytesBefore(int var1, byte var2) {
        return this.source.bytesBefore(var1, var2);
    }

    public int bytesBefore(int var1, int var2, byte var3) {
        return this.source.bytesBefore(var1, var2, var3);
    }

    public int forEachByte(ByteProcessor var1) {
        return this.source.forEachByte(var1);
    }

    public int forEachByte(int var1, int var2, ByteProcessor var3) {
        return this.source.forEachByte(var1, var2, var3);
    }

    public int forEachByteDesc(ByteProcessor var1) {
        return this.source.forEachByteDesc(var1);
    }

    public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
        return this.source.forEachByteDesc(var1, var2, var3);
    }

    public ByteBuf copy() {
        return this.source.copy();
    }

    public ByteBuf copy(int var1, int var2) {
        return this.source.copy(var1, var2);
    }

    public ByteBuf slice() {
        return this.source.slice();
    }

    public ByteBuf retainedSlice() {
        return this.source.retainedSlice();
    }

    public ByteBuf slice(int var1, int var2) {
        return this.source.slice(var1, var2);
    }

    public ByteBuf retainedSlice(int var1, int var2) {
        return this.source.retainedSlice(var1, var2);
    }

    public ByteBuf duplicate() {
        return this.source.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.source.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    public ByteBuffer nioBuffer(int var1, int var2) {
        return this.source.nioBuffer(var1, var2);
    }

    public ByteBuffer internalNioBuffer(int var1, int var2) {
        return this.source.internalNioBuffer(var1, var2);
    }

    public ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int var1, int var2) {
        return this.source.nioBuffers(var1, var2);
    }

    public boolean hasArray() {
        return this.source.hasArray();
    }

    public byte[] array() {
        return this.source.array();
    }

    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    public String toString(Charset var1) {
        return this.source.toString(var1);
    }

    public String toString(int var1, int var2, Charset var3) {
        return this.source.toString(var1, var2, var3);
    }

    public int hashCode() {
        return this.source.hashCode();
    }

    public boolean equals(Object var1) {
        return this.source.equals(var1);
    }

    public int compareTo(ByteBuf var1) {
        return this.source.compareTo(var1);
    }

    public String toString() {
        return this.source.toString();
    }

    public ByteBuf retain(int var1) {
        return this.source.retain(var1);
    }

    public ByteBuf retain() {
        return this.source.retain();
    }

    public ByteBuf touch() {
        return this.source.touch();
    }

    public ByteBuf touch(Object var1) {
        return this.source.touch(var1);
    }

    public int refCnt() {
        return this.source.refCnt();
    }

    public boolean release() {
        return this.source.release();
    }

    public boolean release(int var1) {
        return this.source.release(var1);
    }
}
