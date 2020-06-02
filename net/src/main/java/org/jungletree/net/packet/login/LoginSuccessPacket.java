package org.jungletree.net.packet.login;

import lombok.*;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

import java.util.UUID;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessPacket implements Packet {

    UUID uuid;
    String username;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeString(this.uuid.toString());
        buf.writeString(this.username);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.uuid = UUID.fromString(buf.readString());
        this.username = buf.readString();
    }
}
