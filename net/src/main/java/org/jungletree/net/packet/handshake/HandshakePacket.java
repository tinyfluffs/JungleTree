package org.jungletree.net.packet.handshake;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.net.Packet;
import org.jungletree.net.protocol.Protocols;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HandshakePacket implements Packet {

    int protocolVersion;
    String address;
    int port;
    Protocols nextState;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeVarInt(this.protocolVersion);
        buf.writeString(this.address);
        buf.writeShort(this.port);
        buf.writeVarInt(this.nextState.getId());
    }

    @Override
    public void decode(ByteBuf buf) {
        this.protocolVersion = buf.readVarInt();
        this.address = buf.readString();
        this.port = buf.readShort();
        this.nextState = Protocols.fromId(buf.readVarInt());
    }
}
