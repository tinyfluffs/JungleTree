package org.jungletree.api;

import org.json.JSONArray;
import org.jungletree.api.exception.StartupException;

public interface Server {
    
    void start() throws StartupException;

    String getName();
    void setName(String name);
    
    String getMotd();
    void setMotd(String motd);
    
    int getMaxPlayers();
    void setMaxPlayers(int maxPlayers);
    
    int getServerStatusPlayerSampleCount();
    void setServerStatusPlayerSampleCount(int count);
    
    String getApiVersion();
    String getImplementationVersion();

    GameVersion getHighestSupportedGameVersion();

    GameVersion[] getSupportedGameVersions();

    Player[] getOnlinePlayers();

    JSONArray getServerListSample();

    byte[] getFavicon();
}
