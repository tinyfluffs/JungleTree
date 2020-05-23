package org.jungletree.net.service;

import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Codec;
import org.jungletree.net.Packet;
import org.jungletree.net.exception.UnknownPacketException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CodecLookupService {

    ConcurrentMap<Class<? extends Packet>, Codec.CodecRegistration> messages;
    ConcurrentMap<Integer, Codec<? extends Packet>> opcodes;
    Codec<? extends Packet>[] opcodeTable;

    public CodecLookupService(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be less than 0!");
        }
        messages = new ConcurrentHashMap<>();
        if (size == 0) {
            opcodes = new ConcurrentHashMap<>();
            opcodeTable = null;
        } else {
            opcodeTable = new Codec[size];
            opcodes = null;
        }
    }

    public <M extends Packet, C extends Codec<M>> Codec.CodecRegistration bind(Class<M> messageClazz, Class<C> codecClazz, int opcode) throws InstantiationException {
        Codec.CodecRegistration reg = messages.get(messageClazz);
        if (reg != null) {
            return reg;
        }
        Codec<M> codec;
        try {
            Constructor<C> con = codecClazz.getConstructor();
            con.setAccessible(true);
            codec = con.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException("Codec could not be created!", ex);
        }

        if (opcode < 0) {
            throw new IllegalArgumentException("Opcode must either be null or greater than or equal to 0!");
        }

        Codec<?> previous = get(opcode);
        if (previous != null && previous.getClass() != codecClazz) {
            throw new IllegalStateException("Trying to bind an opcode where one already exists. New: " + codecClazz.getSimpleName() + " Old: " + previous.getClass().getSimpleName());
        }
        put(opcode, codec);
        reg = new Codec.CodecRegistration(opcode, codec);
        messages.put(messageClazz, reg);
        return reg;
    }

    private Codec<?> get(int opcode) throws ArrayIndexOutOfBoundsException {
        if (opcodeTable != null && opcodes == null) {
            return opcodeTable[opcode];
        } else if (opcodes != null && opcodeTable == null) {
            return opcodes.get(opcode);
        } else {
            throw new IllegalStateException("One and only one codec storage system must be in use!");
        }
    }

    private void put(int opcode, Codec<?> codec) {
        if (opcodeTable != null && opcodes == null) {
            opcodeTable[opcode] = codec;
        } else if (opcodes != null && opcodeTable == null) {
            opcodes.put(opcode, codec);
        } else {
            throw new IllegalStateException("One and only one codec storage system must be in use!");
        }
    }

    public Codec<?> find(int id) throws UnknownPacketException {
        try {
            Codec<?> c = get(id);
            if (c == null) {
                log.error("No codec for packet 0x{}", Integer.toHexString(id));
                throw new UnknownPacketException(id);
            }
            return c;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
            log.error("No codec for packet 0x{} (OOB)", Integer.toHexString(id));
            throw new UnknownPacketException(id);
        }
    }

    public <M extends Packet> Codec.CodecRegistration find(Class<M> clazz) {
        return messages.get(clazz);
    }
}
