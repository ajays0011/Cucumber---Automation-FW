package com.automation.framework.driver;

import com.automation.framework.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal for parallel execution.
 * Supports Chrome, Firefox, and Edge with headless mode configuration.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Thread-safe driver management via ThreadLocal</li>
 *   <li>Configurable browser, headless mode, and timeouts</li>
 *   <li>Selenium Grid support for remote execution</li>
 *   <li>Automatic WebDriver binary management</li>
 * </ul>
 */
public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {
        // Utility class — no instantiation
    }

    /**
     * Initializes and returns a WebDriver instance for the current thread.
     * Browser type is resolved from system property or config file.
     *
     * @return thread-safe WebDriver instance
     */
    public static WebDriver initDriver() {
        if (DRIVER_THREAD_LOCAL.get() != null) {
            LOG.warn("Driver already initialized for thread: {}", Thread.currentThread().getName());
            return DRIVER_THREAD_LOCAL.get();
        }

        String browser = System.getProperty("browser", ConfigReader.get("browser", "chrome")).toLowerCase();
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", ConfigReader.get("headless", "false")));
        String gridUrl = ConfigReader.get("grid.url", "");

        LOG.info("┌─────────────────────────────────────────────────────────┐");
        LOG.info("│ Initializing WebDriver                                  │");
        LOG.info("│ Browser : {}", browser);
        LOG.info("│ Headless: {}", headless);
        LOG.info("│ Thread  : {}", Thread.currentThread().getName());
        LOG.info("│ Grid    : {}", gridUrl.isEmpty() ? "Local" : gridUrl);
        LOG.info("└─────────────────────────────────────────────────────────┘");

        WebDriver driver;

        if (!gridUrl.isEmpty()) {
            driver = createRemoteDriver(browser, headless, gridUrl);
        } else {
            driver = createLocalDriver(browser, headless);
        }

        configureDriver(driver);
        DRIVER_THREAD_LOCAL.set(driver);
        return driver;
    }

    /**
     * Returns the WebDriver instance for the current thread.
     *
     * @return WebDriver instance
     * @throws IllegalStateException if driver is not initialized
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not initialized for thread: " + Thread.currentThread().getName()
                            + ". Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    /**
     * Quits the WebDriver and removes it from ThreadLocal.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            LOG.info("Quitting WebDriver for thread: {}", Thread.currentThread().getName());
            try {
                driver.quit();
            } catch (Exception e) {
                LOG.error("Error quitting driver: {}", e.getMessage());
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }

    /**
     * Checks if a driver is active on the current thread.
     */
    public static boolean hasDriver() {
        return DRIVER_THREAD_LOCAL.get() != null;
    }

    // ──────────────────────────────────────────────
    // Private helper methods
    // ──────────────────────────────────────────────

    private static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--disable-extensions");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--remote-allow-origins=*");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                }
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                options.addPreference("dom.webnotifications.enabled", false);
                if (headless) {
                    options.addArguments("--headless");
                    options.addArguments("--width=1920");
                    options.addArguments("--height=1080");
                }
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--disable-notifications");
                options.addArguments("--no-sandbox");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                }
                yield new EdgeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private static WebDriver createRemoteDriver(String browser, boolean headless, String gridUrl) {
        try {
            URL remoteUrl = new URL(gridUrl);
            return switch (browser) {
                case "chrome" -> {
                    ChromeOptions options = new ChromeOptions();
                    if (headless) options.addArguments("--headless=new");
                    yield new RemoteWebDriver(remoteUrl, options);
                }
                case "firefox" -> {
                    FirefoxOptions options = new FirefoxOptions();
                    if (headless) options.addArguments("--headless");
                    yield new RemoteWebDriver(remoteUrl, options);
                }
                case "edge" -> {
                    EdgeOptions options = new EdgeOptions();
                    if (headless) options.addArguments("--headless=new");
                    yield new RemoteWebDriver(remoteUrl, options);
                }
                default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    private static void configureDriver(WebDriver driver) {
        int implicitWait = Integer.parseInt(ConfigReader.get("implicit.wait", "10"));
        int pageLoadTimeout = Integer.parseInt(ConfigReader.get("page.load.timeout", "30"));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        LOG.info("Driver configured — implicitWait={}s, pageLoadTimeout={}s", implicitWait, pageLoadTimeout);
    }
}
