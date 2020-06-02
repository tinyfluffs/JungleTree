module org.jungletree.api {
    exports org.jungletree.api;
    exports org.jungletree.api.chat;
    exports org.jungletree.api.entity;
    exports org.jungletree.api.exception;
    exports org.jungletree.api.nbt;
    exports org.jungletree.api.player;
    exports org.jungletree.api.util;
    exports org.jungletree.api.world;
    exports org.jungletree.api.world.biome;

    requires static lombok;

    requires org.json;
    requires java.desktop;

    uses org.jungletree.api.Server;
    uses org.jungletree.api.Scheduler;
    uses org.jungletree.api.world.ChunkGenerator;
}
