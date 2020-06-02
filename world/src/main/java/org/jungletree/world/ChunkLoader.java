package org.jungletree.world;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jungletree.world.chunk.JungleChunk;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChunkLoader {

    JungleWorld world;
    Map<Integer, JungleChunk> loaded;

    public ChunkLoader(JungleWorld world) {
        this.world = world;
        this.loaded = new ConcurrentHashMap<>();
    }

    public JungleChunk getOrGenerate(int cx, int cz) {
        int hash = Objects.hash(cx, cz);
        if (loaded.containsKey(hash)) {
            return loaded.get(hash);
        } else {
            var chunk = new JungleChunk(cx, cz, world.getHeight());
            world.getGenerator().generate(chunk, cx, cz);
            loaded.put(hash, chunk);
            return chunk;
        }
    }
}
