package org.jungletree.net.packet.login;

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
public class LoginStartPacket implements Packet {

    String username;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeString(this.username);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.username = buf.readString();
    }
}
