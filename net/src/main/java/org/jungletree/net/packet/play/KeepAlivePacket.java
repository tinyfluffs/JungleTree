package org.jungletree.net.packet.play;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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
