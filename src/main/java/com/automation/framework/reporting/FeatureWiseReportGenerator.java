package com.automation.framework.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.automation.framework.config.ConfigReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Generates a feature-wise Extent Report by processing Cucumber JSON output.
 * <p>
 * This replaces the grasshopper adapter's tag-based Category view with
 * a clean feature-based grouping.
 * </p>
 *
 * <p>Category tab will show:</p>
 * <ul>
 *   <li>User Login Functionality — 4 passed, 0 failed</li>
 *   <li>Product Browsing and Search — 3 passed, 1 failed</li>
 *   <li>Shopping Cart Functionality — 2 passed, 1 failed</li>
 *   <li>Products and Brands API — 6 passed, 0 failed</li>
 *   <li>User Account API — 3 passed, 2 failed</li>
 * </ul>
 *
 * <p>Usage: Call {@code FeatureWiseReportGenerator.generate()} after test execution.</p>
 */
public class FeatureWiseReportGenerator {

    private static final Logger LOG = LogManager.getLogger(FeatureWiseReportGenerator.class);
    private static final String REPORT_PATH = "reports/extent-report/SparkReport.html";
    private static final String CUCUMBER_JSON_DIR = "target/cucumber-reports";

    /**
     * Generates the feature-wise Extent Report from Cucumber JSON files.
     */
    public static void generate() {
        LOG.info("╔════════════════════════════════════════════════════════╗");
        LOG.info("║  Generating Feature-Wise Extent Report                ║");
        LOG.info("╚════════════════════════════════════════════════════════╝");

        try {
            // 1. Find Cucumber JSON file
            File jsonFile = findCucumberJson();
            if (jsonFile == null) {
                LOG.warn("No Cucumber JSON file found. Skipping Extent report generation.");
                return;
            }

            // 2. Parse the JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode features = mapper.readTree(jsonFile);

            // 3. Create Extent Report
            ExtentReports extent = createExtentReports();

            // 4. Process each feature
            for (JsonNode feature : features) {
                processFeature(extent, feature);
            }

            // 5. Flush report
            extent.flush();
            LOG.info("✓ Feature-Wise Extent Report generated: {}", REPORT_PATH);

        } catch (IOException e) {
            LOG.error("Failed to generate Feature-Wise report", e);
        }
    }

    /**
     * Creates and configures the ExtentReports instance.
     */
    private static ExtentReports createExtentReports() {
        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Automation Exercise — Feature-Wise Test Report");
        spark.config().setReportName("BDD Automation Test Report");
        spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        spark.config().setEncoding("UTF-8");
        spark.config().setOfflineMode(true);

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);

        // System info
        String env;
        String browser;
        try {
            env = ConfigReader.getEnvironment().toUpperCase();
            browser = System.getProperty("browser", ConfigReader.get("browser", "chrome"));
        } catch (Exception e) {
            env = System.getProperty("env", "QA").toUpperCase();
            browser = System.getProperty("browser", "chrome");
        }

        extent.setSystemInfo("Application", "Automation Exercise");
        extent.setSystemInfo("Framework", "Cucumber BDD + TestNG");
        extent.setSystemInfo("Environment", env);
        extent.setSystemInfo("Browser", browser);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));

        return extent;
    }

    /**
     * Processes a single feature node from Cucumber JSON.
     */
    private static void processFeature(ExtentReports extent, JsonNode feature) {
        String featureName = feature.path("name").asText("Unknown Feature");
        String featureDesc = feature.path("description").asText("");

        LOG.info("Processing feature: {}", featureName);

        // Process each scenario in the feature
        JsonNode elements = feature.path("elements");
        for (JsonNode element : elements) {
            String type = element.path("type").asText("");
            if (!"scenario".equals(type)) continue;

            String scenarioName = element.path("name").asText("Unknown Scenario");

            // Create test with scenario name
            ExtentTest test = extent.createTest(scenarioName);

            // Assign feature name as CATEGORY (this is the key change!)
            test.assignCategory(featureName);

            // Add feature description if available
            if (!featureDesc.isEmpty()) {
                test.info("Feature: " + featureName);
            }

            // Process steps
            boolean scenarioFailed = false;
            boolean scenarioSkipped = false;
            StringBuilder failureMessage = new StringBuilder();

            JsonNode steps = element.path("steps");
            for (JsonNode step : steps) {
                String keyword = step.path("keyword").asText("").trim();
                String stepName = step.path("name").asText("");
                String stepStatus = step.path("result").path("status").asText("undefined");
                long durationNanos = step.path("result").path("duration").asLong(0);
                String errorMessage = step.path("result").path("error_message").asText("");

                String fullStep = keyword + " " + stepName;
                double durationMs = durationNanos / 1_000_000.0;

                switch (stepStatus.toLowerCase()) {
                    case "passed" -> test.pass(String.format("✓ %s (%.0fms)", fullStep, durationMs));
                    case "failed" -> {
                        scenarioFailed = true;
                        String msg = String.format("✗ %s (%.0fms)", fullStep, durationMs);
                        if (!errorMessage.isEmpty()) {
                            // Truncate long error messages
                            String shortError = errorMessage.length() > 500
                                    ? errorMessage.substring(0, 500) + "..."
                                    : errorMessage;
                            test.fail(msg + "\n" + shortError);
                            failureMessage.append(shortError);
                        } else {
                            test.fail(msg);
                        }
                    }
                    case "skipped" -> {
                        scenarioSkipped = true;
                        test.skip("⊘ " + fullStep);
                    }
                    case "undefined" -> {
                        scenarioSkipped = true;
                        test.warning("⚠ " + fullStep + " — Step not implemented");
                    }
                    default -> test.info("• " + fullStep + " [" + stepStatus + "]");
                }
            }

            // Process embedded screenshots (if any)
            for (JsonNode step : steps) {
                JsonNode embeddings = step.path("embeddings");
                if (embeddings.isArray()) {
                    for (JsonNode embedding : embeddings) {
                        String mimeType = embedding.path("mime_type").asText("");
                        if (mimeType.contains("image")) {
                            String base64Data = embedding.path("data").asText("");
                            if (!base64Data.isEmpty()) {
                                test.addScreenCaptureFromBase64String(base64Data, "Screenshot");
                            }
                        }
                    }
                }
            }

            // Add tags as info (not as category)
            JsonNode tags = element.path("tags");
            if (tags.isArray() && !tags.isEmpty()) {
                StringBuilder tagStr = new StringBuilder("Tags: ");
                for (JsonNode tag : tags) {
                    tagStr.append(tag.path("name").asText("")).append(" ");
                }
                test.info(tagStr.toString().trim());
            }
        }
    }

    /**
     * Finds the first Cucumber JSON file in the reports directory.
     */
    private static File findCucumberJson() {
        File dir = new File(CUCUMBER_JSON_DIR);
        if (!dir.exists()) {
            LOG.warn("Cucumber reports directory not found: {}", CUCUMBER_JSON_DIR);
            return null;
        }

        // Look for cucumber.json first, then any JSON file
        File primary = new File(dir, "cucumber.json");
        if (primary.exists() && primary.length() > 0) {
            return primary;
        }

        // Fallback: find any JSON file
        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (jsonFiles != null && jsonFiles.length > 0) {
            // Sort to get the most recent
            Arrays.sort(jsonFiles, Comparator.comparingLong(File::lastModified).reversed());
            return jsonFiles[0];
        }

        return null;
    }
}
