package org.jungletree.net.packet.play;

import lombok.*;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KeepAlivePacket implements Packet {

    long id;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(id);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.id = buf.readLong();
    }
}
