package org.jungletree.net;

public interface Packet {

    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);
}
