package org.jungletree.api;

import org.jungletree.api.player.OfflinePlayer;

import java.util.concurrent.TimeUnit;

public interface Player extends OfflinePlayer {

    long getPingRTT(TimeUnit unit);
}
