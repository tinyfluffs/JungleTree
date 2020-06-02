package org.jungletree.net.packet.login;

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
public class LoginStartPacket implements Packet {

    String username;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeString(this.username);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.username = buf.readString();
    }
}
