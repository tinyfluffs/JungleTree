package org.jungletree.api;

public enum GameVersion {
    VERSION_1_15_2("1.15.2", 578);
    
    private final String name;
    private final int protocol;

    GameVersion(String name, int protocolVersion) {
        this.name = name;
        this.protocol = protocolVersion;
    }

    public String getName() {
        return name;
    }

    public int getProtocol() {
        return protocol;
    }
}
