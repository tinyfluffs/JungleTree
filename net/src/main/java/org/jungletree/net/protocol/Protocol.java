package org.jungletree.net.protocol;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.Packet;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.service.HandlerLookupService;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Protocol {

    @Getter String name;

    Map<Integer, Class<? extends Packet>> inbound;
    Map<Integer, Class<? extends Packet>> outbound;
    HandlerLookupService handlers;

    public Protocol(String name) {
        this.name = name;
        this.inbound = new ConcurrentHashMap<>();
        this.outbound = new ConcurrentHashMap<>();
        this.handlers = new HandlerLookupService();
    }

    public <P extends Packet, H extends Handler<P>> void handler(Class<P> packet, Class<H> handler) {
        try {
            handlers.bind(packet, handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            log.error("Error registering inbound handler {} for packet {}: protocol={}",
                    handler.getSimpleName(),
                    packet.getSimpleName(),
                    getName(),
                    ex);
        }
    }

    protected <P extends Packet> void inbound(int packetId, Class<P> packet) {
        if (this.inbound.containsKey(packetId)) {
            log.error("Inbound packet already registered: {}, claimed by: {}",
                    packetId,
                    this.inbound.get(packetId).getSimpleName());
            return;
        }
        try {
            packet.getConstructor();
        } catch (NoSuchMethodException e) {
            log.error("No default constructor for packet: {}", packet.getSimpleName());
            return;
        }
        this.inbound.put(packetId, packet);
    }

    protected <P extends Packet> void outbound(int packetId, Class<P> packet) {
        if (this.outbound.containsKey(packetId)) {
            log.error("Inbound packet already registered: {}, claimed by: {}",
                    packetId,
                    this.outbound.get(packetId).getSimpleName());
            return;
        }
        try {
            packet.getConstructor();
        } catch (NoSuchMethodException e) {
            log.error("No default constructor for packet: {}", packet.getSimpleName());
            return;
        }
        this.outbound.put(packetId, packet);
    }

    public <P extends Packet> Handler<P> getPacketHandler(Class<P> clazz) {
        Handler<P> handler = handlers.find(clazz);
        if (handler == null) {
            log.warn("No handler for packet {}: protocol={}", clazz.getSimpleName(), getName());
        }
        return handler;
    }

    public Packet find(int id) {
        Class<? extends Packet> clazz = this.inbound.entrySet()
                .stream()
                .filter(e -> e.getKey() == id)
                .findFirst()
                .orElseThrow(() -> new DecoderException("Unknown packet id: " + id + "."))
                .getValue();

        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            log.error("Error creating new instance of packet: {}", clazz.getSimpleName());
            throw new RuntimeException(ex);
        }
    }

    public int getPacketId(Class<? extends Packet> packet) {
        return this.outbound.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(packet))
                .findFirst()
                .orElseThrow(() -> new EncoderException("Unknown packet type: " + packet + "."))
                .getKey();
    }
}
