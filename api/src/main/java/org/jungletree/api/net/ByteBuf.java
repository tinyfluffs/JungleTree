package org.jungletree.api.net;

import org.jungletree.api.chat.ChatMessage;
import org.jungletree.api.nbt.CompoundTag;
import org.jungletree.api.nbt.NBTInputStream;
import org.jungletree.api.nbt.NBTOutputStream;
import org.jungletree.api.nbt.TagType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public final class ByteBuf {

    private static final int MAX_VARINT_SIZE = 5;
    private static final int MAX_VARLONG_SIZE = 10;

    private static final short MAX_STRING_LENGTH = Short.MAX_VALUE;

    private ByteBuffer source;

    public ByteBuf() {
        this.source = ByteBuffer.allocateDirect(0);
    }

    public ByteBuf(int capacity) {
        this.source = ByteBuffer.allocate(capacity);
    }

    public ByteBuf(ByteBuffer source) {
        this.source = source;
    }

    public ByteBuffer getSource() {
        return source;
    }

    public void setSource(ByteBuffer source) {
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

    public ByteBuf writeByteArray(byte[] bytes) {
        writeVarInt(bytes.length);
        writeBytes(bytes);
        return this;
    }

    public byte[] readByteArray() {
        return readByteArray(source.remaining());
    }

    public byte[] readByteArray(int value) {
        int length = readVarInt();
        if (length > value) {
            throw new DecoderException("ByteArray with size " + length + " is bigger than allowed " + value);
        } else {
            byte[] result = new byte[length];
            readBytes(result);
            return result;
        }
    }

    public ByteBuf writeVarIntArray(int[] value) {
        writeVarInt(value.length);
        for (int val : value) {
            writeVarInt(val);
        }
        return this;
    }

    public int[] readVarIntArray() {
        return readVarIntArray(source.remaining());
    }

    public int[] readVarIntArray(int maxLength) {
        int length = readVarInt();
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

    public ByteBuf writeLongArray(long[] value) {
        writeVarInt(value.length);
        for (long v : value) {
            writeLong(v);
        }
        return this;
    }

    public <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    public ByteBuf writeEnum(Enum<?> clazz) {
        return writeVarInt(clazz.ordinal());
    }

    public int varIntReadableLength() {
        int size = 0;
        int idx = source.position();
        byte in;
        do {
            if (source.remaining() < 1) {
                source.position(idx);
                return 0;
            }
            size++;
            in = source.get();
        } while ((in & 0x80) != 0);

        source.position(idx);
        return size;
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

    public ByteBuf writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public ByteBuf writeVarInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeByte(value);
                return this;
            }
            writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    public ByteBuf writeVarLong(long value) {
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
                String result = toString(source.position(), length, StandardCharsets.UTF_8);
                source.position(source.position() + length);
                if (result.length() > maxLength) {
                    throw new DecoderException("The received string length is longer than maximum allowed (" + length + " > " + maxLength + ")");
                } else {
                    return result;
                }
            }
        }
    }

    public ByteBuf writeString(String value) {
        return writeString(value, MAX_STRING_LENGTH);
    }

    public ByteBuf writeString(String value, int maxLength) {
        byte[] var3 = value.getBytes(StandardCharsets.UTF_8);
        if (var3.length > maxLength) {
            throw new EncoderException("String too big (was " + var3.length + " bytes encoded, max " + maxLength + ")");
        } else {
            writeVarInt(var3.length);
            writeBytes(var3);
            return this;
        }
    }

    public Date readDate() {
        return new Date(readLong());
    }

    public ByteBuf writeDate(Date date) {
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
        int index = source.position();
        byte nextTag = readByte();
        if (nextTag == TagType.TAG_END.getId()) {
            return null;
        } else {
            source.position(index);

            try {
                return (CompoundTag) new NBTInputStream(Channels.newInputStream(new ByteBufferChannel(this))).readTag();
            } catch (IOException ex) {
                throw new DecoderException(ex);
            }
        }
    }

    public void writeNbt(CompoundTag tag) {
        if (tag == null) {
            writeByte(TagType.TAG_END.getId());
        } else {
            try {
                new NBTOutputStream(Channels.newOutputStream(new ByteBufferChannel(this))).writeTag(tag);
            } catch (IOException ex) {
                throw new EncoderException(ex);
            }
        }
    }

    public int remaining() {
        return source.remaining();
    }

    public int position() {
        return source.position();
    }

    public void position(int pos) {
        source.position(pos);
    }

    public ByteBuf mark() {
        source.mark();
        return this;
    }

    public ByteBuf reset() {
        source.reset();
        return this;
    }

    public ByteBuf slice() {
        return new ByteBuf(source.slice());
    }

    public ByteBuf slice(int index, int length) {
        return new ByteBuf(source.slice(index, length));
    }

    public ByteBuf duplicate() {
        return new ByteBuf(source.duplicate());
    }

    public ByteBuf asReadOnlyBuffer() {
        return new ByteBuf(source.asReadOnlyBuffer());
    }

    public boolean isReadOnly() {
        return source.isReadOnly();
    }

    public boolean isDirect() {
        return source.isDirect();
    }

    public int read(ByteBuf dst) {
        return dst.write(this.source);
    }

    public int read(ByteBuffer dst) {
        int index = source.position();
        dst.put(this.source);
        return dst.position() - index;
    }

    public int write(ByteBuffer buf) {
        int index = source.position();
        source.put(buf);
        return source.position() - index;
    }

    public int readBytes(byte[] dest) {
        int index = source.position();
        source.get(dest);
        return source.position() - index;
    }

    public ByteBuf writeBytes(byte[] val) {
        source.put(val);
        return this;
    }

    public ByteBuf writeBytes(int index, byte[] val) {
        source.put(index, val);
        return this;
    }

    public byte readByte() {
        return source.get();
    }

    public byte readByte(int index) {
        return source.get(index);
    }

    public ByteBuf writeByte(int b) {
        source.put((byte) b);
        return this;
    }

    public ByteBuf writeByte(int index, int b) {
        source.put(index, (byte) b);
        return this;
    }

    public boolean readBoolean() {
        return source.get() == 0 ? false : true;
    }

    public boolean readBoolean(int index) {
        return source.get(index) == 0 ? false : true;
    }

    public ByteBuf writeBoolean(boolean val) {
        return writeByte(val ? 0x01 : 0x00);
    }

    public ByteBuf writeBoolean(int index, boolean val) {
        return writeByte(index, val ? 0x01 : 0x00);
    }

    public char readChar() {
        return source.getChar();
    }

    public char readChar(int index) {
        return source.getChar(index);
    }

    public ByteBuffer writeChar(char value) {
        return source.putChar(value);
    }

    public ByteBuf writeChar(int index, char value) {
        source.putChar(index, value);
        return this;
    }

    public short readShort() {
        return source.getShort();
    }

    public short readShort(int index) {
        return source.getShort(index);
    }

    public ByteBuf writeShort(int value) {
        source.putShort((short) value);
        return this;
    }

    public ByteBuf writeShort(int index, int value) {
        source.putShort(index, (short) value);
        return this;
    }

    public int readInt() {
        return source.getInt();
    }

    public int readInt(int index) {
        return source.getInt(index);
    }

    public ByteBuf writeInt(int value) {
        source.putInt(value);
        return this;
    }

    public ByteBuf writeInt(int index, int value) {
        source.putInt(index, value);
        return this;
    }

    public long readLong() {
        return source.getLong();
    }

    public long readLong(int index) {
        return source.getLong(index);
    }

    public ByteBuf writeLong(long value) {
        source.putLong(value);
        return this;
    }

    public ByteBuf writeLong(int index, long value) {
        source.putLong(index, value);
        return this;
    }

    public float readFloat() {
        return source.getFloat();
    }

    public float readFloat(int index) {
        return source.getFloat(index);
    }

    public ByteBuf writeFloat(float value) {
        source.putFloat(value);
        return this;
    }

    public ByteBuf writeFloat(int index, float value) {
        source.putFloat(index, value);
        return this;
    }

    public double readDouble() {
        return source.getDouble();
    }

    public double readDouble(int index) {
        return source.getDouble(index);
    }

    public ByteBuf writeDouble(double value) {
        source.putDouble(value);
        return this;
    }

    public ByteBuf writeDouble(int index, double value) {
        source.putDouble(index, value);
        return this;
    }

    public String toString(int index, int length, Charset charset) {
        byte[] val = new byte[length];
        source.get(index, val);
        return new String(val, charset);
    }
}
