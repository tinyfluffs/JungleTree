package org.jungletree.net.packet;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.api.net.ByteBuf;
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
    public void encode(ByteBuf buf) {
        buf.writeChatMessage(this.reason);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.reason = buf.readChatMessage();
    }
}
