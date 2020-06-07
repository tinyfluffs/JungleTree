package org.jungletree.startup;

import lombok.extern.log4j.Log4j2;
import org.jungletree.api.GameVersion;
import org.jungletree.api.exception.StartupException;

import java.util.StringJoiner;

import static org.jungletree.api.JungleTree.server;

@Log4j2
public class Startup {

    public static void main(String[] args) {
        try {
            server().start();
        } catch (StartupException ex) {
            log.error("", ex);
            System.exit(1);
        }

        log.info("Starting Minecraft server (versions: {})", versionList());
        log.info("This server is running JungleTree version {} (Implementing API version {})", server().getImplementationVersion(), server().getApiVersion());
    }

    private static String versionList() {
        var j = new StringJoiner(", ");
        for (GameVersion v : server().getSupportedGameVersions()) {
            j.add(v.getName());
        }
        return j.toString();
    }
}
