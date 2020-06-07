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
public class StatusResponsePacket implements Packet {

    String json;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeString(this.json);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.json = buf.readString();
    }
}
