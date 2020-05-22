package org.jungletree.api.player;

import org.jungletree.api.chat.ChatMessage;

import java.util.UUID;

public interface OfflinePlayer {
    UUID getUuid();

    String getUsername();

    ProfileItem[] getProfile();

    String getTexture();

    ChatMessage getDisplayName();

    void setDisplayName(ChatMessage displayName);

    default boolean isOnline() {
        return false;
    }
}
