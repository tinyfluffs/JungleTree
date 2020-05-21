package org.jungletree.startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jungletree.api.JungleTree;
import org.jungletree.api.exception.StartupException;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);
    
    public static void main(String[] args) {
        var server = JungleTree.server();

        try {
            server.start();
        } catch (StartupException ex) {
            log.error(ex);
            System.exit(1);
        }

        log.info("Starting JungleTree {} for Minecraft v{}", "0.0.1-SNAPSHOT", server.getHighestSupportedGameVersion().getName());
    }
}
