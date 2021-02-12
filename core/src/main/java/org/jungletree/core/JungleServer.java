package org.jungletree.core;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jungletree.api.GameVersion;
import org.jungletree.api.Player;
import org.jungletree.api.Server;
import org.jungletree.api.exception.StartupException;
import org.jungletree.api.player.ProfileItem;
import org.jungletree.core.handler.PacketHandlers;
import org.jungletree.net.NetworkServer;
import org.jungletree.net.Session;
import org.jungletree.net.packet.login.LoginSuccessPacket;
import org.jungletree.net.protocol.Protocols;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JungleServer implements Server {

    final Map<UUID, JunglePlayer> onlinePlayers;

    String host;
    int port;

    @Getter @Setter String name;
    @Getter @Setter String motd;

    boolean useEncryption;
    int keySize;

    @Getter @Setter int maxPlayers;
    @Getter @Setter int serverStatusPlayerSampleCount;
    @Getter @Setter byte[] favicon = new byte[0];

    NetworkServer networkServer;
    Executor networkExecutor;

    public JungleServer() {
        this.onlinePlayers = new ConcurrentHashMap<>();

        PacketHandlers.registerAll();
    }

    @Override
    public void start() throws StartupException {
        Path configFile = Paths.get("config.toml");
        if (!Files.exists(configFile)) {
            try {
                Files.write(configFile,
                        Objects.requireNonNull(Thread.currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("config.toml")).readAllBytes());
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
            this.maxPlayers = Math.toIntExact(config.getLong("server.max_players", () -> 10L));
            this.useEncryption = config.getBoolean("server.encryption.enabled", () -> true);
            this.keySize = Math.toIntExact(config.getLong("server.encryption.key_size", () -> 1024L));
            this.serverStatusPlayerSampleCount = Math.toIntExact(config.getLong("server.status.sample", () -> 10L));

            var favIconPath = Paths.get(config.getString("server.icon", () -> "favicon.png")).toFile();
            if (favIconPath.exists()) {
                var out = new ByteArrayOutputStream();
                var img = ImageIO.read(favIconPath);
                ImageIO.write(img, "png", out);
                this.favicon = out.toByteArray();
                out.close();
            }
        } catch (IOException ex) {
            throw new StartupException("Failed to read from configuration: ", ex);
        }


        this.networkServer = new NetworkServer();
        this.networkExecutor = Executors.newSingleThreadExecutor();
        this.networkExecutor.execute(() -> networkServer.bind(new InetSocketAddress(host, port)));
    }

    @Override
    public String getApiVersion() {
        return Versioning.getApiVersion();
    }

    @Override
    public String getImplementationName() {
        return "JungleTree";
    }

    @Override
    public String getImplementationVersion() {
        return Versioning.getImplementationVersion();
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
        return new GameVersion[]{GameVersion.VERSION_1_16_5};
    }

    @Override
    public List<Player> getOnlinePlayers() {
        var online = this.onlinePlayers.values();
        List<Player> result = new ArrayList<>(online.size());
        result.addAll(online);
        return Collections.unmodifiableList(result);
    }

    @Override
    public JSONArray getServerListSample() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        var result = new JSONArray();
        int sampleSize = getServerStatusPlayerSampleCount();
        var players = getOnlinePlayers();
        if (sampleSize >= players.size()) {
            sampleSize = players.size();
        } else {
            for (int i = 0; i < sampleSize; i++) {
                Collections.swap(players, i, rand.nextInt(i, players.size()));
            }
        }

        JSONObject p;
        for (int i = 0; i < sampleSize; i++) {
            p = new JSONObject();
            p.put("name", players.get(i).getUsername());
            p.put("id", players.get(i).getUuid().toString());
            result.put(p);
        }
        return result;
    }

    @Override
    public int getEncryptionKeySize() {
        return keySize;
    }

    @Override
    public boolean isEncryptionEnabled() {
        return useEncryption;
    }

    public void setPlayer(Session session, UUID uuid, String username, ProfileItem[] profile) {
        var player = new JunglePlayer(session, uuid, username, profile);
        for (Map.Entry<UUID, JunglePlayer> e : this.onlinePlayers.entrySet()) {
            if (e.getValue().getUuid().equals(uuid)) {
                e.getValue().getSession().disconnect("You logged in from another location.");
                break;
            }
        }
        player.onPreJoin(); // TODO: Events
        session.setOnline(true);

        session.send(new LoginSuccessPacket(player.getUuid(), player.getUsername()));
        session.setProtocol(Protocols.PLAY.getProtocol());
        this.onlinePlayers.put(uuid, player);

        session.setPlayer(player, p -> {
            log.info("{} disconnected", p.getUsername());
            this.onlinePlayers.remove(p.getUuid());
        });

        player.onJoin();
        // session.startKeepAlive();
    }
}
