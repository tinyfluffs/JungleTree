package org.jungletree.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JungleTree {

    private static final Map<Class<?>, ServiceLoader<?>> services = new ConcurrentHashMap<>();

    public static Server server() {
        return instance(Server.class);
    }

    private static <T> T instance(Class<T> clazz) {
        var it = serviceLoader(clazz).iterator();
        if (!it.hasNext()) {
            throw new NoSuchElementException("No SPI implementation for " + Server.class.getSimpleName());
        }
        return it.next();
    }

    private static <T> ServiceLoader<T> serviceLoader(Class<T> clazz) {
        var serviceLoader = services.get(clazz);
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(clazz);
            services.put(clazz, serviceLoader);
        }
        return (ServiceLoader<T>) serviceLoader;
    }
}
