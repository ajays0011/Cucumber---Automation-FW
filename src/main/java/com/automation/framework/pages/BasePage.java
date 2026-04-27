package com.automation.framework.pages;

import com.automation.framework.driver.DriverFactory;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base page class providing reusable interaction methods for all Page Objects.
 * All page classes should extend this class to inherit common functionality.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Explicit wait wrappers for stability</li>
 *   <li>Click, type, select helpers</li>
 *   <li>JavaScript executor utilities</li>
 *   <li>Actions class wrappers (hover, drag-drop)</li>
 *   <li>Screenshot capture support</li>
 * </ul>
 */
public abstract class BasePage {

    protected static final Logger LOG = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;

    private static final int DEFAULT_TIMEOUT = 15;
    private static final int SHORT_TIMEOUT = 5;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // ──────────────────────────────────────────────
    // Wait Helpers
    // ──────────────────────────────────────────────

    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickability(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected boolean waitForInvisibility(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean waitForTextPresent(WebElement element, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    // ──────────────────────────────────────────────
    // Interaction Helpers
    // ──────────────────────────────────────────────

    @Step("Click on element")
    protected void click(WebElement element) {
        waitForClickability(element);
        LOG.debug("Clicking element: {}", element);
        element.click();
    }

    @Step("Click on element located by: {locator}")
    protected void click(By locator) {
        WebElement element = waitForClickability(locator);
        LOG.debug("Clicking element: {}", locator);
        element.click();
    }

    @Step("Type '{text}' into element")
    protected void type(WebElement element, String text) {
        waitForVisibility(element);
        element.clear();
        element.sendKeys(text);
        LOG.debug("Typed '{}' into element", text);
    }

    @Step("Type '{text}' into element located by: {locator}")
    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        LOG.debug("Typed '{}' into element: {}", text, locator);
    }

    @Step("Get text from element")
    protected String getText(WebElement element) {
        waitForVisibility(element);
        String text = element.getText().trim();
        LOG.debug("Got text: '{}'", text);
        return text;
    }

    @Step("Get text from element located by: {locator}")
    protected String getText(By locator) {
        WebElement element = waitForVisibility(locator);
        return element.getText().trim();
    }

    protected String getAttribute(WebElement element, String attribute) {
        waitForVisibility(element);
        return element.getAttribute(attribute);
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    // ──────────────────────────────────────────────
    // Select (Dropdown) Helpers
    // ──────────────────────────────────────────────

    protected void selectByVisibleText(WebElement element, String text) {
        waitForVisibility(element);
        new Select(element).selectByVisibleText(text);
        LOG.debug("Selected '{}' from dropdown", text);
    }

    protected void selectByValue(WebElement element, String value) {
        waitForVisibility(element);
        new Select(element).selectByValue(value);
    }

    protected void selectByIndex(WebElement element, int index) {
        waitForVisibility(element);
        new Select(element).selectByIndex(index);
    }

    // ──────────────────────────────────────────────
    // Actions Helpers
    // ──────────────────────────────────────────────

    protected void hoverOver(WebElement element) {
        waitForVisibility(element);
        actions.moveToElement(element).perform();
        LOG.debug("Hovered over element");
    }

    protected void doubleClick(WebElement element) {
        waitForClickability(element);
        actions.doubleClick(element).perform();
    }

    protected void rightClick(WebElement element) {
        waitForClickability(element);
        actions.contextClick(element).perform();
    }

    // ──────────────────────────────────────────────
    // JavaScript Helpers
    // ──────────────────────────────────────────────

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        LOG.debug("JS clicked element");
    }

    protected void jsScrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    protected void jsScrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    protected void jsScrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    protected void jsType(WebElement element, String text) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];", element, text);
    }

    protected Object executeJs(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    // ──────────────────────────────────────────────
    // Navigation Helpers
    // ──────────────────────────────────────────────

    @Step("Navigate to URL: {url}")
    protected void navigateTo(String url) {
        driver.get(url);
        LOG.info("Navigated to: {}", url);
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }

    protected void refreshPage() {
        driver.navigate().refresh();
    }

    // ──────────────────────────────────────────────
    // Alert Helpers
    // ──────────────────────────────────────────────

    protected void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        LOG.debug("Alert accepted");
    }

    protected void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }

    protected String getAlertText() {
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();
    }

    // ──────────────────────────────────────────────
    // Frame Helpers
    // ──────────────────────────────────────────────

    protected void switchToFrame(WebElement frame) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
    }

    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // ──────────────────────────────────────────────
    // Screenshot
    // ──────────────────────────────────────────────

    protected byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    protected String getPageSource() {
        return driver.getPageSource();
    }
}
