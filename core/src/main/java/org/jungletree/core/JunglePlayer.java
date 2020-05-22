package org.jungletree.core;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jungletree.api.Player;
import org.jungletree.api.chat.ChatMessage;
import org.jungletree.api.player.ProfileItem;
import org.jungletree.net.Session;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Log4j2
public class JunglePlayer implements Player, Comparable<JunglePlayer> {

    @Getter private final Session session;
    private final UUID uuid;
    private final String username;
    private final ProfileItem[] profile;

    private ChatMessage displayName;

    public JunglePlayer(Session session, UUID uuid, String username, ProfileItem[] profile) {
        this.session = session;
        this.uuid = uuid;
        this.username = username;
        this.profile = profile;

        this.displayName = ChatMessage.builder().text(username).build();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public ChatMessage getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(ChatMessage displayName) {
        this.displayName = displayName;
    }

    @Override
    public ProfileItem[] getProfile() {
        return Arrays.copyOf(profile, profile.length);
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public boolean isOnline() {
        return session.isOnline();
    }

    @Override
    public int compareTo(JunglePlayer o) {
        if (o == null) {
            return -1;
        }
        // Natural sort
        return username.compareTo(o.username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JunglePlayer that = (JunglePlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JunglePlayer.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("username='" + username + "'")
                .toString();
    }

    public void join() {
        log.info("{} joined the game", username);
    }
}