package org.jungletree.net.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Codec;
import org.jungletree.net.Packet;
import org.jungletree.net.exception.UnknownPacketException;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.service.CodecLookupService;
import org.jungletree.net.service.HandlerLookupService;

import java.lang.reflect.InvocationTargetException;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Protocol {

    @Getter String name;

    CodecLookupService inboundCodecs;
    CodecLookupService outboundCodecs;
    HandlerLookupService handlers;

    public Protocol(String name, int highestOpcode) {
        this.name = name;
        this.inboundCodecs = new CodecLookupService(highestOpcode + 1);
        this.outboundCodecs = new CodecLookupService(highestOpcode + 1);
        this.handlers = new HandlerLookupService();
    }

    public <P extends Packet, H extends Handler<P>> void handler(Class<P> packet, Class<H> handler) {
        try {
            handlers.bind(packet, handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            log.error("Error registering inbound {}: protocol={}", inboundCodecs.find(packet).getOpcode(), getName(), ex);
        }
    }

    protected <P extends Packet, C extends Codec<P>> void inbound(int opcode, Class<P> packet, Class<C> codec) {
        try {
            inboundCodecs.bind(packet, codec, opcode);
        } catch (InstantiationException ex) {
            log.error("Error registering inbound packet {}: protocol={}", opcode, getName(), ex);
        }
    }

    protected <P extends Packet, C extends Codec<P>> void outbound(int opcode, Class<P> message, Class<C> codec) {
        try {
            outboundCodecs.bind(message, codec, opcode);
        } catch (InstantiationException ex) {
            log.error("Error registering inbound packet {}: protocol={}", opcode, getName(), ex);
        }
    }

    public <P extends Packet> Handler<P> getPacketHandler(Class<P> clazz) {
        Handler<P> handler = handlers.find(clazz);
        if (handler == null) {
            log.warn("No handler for packet {}: protocol={}", clazz.getSimpleName(), getName());
        }
        return handler;
    }

    public <M extends Packet> Codec.CodecRegistration getCodecRegistration(Class<M> clazz) {
        Codec.CodecRegistration reg = outboundCodecs.find(clazz);
        if (reg == null) {
            log.warn("No codec to write {}: protocol={}", clazz.getSimpleName(), getName());
        }
        return reg;
    }

    public Codec<?> find(int id) throws UnknownPacketException {
        return inboundCodecs.find(id);
    }
}
