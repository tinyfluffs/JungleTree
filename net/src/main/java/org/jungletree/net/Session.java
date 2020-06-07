package org.jungletree.net;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.api.Player;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.net.exception.ChannelClosedException;
import org.jungletree.net.packet.DisconnectPacket;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.play.KeepAlivePacket;
import org.jungletree.net.protocol.Protocol;
import org.jungletree.net.protocol.Protocols;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.jungletree.api.JungleTree.scheduler;
import static org.jungletree.api.JungleTree.server;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Session {

    private static final String SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";

    static final SecureRandom secRandom = new SecureRandom();

    final AtomicBoolean encryptionEnabled = new AtomicBoolean();
    final AtomicBoolean compressionEnabled = new AtomicBoolean();

    final AtomicBoolean online = new AtomicBoolean();
    final AtomicLong lastPing = new AtomicLong();
    final AtomicLong lastPong = new AtomicLong();

    @Getter final String sessionId = Long.toString(ThreadLocalRandom.current().nextLong(), 16).trim();
    @Getter final NetworkServer networkServer;
    @Getter final SocketChannel channel;
    AtomicReference<Protocol> protocol;
    @Getter @Setter String verifyUsername;
    @Getter byte[] verifyToken;
    @Getter Player player;
    Consumer<Player> callback;
    ScheduledFuture<?> keepAliveTask;
    @Getter @Setter private EncryptedBuffer encodeBuffer;
    @Getter @Setter private EncryptedBuffer decodeBuffer;

    public Session(NetworkServer networkServer, SocketChannel channel) {
        this.networkServer = networkServer;
        this.channel = channel;
        this.protocol = new AtomicReference<>(Protocols.HANDSHAKE.getProtocol());
    }

    public boolean isEncryptionEnabled() {
        return server().isEncryptionEnabled() && this.encryptionEnabled.get();
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled.set(encryptionEnabled);
    }

    public boolean isCompressionEnabled() {
        return this.compressionEnabled.get();
    }

    public boolean isOnline() {
        return this.online.get();
    }

    public void setOnline(boolean online) {
        this.online.set(online);
    }

    public void send(Packet pkt) throws ChannelClosedException {
        log.info("OUT: {}", pkt);
        sendFuture(pkt);
    }

    public void sendAll(Packet... pkts) throws ChannelClosedException {
        for (Packet pkt : pkts) {
            sendFuture(pkt);
        }
    }

    public void sendFuture(Packet pkt) throws ChannelClosedException {
        if (!channel.isOpen()) {
            throw new ChannelClosedException("Trying to send a message when a session is inactive!");
        }
        networkServer.send(this, pkt, this::onOutboundThrowable);
    }

    public InetSocketAddress getAddress() {
        try {
            var addr = channel.getRemoteAddress();
            if (!(addr instanceof InetSocketAddress)) {
                return null;
            }
            return (InetSocketAddress) addr;
        } catch (IOException ex) {
            log.warn("", ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void packetReceived(T packet) {
        Class<T> clazz = (Class<T>) packet.getClass();
        Handler<T> handler = protocol.get().getPacketHandler(clazz);
        if (handler == null) {
            return;
        }

        try {
            handler.handle(this, packet);
        } catch (Throwable t) {
            onHandlerThrowable(packet, handler, t);
        }
    }

    public Protocol getProtocol() {
        return protocol.get();
    }

    public void setProtocol(Protocol protocol) {
        this.protocol.set(protocol);
    }

    public void setPlayer(Player player, Consumer<Player> callback) {
        this.player = player;
        this.callback = callback;
    }

    public void disconnect() {
        try {
            channel.close();
            onDisconnect();
        } catch (IOException ignored) {}
    }

    public void disconnect(String reason) {
        disconnect(ChatMessage.builder().text(reason).build());
    }

    public void disconnect(ChatMessage reason) {
        send(new DisconnectPacket(reason));
        disconnect();
    }

    public void onConnect() {
    }

    public void onDisconnect() {
        log.error("Disconnected");
        if (keepAliveTask != null) {
            keepAliveTask.cancel(true);
        }
        if (callback != null) {
            callback.accept(player);
        }
    }

    public void onInboundThrowable(Throwable cause) {
        log.error("", cause);
    }

    public void onOutboundThrowable(Throwable cause) {
        log.error("", cause);
    }

    public <T extends Packet> void onHandlerThrowable(T pkt, Handler<T> handler, Throwable cause) {
        log.error("Error handling {} (handler: {})", pkt, handler.getClass().getSimpleName(), cause);
    }

    public byte[] generateVerifyToken() {
        var token = new byte[4];
        secRandom.nextBytes(token);
        this.verifyToken = token;
        return token;
    }

    public void enableEncryption(byte[] encodedSharedSecret, byte[] encodedVerifyToken, Consumer<String> callback) {
        Cipher cipher;
        var privateKey = this.networkServer.getPrivateKey();
        var storedVerifyUsername = this.verifyUsername;

        SecretKey sharedSecret;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            sharedSecret = new SecretKeySpec(cipher.doFinal(encodedSharedSecret), "AES");
        } catch (Exception ex) {
            log.warn("Failed to init shared secret:", ex);
            disconnect("Failed to init shared secret");
            return;
        }

        byte[] verifyToken;
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = cipher.doFinal(encodedVerifyToken);
        } catch (Exception ex) {
            log.warn("Bad key for client: username={}", storedVerifyUsername, ex);
            disconnect("Bad key");
            return;
        }

        if (!Arrays.equals(verifyToken, this.verifyToken)) {
            log.warn("Bad token for client: username={}", storedVerifyUsername);
            disconnect("Bad token");
            return;
        }

        try {
            this.encodeBuffer = new EncryptedBuffer(Cipher.ENCRYPT_MODE, sharedSecret);
            this.decodeBuffer = new EncryptedBuffer(Cipher.DECRYPT_MODE, sharedSecret);
            this.encryptionEnabled.set(true);
        } catch (GeneralSecurityException ex) {
            log.error("", ex);
            disconnect("Server security error.");
        }

        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(this.sessionId.getBytes());
            digest.update(sharedSecret.getEncoded());
            digest.update(this.networkServer.getPublicKey().getEncoded());

            hash = new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            log.error("", ex);
            disconnect("Hash failure");
            return;
        }

        scheduler("NETWORK").submit(() -> {
            try {
                URL url = new URL(String.format(SESSION_URL + "?username=%s&serverId=%s&ip=%s", storedVerifyUsername, hash, URLEncoder.encode(getAddress().getAddress().getHostAddress(), StandardCharsets.UTF_8)));
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder b = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    b.append(line);
                }
                reader.close();
                callback.accept(b.toString());
            } catch (Exception ex) {
                disconnect();
            }
        });
    }

    public void startKeepAlive() {
        if (keepAliveTask != null) {
            return;
        }
        keepAliveTask = scheduler("NETWORK").scheduleAtFixedRate(() -> {
            final long time = System.nanoTime();
            this.lastPing.set(time);
            send(new KeepAlivePacket(time));
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public long getLastPingNs() {
        return this.lastPing.get();
    }

    public long getLastPongNs() {
        return this.lastPong.get();
    }

    public void setLastPongNs(long ns) {
        this.lastPong.set(ns);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return sessionId.equals(session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Session.class.getSimpleName() + "[", "]")
                .add("sessionId='" + sessionId + "'")
                .toString();
    }
}
