module org.jungletree.core {
    exports org.jungletree.core to org.jungletree.startup;
    exports org.jungletree.core.handler to org.jungletree.net;
    exports org.jungletree.core.handler.handshake to org.jungletree.net;
    exports org.jungletree.core.handler.status to org.jungletree.net;
    exports org.jungletree.core.handler.login to org.jungletree.net;
    exports org.jungletree.core.handler.play to org.jungletree.net;

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
