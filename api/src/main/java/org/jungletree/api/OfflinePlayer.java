package org.jungletree.api;

import java.util.UUID;

public interface OfflinePlayer {
    UUID uuid();
    String username();
    String displayName();
    void displayName(String displayName);
}
