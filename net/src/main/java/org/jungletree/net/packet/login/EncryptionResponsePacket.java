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
public class EncryptionResponsePacket implements Packet {

    byte[] sharedSecret;
    byte[] verifyToken;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByteArray(this.sharedSecret);
        buf.writeByteArray(this.verifyToken);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sharedSecret = buf.readByteArray();
        this.verifyToken = buf.readByteArray();
    }
}
