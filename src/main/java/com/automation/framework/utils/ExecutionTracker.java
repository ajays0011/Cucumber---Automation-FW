package com.automation.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Tracks execution metrics across builds for analytics.
 * Stores results as JSON for historical trend analysis.
 */
public final class ExecutionTracker {

    private static final Logger LOG = LogManager.getLogger(ExecutionTracker.class);
    private static final String HISTORY_FILE = "reports/execution-history.json";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private ExecutionTracker() {}

    /**
     * Records execution results for trend analysis.
     */
    @SuppressWarnings("unchecked")
    public static void recordExecution(int total, int passed, int failed, int skipped, long durationMs) {
        try {
            Path historyPath = Paths.get(HISTORY_FILE);
            Files.createDirectories(historyPath.getParent());

            List<Map<String, Object>> history;
            if (Files.exists(historyPath)) {
                history = MAPPER.readValue(historyPath.toFile(), List.class);
            } else {
                history = new ArrayList<>();
            }

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            entry.put("buildNumber", System.getenv().getOrDefault("BUILD_NUMBER", 
                    String.valueOf(history.size() + 1)));
            entry.put("total", total);
            entry.put("passed", passed);
            entry.put("failed", failed);
            entry.put("skipped", skipped);
            entry.put("passRate", total > 0 ? Math.round((double) passed / total * 100.0) : 0);
            entry.put("durationMs", durationMs);
            entry.put("durationFormatted", formatDuration(durationMs));
            entry.put("environment", System.getProperty("env", "qa"));
            entry.put("browser", System.getProperty("browser", "chrome"));

            history.add(entry);

            // Keep last 50 runs
            if (history.size() > 50) {
                history = history.subList(history.size() - 50, history.size());
            }

            MAPPER.writeValue(historyPath.toFile(), history);
            LOG.info("Execution recorded: P={} F={} S={} Duration={}", passed, failed, skipped, formatDuration(durationMs));
        } catch (IOException e) {
            LOG.error("Failed to record execution", e);
        }
    }

    private static String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%dm %ds", minutes, seconds);
    }
}
