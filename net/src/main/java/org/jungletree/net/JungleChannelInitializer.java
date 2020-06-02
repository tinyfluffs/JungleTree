package org.jungletree.net;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.net.pipeline.*;
import org.jungletree.net.protocol.Protocols;

@Log4j2
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JungleChannelInitializer extends ChannelInitializer<SocketChannel> {

    static int READ_IDLE_TIMEOUT = 20;
    static int WRITE_IDLE_TIMEOUT = 15;

    NetworkServer networkServer;

    @Override
    protected final void initChannel(SocketChannel c) {
        PacketHandler handler = new PacketHandler(networkServer);
        PacketCodecHandler codecs = new PacketCodecHandler(Protocols.HANDSHAKE.getProtocol());
        FramingHandler framing = new FramingHandler();

        try {
            c.config().setOption(ChannelOption.IP_TOS, 0x18);
        } catch (ChannelException ex) {
            log.warn("Runtime OS does not support IP_TOS");
        }

        c.pipeline()
                .addLast("timeout", new IdleStateHandler(READ_IDLE_TIMEOUT, WRITE_IDLE_TIMEOUT, 0))
                .addLast("legacy_query", new LegacyPingHandler())
                .addLast("encryption", NoOpHandler.INSTANCE)
                .addLast("framing", framing)
                .addLast("compression", NoOpHandler.INSTANCE)
                .addLast("codecs", codecs)
                .addLast("handler", handler);
    }
}
