package org.jungletree.net.packet.login;

import lombok.Builder;
import lombok.Value;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.net.Packet;

@Value
@Builder
public class DisconnectPacket implements Packet {
    ChatMessage reason;
}
