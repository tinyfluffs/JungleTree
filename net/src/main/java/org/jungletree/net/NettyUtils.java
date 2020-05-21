package org.jungletree.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.kqueue.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NettyUtils {

    public static final boolean EPOLL_AVAILABLE = Epoll.isAvailable();
    public static final boolean KQUEUE_AVAILABLE = KQueue.isAvailable();

    public static EventLoopGroup createBestEventLoopGroup() {
        if (EPOLL_AVAILABLE) {
            return new EpollEventLoopGroup();
        } else if (KQUEUE_AVAILABLE) {
            return new KQueueEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    public static Class<? extends ServerSocketChannel> bestServerSocketChannel() {
        if (EPOLL_AVAILABLE) {
            return EpollServerSocketChannel.class;
        } else if (KQUEUE_AVAILABLE) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public static Class<? extends SocketChannel> bestSocketChannel() {
        if (EPOLL_AVAILABLE) {
            return EpollSocketChannel.class;
        } else if (KQUEUE_AVAILABLE) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static Class<? extends DatagramChannel> bestDatagramChannel() {
        if (EPOLL_AVAILABLE) {
            return EpollDatagramChannel.class;
        } else if (KQUEUE_AVAILABLE) {
            return KQueueDatagramChannel.class;
        } else {
            return NioDatagramChannel.class;
        }
    }
}
