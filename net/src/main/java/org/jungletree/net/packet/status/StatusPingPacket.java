package org.jungletree.net.packet.status;

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
public class StatusPingPacket implements Packet {

    long time;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(this.time);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.time = buf.readLong();
    }
}
