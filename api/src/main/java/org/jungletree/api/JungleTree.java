package org.jungletree.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jungletree.api.world.ChunkGenerator;
import org.jungletree.api.world.BlockState;
import org.jungletree.api.world.Palette;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JungleTree {

    private static final Map<Class<?>, ServiceLoader<?>> services = new ConcurrentHashMap<>();
    private static final Palette globalPalette = createGlobalPalette();

    public static Server server() {
        return instance(Server.class);
    }

    public static Palette globalPalette() {
        return globalPalette;
    }

    public static Scheduler scheduler(String name) {
        name = name.toUpperCase();
        for (Scheduler sch : serviceLoader(Scheduler.class)) {
            if (sch.getName().equals(name)) {
                return sch;
            }
        }
        throw new NoSuchElementException("No chunk generator with the provided name " + name);
    }

    public static ChunkGenerator generator(String name) {
        name = name.toUpperCase();
        for (ChunkGenerator gen : serviceLoader(ChunkGenerator.class)) {
            if (gen.getName().equals(name)) {
                return gen;
            }
        }
        throw new NoSuchElementException("No chunk generator with the provided name " + name);
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

    private static Palette createGlobalPalette() {
        var p = "minecraft:";
        return new Palette(
                new BlockState(p + "stone"),
                new BlockState(p + "dirt"),
                new BlockState(p + "grass"),
                new BlockState(p + "glass")
        );
    }
}
