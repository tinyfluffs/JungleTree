package org.jungletree.api;

import org.jungletree.api.exception.StartupException;

public interface Server {
    
    void start() throws StartupException;

    String getName();
    void setName(String name);
    
    String getMotd();
    void setMotd(String motd);
    
    GameVersion getHighestSupportedGameVersion();

    GameVersion[] getSupportedGameVersions();
}
