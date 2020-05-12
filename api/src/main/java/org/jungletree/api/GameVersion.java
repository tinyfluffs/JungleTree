package org.jungletree.api;

public enum GameVersion {
    VERSION_1_15_2(578);

    private final int protocolVersion;

    GameVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int protocolVersion() {
        return protocolVersion;
    }
}
