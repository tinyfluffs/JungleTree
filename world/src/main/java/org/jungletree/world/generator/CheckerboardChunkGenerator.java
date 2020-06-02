package org.jungletree.world.generator;

import org.jungletree.api.world.*;

import static org.jungletree.api.JungleTree.globalPalette;

public class CheckerboardChunkGenerator implements ChunkGenerator {

    @Override
    public String getName() {
        return "CHECKERBOARD";
    }

    @Override
    public void generate(Chunk c, int cx, int cz) {
        ChunkSection floorSection = c.getSection(0);
        Palette palette = globalPalette();
        char paletteId = palette.getState(palette.fromName("minecraft:stone"));

        for (int x=0; x<World.CHUNK_SECTION_WIDTH; x++) {
            for (int z=0; z<World.CHUNK_SECTION_DEPTH; z++) {
                floorSection.setBlockAt(x, 0, z, paletteId);
            }
        }
    }
}
