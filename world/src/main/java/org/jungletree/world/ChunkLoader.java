package org.jungletree.world;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.world.ChunkPos;
import org.jungletree.world.chunk.JungleChunk;

import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChunkLoader {

    JungleWorld world;
    SortedMap<ChunkPos, JungleChunk> loaded;

    public ChunkLoader(JungleWorld world) {
        this.world = world;
        this.loaded = new ConcurrentSkipListMap<>();
    }

    public JungleChunk getOrGenerate(ChunkPos pos) {
        if (loaded.containsKey(pos)) {
            return loaded.get(pos);
        } else {
            var chunk = new JungleChunk(world.getHeight());
            world.getGenerator().generate(chunk, pos);
            loaded.put(pos, chunk);
            return chunk;
        }
    }
}
