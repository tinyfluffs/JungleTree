package org.jungletree.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.net.exception.ChannelClosedException;
import org.jungletree.net.http.HttpCallback;
import org.jungletree.net.packet.DisconnectPacket;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.pipeline.CodecHandler;
import org.jungletree.net.pipeline.EncryptionHandler;
import org.jungletree.net.protocol.LoginProtocol;
import org.jungletree.net.protocol.Protocol;
import org.jungletree.net.protocol.Protocols;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Session implements Comparable<Session> {

    private static final String SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";

    static final SecureRandom secRandom = new SecureRandom();

    final AtomicBoolean online = new AtomicBoolean();
    final AtomicBoolean disconnected = new AtomicBoolean();

    @Getter final String sessionId = Long.toString(ThreadLocalRandom.current().nextLong(), 16).trim();
    @Getter final NetworkServer networkServer;
    @Getter final Channel channel;
    @Getter Protocol protocol;
    @Getter @Setter String verifyUsername;
    @Getter byte[] verifyToken;

    public Session(NetworkServer networkServer, Channel channel) {
        this.networkServer = networkServer;
        this.channel = channel;
        this.protocol = Protocols.HANDSHAKE.getProtocol();
    }

    public boolean isOnline() {
        return this.online.get();
    }

    public void setOnline(boolean online) {
        this.online.set(online);
    }

    public void send(Packet pkt) throws ChannelClosedException {
        sendFuture(pkt);
    }

    public void sendAll(Packet... pkts) throws ChannelClosedException {
        for (Packet pkt : pkts) {
            sendFuture(pkt);
        }
    }

    public ChannelFuture sendFuture(Packet pkt) throws ChannelClosedException {
        if (!channel.isActive()) {
            throw new ChannelClosedException("Trying to send a message when a session is inactive!");
        }
        return channel.writeAndFlush(pkt).addListener(future -> {
            if (future.cause() != null) {
                onOutboundThrowable(future.cause());
            }
        });
    }

    public InetSocketAddress getAddress() {
        var addr = channel.remoteAddress();
        if (!(addr instanceof InetSocketAddress)) {
            return null;
        }
        return (InetSocketAddress) addr;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void packetReceived(T packet) {
        Class<T> clazz = (Class<T>) packet.getClass();
        Handler<T> handler = protocol.getPacketHandler(clazz);
        if (handler == null) {
            return;
        }

        try {
            handler.handle(this, packet);
        } catch (Throwable t) {
            onHandlerThrowable(packet, handler, t);
        }
    }

    public void setProtocol(Protocol protocol) {
        this.channel.flush();
        updatePipeline("codecs", new CodecHandler(protocol));
        this.protocol = protocol;
    }

    public void updatePipeline(String key, ChannelHandler handler) {
        this.channel.pipeline().replace(key, key, handler);
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public void disconnect() {
        channel.close();
    }

    public void disconnect(String reason) {
        disconnect(ChatMessage.builder().text(reason).build());
    }

    public void disconnect(ChatMessage reason) {
        send(DisconnectPacket.builder().reason(reason).build());
        channel.flush();
        channel.close();
    }

    public void onConnect() {
    }

    public void onDisconnect() {
        this.disconnected.set(true);
    }

    public void onInboundThrowable(Throwable cause) {
    }

    public void onOutboundThrowable(Throwable cause) {
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

    public void enableEncryption(Session session, byte[] sharedSecret, byte[] encryptedVerifyToken, HttpCallback callback) {
        Cipher cipher;
        var privateKey = networkServer.getPrivateKey();
        var storedVerifyUsername = session.getVerifyUsername();
        var storedVerifyToken = session.getVerifyToken();

        SecretKey sharedKey;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            sharedKey = new SecretKeySpec(cipher.doFinal(sharedSecret), "AES");
        } catch (Exception ex) {
            log.warn("Failed to enable encryption cipher for client: ", ex);
            session.disconnect();
            return;
        }

        byte[] verifyToken;
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            verifyToken = cipher.doFinal(encryptedVerifyToken);
        } catch (Exception ex) {
            log.warn("Bad key for client: username={}", storedVerifyUsername, ex);
            session.disconnect();
            return;
        }

        if (!Arrays.equals(verifyToken, storedVerifyToken)) {
            log.warn("Bad token for client: username={}", storedVerifyUsername);
            session.disconnect();
            return;
        }

        try {
            session.updatePipeline("encryption", new EncryptionHandler(sharedKey));
        } catch (GeneralSecurityException ex) {
            log.error(ex);
            session.disconnect();
            return;
        }

        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(session.getSessionId().getBytes());
            digest.update(sharedKey.getEncoded());
            digest.update(networkServer.getPublicKey().getEncoded());

            hash = new BigInteger(digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex);
            session.disconnect();
            return;
        }

        String url = String.format(SESSION_URL + "?username=%s&serverId=%s&ip=%s", storedVerifyUsername, hash, URLEncoder.encode(session.getAddress().getAddress().getHostAddress(), StandardCharsets.UTF_8));
        if (!(session.getProtocol() instanceof LoginProtocol)) {
            session.disconnect();
            log.warn("Login protocol not used when we haven't finished validation");
            return;
        }

        ((LoginProtocol) session.getProtocol()).getHttpClient().connect(url, session.getChannel().eventLoop(), callback);
    }

    @Override
    public int compareTo(Session o) {
        if (o == null) {
            return -1;
        }
        // Natural sort
        return sessionId.compareTo(o.sessionId);
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
