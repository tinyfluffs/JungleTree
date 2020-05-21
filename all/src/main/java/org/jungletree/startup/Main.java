package org.jungletree.startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jungletree.core.JungleServer;
import org.jungletree.core.exception.StartupException;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);
    
    public static void main(String[] args) {
        JungleServer server = null;
        try {
            server = new JungleServer();
        } catch (StartupException ex) {
            log.error("====\nStartup failed!\n====\n", ex);
            System.exit(1);
        }
        
        log.info("Starting JungleTree {} for Minecraft v{}", "0.0.1-SNAPSHOT", server.getHighestSupportedGameVersion().getName());
    }
}
