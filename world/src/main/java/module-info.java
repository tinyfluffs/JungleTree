module org.jungletree.world {
    exports org.jungletree.world.chunk;
    exports org.jungletree.world;

    requires static lombok;

    requires org.apache.logging.log4j;
    requires org.jungletree.api;

    provides org.jungletree.api.world.ChunkGenerator with org.jungletree.world.generator.CheckerboardChunkGenerator;
}
