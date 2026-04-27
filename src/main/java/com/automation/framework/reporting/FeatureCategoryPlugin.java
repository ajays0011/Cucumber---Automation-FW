package com.automation.framework.reporting;

import com.automation.framework.config.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Cucumber plugin that builds a FEATURE-WISE Extent Report in real-time.
 *
 * <p>Instead of using the grasshopper adapter (which shows tags as categories),
 * this plugin creates an Extent Report where the Category tab shows
 * feature names with pass/fail statistics.</p>
 *
 * <p>The Category tab will display:</p>
 * <ul>
 *   <li>User Login Functionality — passed/failed/total</li>
 *   <li>Product Browsing and Search — passed/failed/total</li>
 *   <li>Shopping Cart Functionality — passed/failed/total</li>
 *   <li>Products and Brands API — passed/failed/total</li>
 *   <li>User Account API — passed/failed/total</li>
 * </ul>
 */
public class FeatureCategoryPlugin implements ConcurrentEventListener {

    private static final Logger LOG = LogManager.getLogger(FeatureCategoryPlugin.class);
    private static final String REPORT_PATH = "reports/extent-report/SparkReport.html";

    // Extent Reports instance (thread-safe singleton)
    private static volatile ExtentReports extent;
    private static final Object LOCK = new Object();

    // Feature tracking
    private static final Map<URI, String> URI_FEATURE_MAP = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> CURRENT_FEATURE = new ThreadLocal<>();

    // Scenario tracking per thread
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();
    private static final ThreadLocal<Long> SCENARIO_START_TIME = new ThreadLocal<>();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
    }

    // ──────────────────────────────────────────────
    // Event Handlers
    // ──────────────────────────────────────────────

    private void handleTestSourceRead(TestSourceRead event) {
        URI uri = event.getUri();
        String featureName = extractFeatureName(event.getSource());
        if (featureName != null) {
            URI_FEATURE_MAP.put(uri, featureName);
            LOG.debug("Registered feature: '{}' from {}", featureName, uri);
        }
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        String scenarioName = event.getTestCase().getName();
        URI uri = event.getTestCase().getUri();

        // Get feature name
        String featureName = URI_FEATURE_MAP.getOrDefault(uri, deriveFromUri(uri));
        CURRENT_FEATURE.set(featureName);
        SCENARIO_START_TIME.set(System.currentTimeMillis());

        // Create Extent test with feature as CATEGORY
        ExtentTest test = getExtentReports().createTest(scenarioName);
        test.assignCategory(featureName);
        CURRENT_TEST.set(test);

        LOG.debug("Test case started — Feature: '{}', Scenario: '{}'", featureName, scenarioName);
    }

    private void handleTestStepFinished(TestStepFinished event) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) return;

        if (event.getTestStep() instanceof PickleStepTestStep step) {
            String keyword = step.getStep().getKeyword().trim();
            String stepText = step.getStep().getText();
            String fullStep = keyword + " " + stepText;

            Result result = event.getResult();
            long durationMs = result.getDuration().toMillis();

            switch (result.getStatus()) {
                case PASSED -> test.pass(String.format("✓ %s (%dms)", fullStep, durationMs));
                case FAILED -> {
                    String errorMsg = result.getError() != null
                            ? result.getError().getMessage() : "Unknown error";
                    // Truncate long error messages
                    if (errorMsg != null && errorMsg.length() > 300) {
                        errorMsg = errorMsg.substring(0, 300) + "...";
                    }
                    test.fail(String.format("✗ %s (%dms)\n%s", fullStep, durationMs, errorMsg));
                }
                case SKIPPED -> test.skip("⊘ " + fullStep);
                case UNDEFINED -> test.warning("⚠ " + fullStep + " — Step not defined");
                default -> test.info("• " + fullStep);
            }
        }
    }

    private void handleTestCaseFinished(TestCaseFinished event) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            long startTime = SCENARIO_START_TIME.get() != null ? SCENARIO_START_TIME.get() : 0;
            long duration = System.currentTimeMillis() - startTime;

            String featureName = CURRENT_FEATURE.get();
            test.info(String.format("Feature: %s | Duration: %dms", featureName, duration));
        }

        // Cleanup ThreadLocals
        CURRENT_TEST.remove();
        CURRENT_FEATURE.remove();
        SCENARIO_START_TIME.remove();
    }

    private void handleTestRunFinished(TestRunFinished event) {
        synchronized (LOCK) {
            if (extent != null) {
                extent.flush();
                LOG.info("╔════════════════════════════════════════════════════════╗");
                LOG.info("║  ✓ Feature-Wise Extent Report Generated               ║");
                LOG.info("║  Path: {}  ", REPORT_PATH);
                LOG.info("╚════════════════════════════════════════════════════════╝");
                extent = null; // Reset for next run
            }
        }
    }

    // ──────────────────────────────────────────────
    // Public Static API (for Hooks)
    // ──────────────────────────────────────────────

    public static String getCurrentFeatureName() {
        String name = CURRENT_FEATURE.get();
        return name != null ? name : "Unknown Feature";
    }

    public static void clearCurrentFeature() {
        CURRENT_FEATURE.remove();
    }

    // ──────────────────────────────────────────────
    // Extent Reports Setup
    // ──────────────────────────────────────────────

    private static ExtentReports getExtentReports() {
        if (extent == null) {
            synchronized (LOCK) {
                if (extent == null) {
                    ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
                    spark.config().setTheme(Theme.DARK);
                    spark.config().setDocumentTitle("Automation Exercise — Test Report");
                    spark.config().setReportName("Feature-Wise BDD Test Report");
                    spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
                    spark.config().setEncoding("UTF-8");
                    spark.config().setOfflineMode(true);

                    extent = new ExtentReports();
                    extent.attachReporter(spark);

                    // System info
                    String env, browser;
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

                    LOG.info("ExtentReports initialized: {}", REPORT_PATH);
                }
            }
        }
        return extent;
    }

    // ──────────────────────────────────────────────
    // Helper Methods
    // ──────────────────────────────────────────────

    private String extractFeatureName(String source) {
        if (source == null) return null;
        for (String line : source.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("Feature:")) {
                return trimmed.substring("Feature:".length()).trim();
            }
        }
        return null;
    }

    private String deriveFromUri(URI uri) {
        try {
            Path path = Paths.get(uri);
            String fileName = path.getFileName().toString()
                    .replace(".feature", "")
                    .replace("_", " ")
                    .replace("-", " ");
            StringBuilder result = new StringBuilder();
            for (String word : fileName.split("\\s+")) {
                if (!word.isEmpty()) {
                    result.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1)).append(" ");
                }
            }
            return result.toString().trim();
        } catch (Exception e) {
            return "Unknown Feature";
        }
    }
}
