package org.jungletree.api.world;

import static org.jungletree.api.JungleTree.globalPalette;

public interface ChunkSection {

    /**
     * Obtain the palette id of the block at the provided index
     *
     * @param x index inside the section
     * @param y index inside the section
     * @param z index inside the section
     * @return palette id of the block at the given coords
     */
    char getPaletteIdAt(int x, int y, int z);

    void setPaletteIdAt(int x, int y, int z, char paletteId);

    boolean isEmpty();

    void recalculateEmpty();

    /**
     * Fill the section with the given palette element
     *
     * @param paletteId palette id to fill with
     */
    void fill(char paletteId);

    default void fill(Material material) {
        fill(globalPalette().getId(material));
    }
}
