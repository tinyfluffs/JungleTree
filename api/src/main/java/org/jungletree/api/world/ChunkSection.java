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
    long getBlockAt(int x, int y, int z);

    void setBlockAt(int x, int y, int z, long state);

    boolean isEmpty();

    void recalculateEmpty();

    /**
     * Fill the section with the given palette element
     *
     * @param state palette id to fill with
     */
    void fill(long state);

    default void fill(BlockState blockState) {
        fill(globalPalette().getState(blockState));
    }
}
