package org.jungletree.net.protocol;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Protocols {
    HANDSHAKE(0, new HandshakeProtocol()),
    STATUS(1, new StatusProtocol()),
    LOGIN(2, new LoginProtocol()),
    PLAY(3, new PlayProtocol());

    int id;
    Protocol protocol;

    public static Protocols fromId(int id) {
        for (var p : values()) {
            if (p.id == id) {
                return p;
            }
        }
        log.warn("No protocol found: id={}", id);
        return HANDSHAKE;
    }
}
