package org.jungletree.net.packet.login;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.net.ByteBuf;
import org.jungletree.net.Packet;

import java.util.UUID;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginSuccessPacket implements Packet {

    UUID uuid;
    String username;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeString(this.uuid.toString());
        buf.writeString(this.username);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.uuid = UUID.fromString(buf.readString());
        this.username = buf.readString();
    }
}
