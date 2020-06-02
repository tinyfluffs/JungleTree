package org.jungletree.net.packet.login;

import lombok.*;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionResponsePacket implements Packet {

    byte[] sharedSecret;
    byte[] verifyToken;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeByteArray(this.sharedSecret);
        buf.writeByteArray(this.verifyToken);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.sharedSecret = buf.readByteArray();
        this.verifyToken = buf.readByteArray();
    }
}
