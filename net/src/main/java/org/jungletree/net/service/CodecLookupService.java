package org.jungletree.net.service;

import org.jungletree.net.Codec;
import org.jungletree.net.Packet;
import org.jungletree.net.exception.IllegalOpcodeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CodecLookupService {

    private final ConcurrentMap<Class<? extends Packet>, Codec.CodecRegistration> messages;
    private final ConcurrentMap<Integer, Codec<? extends Packet>> opcodes;
    private final Codec<? extends Packet>[] opcodeTable;
    private final AtomicInteger nextId;

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
        nextId = new AtomicInteger(0);
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

        int id;
        try {
            do {
                id = nextId.getAndIncrement();
            } while (get(id) != null);
        } catch (IndexOutOfBoundsException ioobe) {
            throw new IllegalStateException("Ran out of Ids!", ioobe);
        }
        opcode = id;

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

    public Codec<?> find(int opcode) throws IllegalOpcodeException {
        try {
            Codec<?> c = get(opcode);
            if (c == null) {
                throw new NullPointerException();
            }
            return c;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            throw new IllegalOpcodeException("Opcode " + opcode + " is not bound!");
        }
    }

    public <M extends Packet> Codec.CodecRegistration find(Class<M> clazz) {
        return messages.get(clazz);
    }

    @Override
    public String toString() {
        return "CodecLookupService{" + "messages=" + messages + ", opcodes=" + opcodes + '}';
    }
}
