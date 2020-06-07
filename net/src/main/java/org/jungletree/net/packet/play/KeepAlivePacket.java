package org.jungletree.net.packet.play;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.net.ByteBuf;
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
    public void encode(ByteBuf buf) {
        buf.writeLong(id);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.id = buf.readLong();
    }
}
