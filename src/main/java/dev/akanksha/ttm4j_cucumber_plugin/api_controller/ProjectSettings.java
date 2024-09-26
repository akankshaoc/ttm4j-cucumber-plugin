package dev.akanksha.ttm4j_cucumber_plugin.api_controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * project settings as read from src/test/resources/cucumber.properties
 *
 * <p>includes:</p>
 *
 * <ul>
 *     <li>ttm4j.projectKey</li>
 *     <li>ttm4j.apiKey</li>
 *     <li>ttm4j.automationName</li>
 * </ul>
 */
public final class ProjectSettings {
    public static final String projectKey;
    public static final String apiKey;
    public static final String automationName;

    static {
        try {
            projectKey = fetchPropertyValue("ttm4j.projectKey");
            apiKey = fetchPropertyValue("ttm4j.apiKey");
            automationName = fetchPropertyValue("ttm4j.agentName");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String fetchPropertyValue(String key) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "cucumber.properties");
        try (InputStream inputStream = Files.newInputStream(path)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty(key);
        }
    }
}
