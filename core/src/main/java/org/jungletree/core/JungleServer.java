package org.jungletree.core;

import org.jungletree.api.GameVersion;
import org.jungletree.api.Server;
import org.jungletree.core.exception.StartupException;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class JungleServer implements Server {

    private final String name;
    private final String motd;

    public JungleServer() throws StartupException {
        var configFile = Paths.get("config.toml");
        if (!Files.exists(configFile)) {
            try {
                Files.write(configFile, Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.toml")).readAllBytes());
            } catch (IOException ex) {
                throw new StartupException("Failed to write new configuration: ", ex);
            }
        }
        try {
            TomlParseResult result = Toml.parse(configFile);
            this.name = result.getString("name");
            this.motd = result.getString("motd");
        } catch (IOException ex) {
            throw new StartupException("Failed to read from configuration: ", ex);
        }
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public void name(String name) {
    }

    @Override
    public String motd() {
        return null;
    }

    @Override
    public void motd(String motd) {
    }

    @Override
    public GameVersion getHighestSupportedGameVersion() {
        GameVersion result = null;
        int highest = 0;
        
        for (GameVersion v : getSupportedGameVersions()) {
            if (v.getProtocol() > highest) {
                result = v;
            }
        }
        return result;
    }

    @Override
    public GameVersion[] getSupportedGameVersions() {
        return new GameVersion[] { GameVersion.VERSION_1_15_2 };
    }
}
