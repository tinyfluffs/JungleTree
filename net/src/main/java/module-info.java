module org.jungletree.net {
    exports org.jungletree.net;
    exports org.jungletree.net.packet;
    exports org.jungletree.net.packet.status;
    exports org.jungletree.net.packet.handshake;
    exports org.jungletree.net.packet.login;
    exports org.jungletree.net.packet.play;
    exports org.jungletree.net.protocol;
    exports org.jungletree.net.exception;

    requires static lombok;

    requires org.apache.logging.log4j;
    requires org.jungletree.api;
    requires org.json;
    requires org.jungletree.world;
}
