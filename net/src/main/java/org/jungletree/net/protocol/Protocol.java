package org.jungletree.net.protocol;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jungletree.net.ByteBufUtils;
import org.jungletree.net.Codec;
import org.jungletree.net.Packet;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.exception.IllegalOpcodeException;
import org.jungletree.net.service.CodecLookupService;
import org.jungletree.net.service.HandlerLookupService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class Protocol {

    private static final Logger log = LogManager.getLogger(Protocol.class);

    private final String name;

    private final CodecLookupService inboundCodecs;
    private final CodecLookupService outboundCodecs;
    private final HandlerLookupService handlers;

    public Protocol(String name, int highestOpcode) {
        this.name = name;
        this.inboundCodecs = new CodecLookupService(highestOpcode + 1);
        this.outboundCodecs = new CodecLookupService(highestOpcode + 1);
        this.handlers = new HandlerLookupService();
    }

    public String getName() {
        return name;
    }

    public <P extends Packet, H extends Handler<P>> void handler(Class<P> packet, Class<H> handler) {
        try {
            handlers.bind(packet, handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            log.error("Error registering inbound {} in {}", inboundCodecs.find(packet).getOpcode(), getName(), ex);
        }
    }

    protected <P extends Packet, C extends Codec<P>> void inbound(int opcode, Class<P> packet, Class<C> codec) {
        try {
            inboundCodecs.bind(packet, codec, opcode);
        } catch (InstantiationException ex) {
            log.error("Error registering inbound {} in {}", opcode, getName(), ex);
        }
    }

    protected <P extends Packet, C extends Codec<P>> void outbound(int opcode, Class<P> message, Class<C> codec) {
        try {
            outboundCodecs.bind(message, codec, opcode);
        } catch (InstantiationException ex) {
            log.error("Error registering inbound {} in {}", opcode, getName(), ex);
        }
    }

    public <P extends Packet> Handler<P> getPacketHandler(Class<P> clazz) {
        Handler<P> handler = handlers.find(clazz);
        if (handler == null) {
            log.warn("No message handler for:  {} in {}", clazz.getSimpleName(), getName());
        }
        return handler;
    }
    public <M extends Packet> Codec.CodecRegistration getCodecRegistration(Class<M> clazz) {
        Codec.CodecRegistration reg = outboundCodecs.find(clazz);
        if (reg == null) {
            log.warn("No codec to write: {} in {}", clazz.getSimpleName(), getName());
        }
        return reg;
    }

    public Codec<?> readHeader(ByteBuf in) throws IllegalOpcodeException, IOException {
        int opcode = ByteBufUtils.readVarInt(in);
        return inboundCodecs.find(opcode);
    }
}
