package org.jungletree.net.protocol;

import org.jungletree.net.http.HttpClient;

public enum Protocols {
    STATUS(new StatusProtocol()),
    LOGIN(new LoginProtocol(new HttpClient())),
    HANDSHAKE(new HandshakeProtocol()),
    PLAY(new PlayProtocol());
    
    private final Protocol protocol;
    
    Protocols(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol protocol() {
        return protocol;
    }
}
