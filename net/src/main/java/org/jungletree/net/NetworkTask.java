package org.jungletree.net;

import lombok.extern.log4j.Log4j2;
import org.jungletree.api.JungleTree;
import org.jungletree.api.Scheduler;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.api.net.DecoderException;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
        this.sessions = new HashMap<>();
    }

    public void bind(SocketAddress address) throws IOException {
        selector = Selector.open();
        serverChannel = selector.provider().openServerSocketChannel();
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverChannel.bind(address, 1000);
        run();
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
        ByteBuffer bb = ByteBuffer.allocate(MTU);
        ByteBuf buf = new ByteBuf(null);
        SocketChannel channel;
        Session session;
        int keys;
        int bytesRead;
        int readableLength;
        int length;
        Iterator<SelectionKey> it;
        SelectionKey key;

        while (serverChannel.isOpen()) {
            try {
                keys = selector.select(TimeUnit.SECONDS.toMillis(READ_IDLE_TIMEOUT));
                if (keys == 0) {
                    continue;
                }
                it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    key = it.next();
                    try {
                        if (!key.isValid()) {
                            it.remove();
                            continue;
                        }

                        if (key.isAcceptable()) {
                            channel = serverChannel.accept();
                            if (channel == null) {
                                it.remove();
                                continue;
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

                            session = new Session(networkServer, channel);
                            this.sessions.put(channel, session);
                            session.onConnect();
                        } else if (key.isReadable()) {
                            try {
                                channel = (SocketChannel) key.channel();
                                session = this.sessions.get(channel);
                                if (session == null) {
                                    it.remove();
                                    throw new DecoderException("session is null!");
                                }

                                bytesRead = channel.read(bb);
                                if (bytesRead <= 0) {
                                    it.remove();
                                    continue;
                                }

                                buf.setSource(bb.slice(0, bytesRead));

                                if (session.isEncryptionEnabled()) {
                                    buf.setSource(session.getDecodeBuffer().crypt(buf.getSource()));
                                }

                                if (legacyRead(buf)) {
                                    it.remove();
                                    continue;
                                }

                                buf.mark();
                                readableLength = buf.varIntReadableLength();
                                if (readableLength <= 0) {
                                    it.remove();
                                    continue;
                                }
                                length = buf.readVarInt();
                                if (buf.remaining() < length) {
                                    it.remove();
                                    throw new DecoderException(String.format("packet too short: expected=%d, actual=%d", length, buf.remaining()));
                                }
                                buf.setSource(buf.getSource().slice(readableLength, length));

                                if (session.isCompressionEnabled()) {
                                    // TODO: Decompress
                                }

                                session.packetReceived(decode(session, buf));
                            } catch (RuntimeException ex) {
                                log.error("", ex);
                            } finally {
                                bb.clear();
                                buf.setSource(null);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                Set<SocketChannel> clients = Set.copyOf(sessions.keySet());
                for (SocketChannel c : clients) {
                    if (!c.isOpen()) {
                        session = this.sessions.remove(c);
                        if (session != null) {
                            session.onDisconnect();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void send(Session session, Packet packet, Consumer<Throwable> callback) {
        try {
            scheduler.submit(() -> {
                try {
                    if (!session.getChannel().isOpen()) {
                        session.onDisconnect();
                        session.disconnect();
                        sessions.remove(session.getChannel());
                        return;
                    }

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
