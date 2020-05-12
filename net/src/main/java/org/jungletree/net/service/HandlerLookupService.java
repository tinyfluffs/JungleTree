package org.jungletree.net.service;

import org.jungletree.net.Packet;
import org.jungletree.net.packet.Handler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HandlerLookupService {

    private final Map<Class<? extends Packet>, Handler<? extends Packet>> handlers = new HashMap<>();

    public <M extends Packet, H extends Handler<M>> void bind(Class<M> clazz, Class<H> handlerClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Handler<? super M> handler = handlerClass.getDeclaredConstructor().newInstance();
        bind(clazz, handler);
    }

    public <M extends Packet> void bind(Class<M> clazz, Handler<? super M> handler) {
        handlers.put(clazz, handler);
    }

    @SuppressWarnings("unchecked")
    public <M extends Packet> Handler<M> find(Class<M> clazz) {
        return (Handler<M>) handlers.get(clazz);
    }

    @Override
    public String toString() {
        return "HandlerLookupService{" + "handlers=" + handlers + '}';
    }

}
