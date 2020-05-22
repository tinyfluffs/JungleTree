module org.jungletree.net {
    exports org.jungletree.net to org.jungletree.core;
    exports org.jungletree.net.packet to org.jungletree.core;
    exports org.jungletree.net.packet.status to org.jungletree.core;
    exports org.jungletree.net.packet.handshake to org.jungletree.core;
    exports org.jungletree.net.packet.login to org.jungletree.core;
    exports org.jungletree.net.protocol to org.jungletree.core;
    exports org.jungletree.net.exception;

    requires static lombok;

    requires org.apache.logging.log4j;
    requires io.netty.transport;
    requires io.netty.buffer;
    requires io.netty.handler;
    requires io.netty.resolver;
    requires io.netty.resolver.dns;
    requires io.netty.codec.http;
    requires io.netty.transport.epoll;
    requires io.netty.transport.kqueue;
    requires io.netty.codec;
    requires io.netty.common;
    requires org.jungletree.api;
    requires org.json;
}
