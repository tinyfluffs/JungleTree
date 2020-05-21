package org.jungletree.core.version;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JungleVersion {
    
    private static final String UNKNOWN_VERSION_NAME = "UNKNOWN";

    private static String apiVersion;
    private static String implVersion;

    public static String getApiVersion() {
        if (apiVersion == null) {
            apiVersion = getVersion("META-INF/maven/org.jungletree/jungletree-api/pom.properties");
        }
        return apiVersion;
    }

    public static String getImplementationVersion() {
        if (implVersion == null) {
            implVersion = getVersion("META-INF/maven/org.jungletree/jungletree/pom.properties");
        }
        return implVersion;
    }
    
    private static String getVersion(String resourceFile) {
        try {
            var propsFile = JungleVersion.class.getClassLoader().getResourceAsStream(resourceFile);
            Properties properties = new Properties();
            properties.load(propsFile);
            return properties.getProperty("version");
        } catch (Exception ignored) {
            return UNKNOWN_VERSION_NAME;
        }
    }
}
