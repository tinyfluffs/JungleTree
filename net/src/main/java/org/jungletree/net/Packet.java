package org.jungletree.net;

public interface Packet {

    @Override
    String toString();

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();
}
