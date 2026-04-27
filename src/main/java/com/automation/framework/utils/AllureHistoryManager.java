package com.automation.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages Allure history for trend analysis across builds.
 * Copies previous history into current results before report generation.
 */
public final class AllureHistoryManager {

    private static final Logger LOG = LogManager.getLogger(AllureHistoryManager.class);
    private static final String ALLURE_RESULTS = "allure-results";
    private static final String ALLURE_REPORT = "allure-report";
    private static final String HISTORY_DIR = "history";

    private AllureHistoryManager() {}

    /**
     * Preserves history from previous allure-report into current allure-results.
     * This enables trend tracking across builds.
     */
    public static void preserveHistory() {
        Path sourceHistory = Paths.get(ALLURE_REPORT, HISTORY_DIR);
        Path targetHistory = Paths.get(ALLURE_RESULTS, HISTORY_DIR);

        if (!Files.exists(sourceHistory)) {
            LOG.info("No previous Allure history found at: {}", sourceHistory);
            return;
        }

        try {
            if (!Files.exists(targetHistory)) {
                Files.createDirectories(targetHistory);
            }

            Files.walk(sourceHistory)
                    .filter(Files::isRegularFile)
                    .forEach(source -> {
                        try {
                            Path target = targetHistory.resolve(sourceHistory.relativize(source));
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                            LOG.debug("Copied history file: {}", source.getFileName());
                        } catch (IOException e) {
                            LOG.error("Failed to copy history file: {}", source, e);
                        }
                    });

            LOG.info("✓ Allure history preserved ({} → {})", sourceHistory, targetHistory);
        } catch (IOException e) {
            LOG.error("Failed to preserve Allure history", e);
        }
    }

    /**
     * Writes environment metadata for Allure report.
     */
    public static void writeEnvironmentInfo(String browser, String env, String baseUrl) {
        Path envFile = Paths.get(ALLURE_RESULTS, "environment.properties");
        try {
            Files.createDirectories(envFile.getParent());
            String content = String.format(
                    "Browser=%s%nEnvironment=%s%nBase.URL=%s%nOS=%s%nJava.Version=%s%nExecution.Time=%s%n",
                    browser, env, baseUrl,
                    System.getProperty("os.name"),
                    System.getProperty("java.version"),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            Files.writeString(envFile, content);
            LOG.info("Allure environment.properties written");
        } catch (IOException e) {
            LOG.error("Failed to write environment.properties", e);
        }
    }

    /**
     * Writes Allure categories for failure classification.
     */
    public static void writeCategories() {
        Path categoriesFile = Paths.get(ALLURE_RESULTS, "categories.json");
        String categories = """
                [
                  {
                    "name": "Product Defects",
                    "matchedStatuses": ["failed"],
                    "messageRegex": ".*AssertionError.*"
                  },
                  {
                    "name": "Test Infrastructure Issues",
                    "matchedStatuses": ["broken"],
                    "messageRegex": ".*TimeoutException.*|.*WebDriverException.*|.*NoSuchElementException.*"
                  },
                  {
                    "name": "Flaky Tests",
                    "matchedStatuses": ["failed"],
                    "traceRegex": ".*retry.*",
                    "flaky": true
                  },
                  {
                    "name": "Skipped / Known Issues",
                    "matchedStatuses": ["skipped"]
                  }
                ]
                """;
        try {
            Files.createDirectories(categoriesFile.getParent());
            Files.writeString(categoriesFile, categories);
            LOG.info("Allure categories.json written");
        } catch (IOException e) {
            LOG.error("Failed to write categories.json", e);
        }
    }

    /**
     * Cleans previous allure-results before a new run.
     */
    public static void cleanResults() {
        Path resultsDir = Paths.get(ALLURE_RESULTS);
        if (Files.exists(resultsDir)) {
            try {
                Files.walk(resultsDir)
                        .filter(p -> !p.equals(resultsDir))
                        .filter(p -> !p.toString().contains(HISTORY_DIR))
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try { Files.deleteIfExists(path); }
                            catch (IOException e) { LOG.warn("Could not delete: {}", path); }
                        });
                LOG.info("Cleaned allure-results (history preserved)");
            } catch (IOException e) {
                LOG.error("Error cleaning allure-results", e);
            }
        }
    }
}
