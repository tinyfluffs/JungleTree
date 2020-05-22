package org.jungletree.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.exception.StartupException;

import java.net.SocketAddress;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static org.jungletree.api.JungleTree.server;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class NetworkServer {

    final ServerBootstrap bootstrap = new ServerBootstrap();
    final EventLoopGroup boss = NettyUtils.createBestEventLoopGroup();
    final EventLoopGroup worker = NettyUtils.createBestEventLoopGroup();

    KeyPair keyPair;
    Channel channel;

    public NetworkServer() throws StartupException {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(server().getEncryptionKeySize());
            this.keyPair = gen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new StartupException("RSA unavailable: ", ex);
        }

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

    public void onBindFailure(SocketAddress address, Throwable t) {
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    protected PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    // TODO: Find a new home
    private Key genKey(Key base) {
        try {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(base.getEncoded());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(ks);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
