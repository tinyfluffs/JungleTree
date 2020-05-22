module org.jungletree.api {
    exports org.jungletree.api;
    exports org.jungletree.api.chat;
    exports org.jungletree.api.entity;
    exports org.jungletree.api.exception;
    exports org.jungletree.api.util;
    exports org.jungletree.api.player;
    exports org.jungletree.api.world;

    requires static lombok;

    requires org.json;

    uses org.jungletree.api.Server;
}
