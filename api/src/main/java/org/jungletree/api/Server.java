package org.jungletree.api;

import org.json.JSONArray;
import org.jungletree.api.exception.StartupException;

import java.util.List;

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

    String getImplementationName();

    String getImplementationVersion();

    GameVersion getHighestSupportedGameVersion();

    GameVersion[] getSupportedGameVersions();

    List<Player> getOnlinePlayers();

    JSONArray getServerListSample();

    byte[] getFavicon();

    int getEncryptionKeySize();

    boolean isEncryptionEnabled();
}
