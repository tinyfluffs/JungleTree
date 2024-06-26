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
public class EncryptionRequestPacket implements Packet {

    String sessionId;
    byte[] publicKey;
    byte[] verifyToken;

    @Override
    public void encode(ByteBuf buf) {
        buf.writeString(this.sessionId);
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.verifyToken);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.sessionId = buf.readString();
        this.publicKey = buf.readByteArray();
        this.verifyToken = buf.readByteArray();
    }
}
