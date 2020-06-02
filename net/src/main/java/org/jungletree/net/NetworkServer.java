package org.jungletree.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.api.JungleTree;
import org.jungletree.api.exception.StartupException;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;

import static org.jungletree.api.JungleTree.server;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NetworkServer {

    final ServerBootstrap bootstrap = new ServerBootstrap();
    final EventLoopGroup boss = NettyUtils.createBestEventLoopGroup();
    final EventLoopGroup worker = NettyUtils.createBestEventLoopGroup();
    final KeyPair keyPair;

    Channel channel;

    public NetworkServer() throws StartupException {
        this.keyPair = generateKeyPair();

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

    public Session newSession(Channel c) {
        return new Session(this, c);
    }

    public void sessionInactivated(Session session) {
        session.setOnline(false);
    }

    public void onBindSuccess(SocketAddress address) {
    }

    public void onBindFailure(SocketAddress address, Throwable cause) {
        log.error("", cause);
    }

    private KeyPair generateKeyPair() throws StartupException {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(server().getEncryptionKeySize());
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new StartupException("RSA unavailable: ", ex);
        }
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public byte[] getBrandData() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(0));
        var brand = JungleTree.server().getImplementationName().getBytes(StandardCharsets.UTF_8);
        buf.writeVarInt(brand.length);
        buf.writeBytes(brand);
        var result = new byte[buf.readableBytes()];
        buf.readBytes(result);
        buf.release();
        return result;
    }
}
