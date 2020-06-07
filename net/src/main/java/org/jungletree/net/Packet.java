package org.jungletree.net;

import org.jungletree.api.net.ByteBuf;

public interface Packet {

    void encode(ByteBuf buf);

    void decode(ByteBuf buf);
}
