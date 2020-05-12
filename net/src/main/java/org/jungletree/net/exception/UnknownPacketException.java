package org.jungletree.net.exception;

public class UnknownPacketException extends Exception {

    private final int opcode;
    private final int length;

    public UnknownPacketException(String message, int opcode, int length) {
        super(message);
        this.opcode = opcode;
        this.length = length;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getLength() {
        return length;
    }
}
