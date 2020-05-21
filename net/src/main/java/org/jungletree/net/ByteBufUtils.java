package org.jungletree.net;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
}
