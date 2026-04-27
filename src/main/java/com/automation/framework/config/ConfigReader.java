package com.automation.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration reader that loads environment-specific properties.
 * Supports dev, qa, and prod environments via system property or Maven profile.
 *
 * <p>Usage: ConfigReader.get("base.url")</p>
 */
public final class ConfigReader {

    private static final Logger LOG = LogManager.getLogger(ConfigReader.class);
    private static final Properties PROPERTIES = new Properties();
    private static final String DEFAULT_ENV = "qa";

    static {
        loadProperties();
    }

    private ConfigReader() {
        // Utility class — no instantiation
    }

    /**
     * Loads environment-specific properties file.
     * Priority: System property "env" → Maven property → default (qa)
     */
    private static void loadProperties() {
        String env = System.getProperty("env", DEFAULT_ENV).toLowerCase();
        String configFile = String.format("config/%s.properties", env);
        LOG.info("╔══════════════════════════════════════════════╗");
        LOG.info("║  Loading configuration for environment: {}  ║", env.toUpperCase());
        LOG.info("╚══════════════════════════════════════════════╝");

        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                LOG.warn("Config file '{}' not found in classpath. Trying filesystem...", configFile);
                try (FileInputStream fis = new FileInputStream("src/test/resources/" + configFile)) {
                    PROPERTIES.load(fis);
                }
            } else {
                PROPERTIES.load(input);
            }
            LOG.info("Configuration loaded successfully: {} properties", PROPERTIES.size());
        } catch (IOException e) {
            LOG.error("Failed to load configuration file: {}", configFile, e);
            throw new RuntimeException("Configuration loading failed for env: " + env, e);
        }

        // Override with system properties (CLI takes precedence)
        System.getProperties().forEach((key, value) -> {
            if (key instanceof String k && value instanceof String v) {
                PROPERTIES.setProperty(k, v);
            }
        });
    }

    /**
     * Retrieves a property value by key.
     *
     * @param key the property key
     * @return the property value
     * @throws RuntimeException if key is not found
     */
    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            LOG.error("Property '{}' not found in configuration", key);
            throw new RuntimeException("Missing required config property: " + key);
        }
        return value.trim();
    }

    /**
     * Retrieves a property value with a default fallback.
     *
     * @param key          the property key
     * @param defaultValue fallback value if key is absent
     * @return the property value or default
     */
    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue).trim();
    }

    /**
     * Retrieves a property as an integer.
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Retrieves a property as a boolean.
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Gets the current execution environment.
     */
    public static String getEnvironment() {
        return System.getProperty("env", DEFAULT_ENV).toLowerCase();
    }
}
