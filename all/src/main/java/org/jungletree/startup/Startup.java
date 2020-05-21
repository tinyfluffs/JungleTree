package org.jungletree.startup;

import lombok.extern.log4j.Log4j2;
import org.jungletree.api.JungleTree;
import org.jungletree.api.exception.StartupException;

@Log4j2
public class Startup {
    
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
