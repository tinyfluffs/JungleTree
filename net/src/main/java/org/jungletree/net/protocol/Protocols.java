package org.jungletree.net.protocol;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jungletree.net.http.HttpClient;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Protocols {
    STATUS(new StatusProtocol()),
    LOGIN(new LoginProtocol(new HttpClient())),
    HANDSHAKE(new HandshakeProtocol()),
    PLAY(new PlayProtocol());
    
    @Getter Protocol protocol;
}
