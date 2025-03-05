package settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final String SETTINGS_FILE = "/Users/vladsoporan/IdeaProjects/a4-soporanvlad/src/main/java/settings/settings.properties";

    private static Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + SETTINGS_FILE, e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}