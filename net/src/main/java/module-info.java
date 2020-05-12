module org.jungletree.net {
    exports org.jungletree.net.session to org.jungletree.core;
    exports org.jungletree.net.packet to org.jungletree.core;
    exports org.jungletree.net.packet.status to org.jungletree.core;
    exports org.jungletree.net.protocol;

    requires org.jungletree.api;

    requires org.apache.logging.log4j;
    requires io.netty.all;

}
