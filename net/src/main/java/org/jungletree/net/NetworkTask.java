package org.jungletree.net;

import lombok.extern.log4j.Log4j2;
import org.jungletree.api.JungleTree;
import org.jungletree.api.Scheduler;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.api.net.DecoderException;
import org.jungletree.net.protocol.Protocols;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Log4j2
public class NetworkTask implements Runnable {

    private static final int MTU = 1500;
    private static final int READ_IDLE_TIMEOUT = 20;
    private static final int WRITE_IDLE_TIMEOUT = 15;

    private final NetworkServer networkServer;
    private final Scheduler scheduler;
    private final Map<SocketChannel, Session> sessions;

    private Selector selector;
    private ServerSocketChannel serverChannel;

    public NetworkTask(NetworkServer networkServer) {
        this.networkServer = networkServer;
        this.scheduler = JungleTree.scheduler("NETWORK");
        this.sessions = new ConcurrentHashMap<>();
    }

    public void bind(SocketAddress address) throws IOException {
        selector = Selector.open();
        serverChannel = selector.provider().openServerSocketChannel();
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverChannel.bind(address, 1000);

        run();
    }

    public void disconnect(SocketChannel channel) {
        Session session = this.sessions.remove(channel);
        if (session != null) {
            session.onDisconnect();
        }
    }

    public void shutdown() {
        if (serverChannel != null && serverChannel.isOpen()) {
            try {
                serverChannel.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void run() {
        int keys;
        Iterator<SelectionKey> it;
        SelectionKey key;

        while (serverChannel.isOpen()) {
            try {
                keys = selector.selectNow();
                if (keys == 0) {
                    continue;
                }
                it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    key = it.next();
                    try {
                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isAcceptable()) {
                            accept(key);
                        }

                        if (key.isReadable()) {
                            try {
                                read(key);
                            } catch (RuntimeException ex) {
                                log.error("", ex);
                            }
                        }
                    } catch (CancelledKeyException ignored) {
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    it.remove();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        if (channel == null) {
            return;
        }

        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        try {
            channel.setOption(StandardSocketOptions.IP_TOS, 0x18);
        } catch (IOException ignored) {
            log.warn("Runtime OS does not support IP_TOS");
        }

        Session session = this.sessions.get(channel);
        if (session != null) {
            log.error("Session already open");
            return;
        }

        log.error("New session");
        session = new Session(networkServer, channel);
        session.setProtocol(Protocols.HANDSHAKE.getProtocol());
        session.onConnect();
        log.error(channel.getRemoteAddress().toString());
        this.sessions.put(channel, session);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        Session session = this.sessions.get(channel);
        if (session == null) {
            log.error("Session null");
            return;
        }

        if (!channel.isOpen()) {
            log.error("Channel closed");
            sessions.remove(channel);
            session.onDisconnect();
            return;
        }

        ByteBuffer bb = ByteBuffer.allocate(MTU);

        int bytesRead = channel.read(bb);
        if (bytesRead <= 0) {
            return;
        }
        read(session, bb, bytesRead);
    }

    public void read(Session session, ByteBuffer bb, int bytesRead) {
        ByteBuffer backing = ByteBuffer.allocateDirect(bytesRead);
        backing.put(bb.slice(0, bytesRead));
        backing.flip();
        ByteBuf buf = new ByteBuf(backing);

        if (session.isEncryptionEnabled()) {
            buf.setSource(session.getDecodeBuffer().crypt(buf.getSource()));
        }

        if (legacyRead(buf)) {
            log.error("Legacy read");
            return;
        }

        buf.mark();
        int readableLength = buf.varIntReadableLength();
        if (readableLength <= 0) {
            return;
        }

        int length = buf.readVarInt();
        if (buf.remaining() < length) {
            throw new DecoderException(String.format("packet too short: expected=%d, actual=%d", length, buf.remaining()));
        }

        buf.setSource(buf.getSource().slice(readableLength, length));

        if (session.isCompressionEnabled()) {
            // TODO: Decompress
        }

        session.packetReceived(decode(session, buf));
    }

    public void send(Session session, Packet packet, Consumer<Throwable> callback) {
        try {
            scheduler.submit(() -> {
                try {
                    ByteBuf buf = new ByteBuf(ByteBuffer.allocate(MTU - 5));
                    int id = session.getProtocol().getPacketId(packet.getClass());
                    buf.writeVarInt(id);
                    packet.encode(buf);

                    if (session.isCompressionEnabled()) {
                        // TODO: Compress
                    }

                    ByteBuf framed = new ByteBuf(ByteBuffer.allocate(MTU));
                    framed.writeVarInt(buf.position());
                    framed.write(buf.getSource().slice(0, buf.position()));

                    ByteBuffer result = ByteBuffer.allocate(framed.position());
                    result.put(framed.getSource().slice(0, framed.position()));
                    result.flip();

                    if (session.isEncryptionEnabled()) {
                        result = session.getEncodeBuffer().crypt(result);
                    }

                    log.error("send protocol: {}, id: {}, class: {}", session.getProtocol().getName(), id, packet.getClass().getSimpleName());
                    session.getChannel().write(result);
                } catch (Exception ex) {
                    callback.accept(ex);
                }
            }).get(WRITE_IDLE_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException ex) {
            callback.accept(ex);
        }
    }

    private Packet decode(Session session, ByteBuf buf) {
        int id = buf.readVarInt();
        Packet packet = session.getProtocol().find(id);
        log.error("recv protocol: {}, id: {}, class: {}", session.getProtocol().getName(), id, packet.getClass().getSimpleName());
        packet.decode(buf);
        return packet;
    }

    private boolean legacyRead(ByteBuf buf) {
        int pos = buf.position();
        if (buf.readByte() == (byte) 0xFE) {
            return true;
        }
        buf.position(pos);
        return false;
    }
}
