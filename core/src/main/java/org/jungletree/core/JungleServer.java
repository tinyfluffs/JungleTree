package org.jungletree.core;

import org.jungletree.api.GameVersion;
import org.jungletree.api.Server;
import org.jungletree.core.exception.StartupException;
import org.jungletree.net.NetworkServer;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JungleServer implements Server {

    private final String name;
    private final String motd;

    private final String host;
    private final int port;

    private final NetworkServer netServ;
    private final Executor networkExecutor;

    public JungleServer() throws StartupException {
        Path configFile = Paths.get("config.toml");
        if (!Files.exists(configFile)) {
            try {
                Files.write(configFile, Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.toml")).readAllBytes());
            } catch (IOException ex) {
                throw new StartupException("Failed to write new configuration: ", ex);
            }
        }

        TomlParseResult config;
        try {
            config = Toml.parse(configFile);
            this.name = config.getString("name", () -> "JungleTree");
            this.motd = config.getString("motd", () -> "A JungleTree Server");
            this.host = config.getString("server.host", () -> "127.0.0.1");
            this.port = Math.toIntExact(config.getLong("server.port", () -> 25565L));
        } catch (IOException ex) {
            throw new StartupException("Failed to read from configuration: ", ex);
        }

        this.netServ = new NetworkServer();
        this.networkExecutor = Executors.newSingleThreadExecutor();
        this.networkExecutor.execute(() -> {
            netServ.bind(new InetSocketAddress(host, port));
        });
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
