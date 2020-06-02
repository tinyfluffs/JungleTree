package org.jungletree.net.packet;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.net.FriendlyByteBuf;
import org.jungletree.net.Packet;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisconnectPacket implements Packet {

    ChatMessage reason;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeChatMessage(this.reason);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.reason = buf.readChatMessage();
    }
}
