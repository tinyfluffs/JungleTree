package org.jungletree.net.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnknownPacketException extends Exception {

    int packetId;
    int length;

    public UnknownPacketException(String message, int packetId, int length) {
        super(message);
        this.packetId = packetId;
        this.length = length;
    }
    
    public UnknownPacketException(int id) {
        super("Unknown packet id: " + id);
        this.packetId = id;
        this.length = 0;
    }

    public int getPacketId() {
        return packetId;
    }

    public int getLength() {
        return length;
    }
}
