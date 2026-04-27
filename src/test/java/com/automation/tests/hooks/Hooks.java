package com.automation.tests.hooks;

import com.automation.framework.config.ConfigReader;
import com.automation.framework.driver.DriverFactory;
import com.automation.framework.utils.AllureHistoryManager;
import com.automation.framework.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;

/**
 * Cucumber Hooks for lifecycle management.
 *
 * <ul>
 *   <li>@BeforeAll: Global setup (Allure environment, history)</li>
 *   <li>@Before: Per-scenario driver initialization</li>
 *   <li>@After: Screenshot on failure, driver teardown</li>
 *   <li>@AfterAll: Generate feature-wise Extent Report + global cleanup</li>
 * </ul>
 */
public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

    @BeforeAll
    public static void globalSetup() {
        LOG.info("╔══════════════════════════════════════════════════╗");
        LOG.info("║        AUTOMATION FRAMEWORK — STARTING          ║");
        LOG.info("╚══════════════════════════════════════════════════╝");

        // Preserve Allure history for trend analysis
        AllureHistoryManager.preserveHistory();

        // Write Allure environment info
        String browser = System.getProperty("browser", ConfigReader.get("browser", "chrome"));
        String env = ConfigReader.getEnvironment();
        String baseUrl = ConfigReader.get("base.url");
        AllureHistoryManager.writeEnvironmentInfo(browser, env, baseUrl);

        // Write Allure categories
        AllureHistoryManager.writeCategories();
    }

    @Before
    public void setUp(Scenario scenario) {
        LOG.info("┌──────────────────────────────────────────────────────────┐");
        LOG.info("│ SCENARIO: {}", scenario.getName());
        LOG.info("│ TAGS    : {}", scenario.getSourceTagNames());
        LOG.info("│ THREAD  : {}", Thread.currentThread().getName());
        LOG.info("└──────────────────────────────────────────────────────────┘");

        // Only init driver for UI scenarios (skip for API-only)
        if (!scenario.getSourceTagNames().contains("@api")) {
            DriverFactory.initDriver();
            DriverFactory.getDriver().get(ConfigReader.get("base.url"));
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        LOG.info("Scenario '{}' finished with status: {}", scenario.getName(), scenario.getStatus());

        // Capture screenshot on failure for UI tests
        if (scenario.isFailed() && DriverFactory.hasDriver()) {
            try {
                byte[] screenshot = ScreenshotUtil.captureScreenshot();

                // Attach to Cucumber report
                scenario.attach(screenshot, "image/png", "failure_screenshot");

                // Attach to Allure report
                Allure.addAttachment(
                        "Failure Screenshot — " + scenario.getName(),
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );

                // Save to file system
                ScreenshotUtil.saveToFile(scenario.getName());

                LOG.info("Screenshot captured for failed scenario: {}", scenario.getName());
            } catch (Exception e) {
                LOG.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }

        // Quit driver if it was initialized
        if (DriverFactory.hasDriver()) {
            DriverFactory.quitDriver();
        }
    }

    @AfterAll
    public static void globalTearDown() {
        LOG.info("╔══════════════════════════════════════════════════╗");
        LOG.info("║        AUTOMATION FRAMEWORK — COMPLETED         ║");
        LOG.info("╚══════════════════════════════════════════════════╝");
    }
}
