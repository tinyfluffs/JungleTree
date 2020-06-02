package org.jungletree.net.packet.status;

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
public class StatusResponsePacket implements Packet {

    String json;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeString(this.json);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.json = buf.readString();
    }
}
