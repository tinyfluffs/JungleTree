package org.jungletree.core.handler.login;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jungletree.api.player.ProfileItem;
import org.jungletree.api.util.UUIDs;
import org.jungletree.core.JungleServer;
import org.jungletree.net.Session;
import org.jungletree.net.http.HttpCallback;
import org.jungletree.net.packet.Handler;
import org.jungletree.net.packet.login.EncryptionResponsePacket;

import java.util.UUID;

import static org.jungletree.api.JungleTree.server;

@Log4j2
public class EncryptionResponseHandler implements Handler<EncryptionResponsePacket> {

    @Override
    public void handle(Session session, EncryptionResponsePacket pkt) {
        session.enableEncryption(pkt.getSharedSecret(), pkt.getVerifyToken(), new ClientAuthCallback(session));
    }

    @AllArgsConstructor
    private static class ClientAuthCallback implements HttpCallback {
        private final Session session;

        @Override
        public void done(String response) {
            JSONObject json = new JSONObject(response);

            UUID uuid = UUIDs.fromFlatString(json.getString("id"));
            String username = json.getString("name");

            JSONArray props = json.getJSONArray("properties");
            ProfileItem[] properties = new ProfileItem[props.length()];

            JSONObject o;
            for (int i = 0; i < properties.length; i++) {
                o = props.getJSONObject(i);
                properties[i] = ProfileItem.builder()
                        .name(o.getString("name"))
                        .value(o.getString("value"))
                        .signature(o.getString("signature"))
                        .build();
            }

            ((JungleServer) server()).setPlayer(session, uuid, username, properties);
        }

        @Override
        public void error(Throwable throwable) {
            session.disconnect();
        }
    }
}
