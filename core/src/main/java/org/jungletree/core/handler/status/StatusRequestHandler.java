package org.jungletree.core.handler.status;

import org.json.JSONObject;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.net.Session;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.status.StatusRequestPacket;
import org.jungletree.net.packet.status.StatusResponsePacket;

import java.util.Base64;

import static org.jungletree.api.JungleTree.server;

public class StatusRequestHandler implements Handler<StatusRequestPacket> {

    @Override
    public void handle(Session session, StatusRequestPacket pkt) {
        // TODO: Bukkit style events
        // TODO: Backwards compatibility with older protocol versions (inject here)

        var response = new JSONObject();
        response.put("version", server().getHighestSupportedGameVersion().toJson());

        var players = new JSONObject();
        players.put("max", server().getMaxPlayers());
        players.put("online", server().getOnlinePlayers().size());
        players.put("sample", server().getServerListSample());
        response.put("players", players);
        response.put("description", ChatMessage.builder().text(server().getMotd()).build().toJson());

        var favicon = server().getFavicon();
        if (favicon.length > 0) {
            response.put("favicon", "data:image/png;base64," + Base64.getEncoder().encodeToString(favicon));
        }

        session.send(new StatusResponsePacket(response.toString()));
    }
}
