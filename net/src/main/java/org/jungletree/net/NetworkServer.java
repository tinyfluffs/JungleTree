package org.jungletree.net;

import lombok.extern.log4j.Log4j2;
import org.jungletree.api.exception.StartupException;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.*;
import java.util.function.Consumer;

import static org.jungletree.api.JungleTree.server;

@Log4j2
public class NetworkServer {

    private final KeyPair keyPair;
    private final NetworkTask networkTask;

    public NetworkServer() throws StartupException {
        this.keyPair = generateKeyPair();
        this.networkTask = new NetworkTask(this);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void bind(final SocketAddress address) {
        try {
            networkTask.bind(address);
        } catch (IOException ex) {
            log.error("Failed to bind to {}", address.toString(), ex);
            throw new RuntimeException(ex);
        }
    }

    public void shutdown() {
        networkTask.shutdown();
    }

    public void send(Session session, Packet packet, Consumer<Throwable> callback) {
        networkTask.send(session, packet, callback);
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
}
