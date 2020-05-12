package org.jungletree.net;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jungletree.net.pipeline.PacketHandler;
import org.jungletree.net.pipeline.*;
import org.jungletree.net.protocol.Protocols;

public class JungleChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger log = LogManager.getLogger(JungleChannelInitializer.class);

    private static final int READ_IDLE_TIMEOUT = 20;
    private static final int WRITE_IDLE_TIMEOUT = 15;

    private final ConnectionManager connectionManager;

    public JungleChannelInitializer(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected final void initChannel(SocketChannel c) {
        PacketHandler handler = new PacketHandler(connectionManager);
        CodecHandler codecs = new CodecHandler(Protocols.HANDSHAKE.protocol());
        FramingHandler framing = new FramingHandler();

        try {
            c.config().setOption(ChannelOption.IP_TOS, 0x18);
        } catch (ChannelException ex) {
            log.warn("Runtime OS does not support IP_TOS");
        }

        c.pipeline()
                .addLast("idle_timeout", new IdleStateHandler(READ_IDLE_TIMEOUT, WRITE_IDLE_TIMEOUT, 0))
                .addLast("legacy_ping", new LegacyPingHandler())
                .addLast("encryption", NoOpHandler.INSTANCE)
                .addLast("framing", framing).addLast("compression", NoOpHandler.INSTANCE)
                .addLast("codecs", codecs).addLast("handler", handler);
    }
}
