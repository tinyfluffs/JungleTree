module org.jungletree.core {
    exports org.jungletree.core;
    exports org.jungletree.core.handler;
    exports org.jungletree.core.handler.handshake;
    exports org.jungletree.core.handler.status;
    exports org.jungletree.core.handler.login;
    exports org.jungletree.core.handler.play;

    provides org.jungletree.api.Server with org.jungletree.core.JungleServer;

    requires static lombok;

    requires org.jungletree.api;
    requires org.jungletree.net;

    requires org.apache.logging.log4j;
    requires org.json;
    requires tomlj;
    requires java.desktop;
    requires org.jungletree.entity;
    requires org.jungletree.world;
}
