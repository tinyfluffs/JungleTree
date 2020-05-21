package org.jungletree.core.handler.status;

import lombok.Builder;
import lombok.Value;
import org.jungletree.api.GameVersion;
import org.jungletree.api.chat.ChatMessage;

import java.util.UUID;

@Value
@Builder
public class StatusResponse {
    GameVersion version;
    Players players;
    ChatMessage description;
    // String favicon;

    @Value
    @Builder
    public static class Players {
        int max;
        int online;
        ServerListPlayer[] sample;
    }

    @Value
    @Builder
    public static class ServerListPlayer {
        UUID id;
        String name;
    }
}
