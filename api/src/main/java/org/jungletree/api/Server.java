package org.jungletree.api;

public interface Server {
    String name();
    void name(String name);
    
    String motd();
    void motd(String motd);
    
    GameVersion getHighestSupportedGameVersion();

    GameVersion[] getSupportedGameVersions();
}
