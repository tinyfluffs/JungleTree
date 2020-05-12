package org.jungletree.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.jungletree.net.session.Session;

import java.net.SocketAddress;

public class NetworkServer implements ConnectionManager {

    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup boss = new NioEventLoopGroup();
    private final EventLoopGroup worker = new NioEventLoopGroup();

    private Channel channel;

    public NetworkServer() {
        bootstrap.group(boss, worker)
                .channel(NettyUtils.bestServerSocketChannel())
                .childHandler(new JungleChannelInitializer(this))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

    public Channel bind(final SocketAddress address) {
        ChannelFuture future = bootstrap.bind(address).addListener(f -> {
            if (f.isSuccess()) {
                onBindSuccess(address);
            } else {
                onBindFailure(address, f.cause());
            }
        });
        channel = future.channel();
        return channel;
    }

    @Override
    public void shutdown() {
        if (channel != null) {
            channel.close();
        }
        worker.shutdownGracefully();
        boss.shutdownGracefully();

        try {
            boss.terminationFuture().sync();
            worker.terminationFuture().sync();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public Session newSession(Channel c) {
        return null;
    }

    @Override
    public void sessionInactivated(Session session) {
    }

    public void onBindSuccess(SocketAddress address) {
    }

    public void onBindFailure(SocketAddress address, Throwable t) {
    }
}
