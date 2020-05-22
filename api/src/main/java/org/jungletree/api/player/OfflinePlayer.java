package org.jungletree.api.player;

import org.json.JSONObject;

import java.util.UUID;

public interface OfflinePlayer {
    UUID getUuid();
    String getUsername();
    String getDisplayName();
    void setDisplayName(String displayName);
    JSONObject getProfile();
    String getTexture();
}
