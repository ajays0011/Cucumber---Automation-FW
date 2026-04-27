package com.automation.framework.utils;

import com.automation.framework.driver.DriverFactory;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screenshot utility for capturing and attaching screenshots.
 * Supports Allure, Extent, and file-system storage.
 */
public final class ScreenshotUtil {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "reports/screenshots/";

    private ScreenshotUtil() {}

    /**
     * Captures screenshot and returns as byte array.
     */
    public static byte[] captureScreenshot() {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Captures screenshot and attaches to Allure report.
     */
    public static void attachToAllure(String name) {
        byte[] screenshot = captureScreenshot();
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
        LOG.info("Screenshot attached to Allure: {}", name);
    }

    /**
     * Captures and saves screenshot to file system, returns file path.
     */
    public static String saveToFile(String scenarioName) {
        try {
            byte[] screenshot = captureScreenshot();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String sanitized = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = sanitized + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + fileName;

            File file = new File(filePath);
            FileUtils.writeByteArrayToFile(file, screenshot);
            LOG.info("Screenshot saved: {}", filePath);
            return file.getAbsolutePath();
        } catch (IOException e) {
            LOG.error("Failed to save screenshot", e);
            return null;
        }
    }

    /**
     * Captures screenshot as Base64 (for Extent Reports embedding).
     */
    public static String captureAsBase64() {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BASE64);
    }
}
