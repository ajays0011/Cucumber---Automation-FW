package com.automation.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Home Page of automationexercise.com.
 * Provides methods to interact with navigation, featured items, and subscriptions.
 */
public class HomePage extends BasePage {

    // ──────────────────────────────────────────────
    // Locators
    // ──────────────────────────────────────────────

    @FindBy(css = "a[href='/login']")
    private WebElement signupLoginLink;

    @FindBy(css = "a[href='/products']")
    private WebElement productsLink;

    @FindBy(css = "a[href='/view_cart']")
    private WebElement cartLink;

    @FindBy(css = "a[href='/contact_us']")
    private WebElement contactUsLink;

    @FindBy(css = "a[href='/test_cases']")
    private WebElement testCasesLink;

    @FindBy(xpath = "//a[contains(text(),'Logged in as')]")
    private WebElement loggedInAsText;

    @FindBy(css = "a[href='/delete_account']")
    private WebElement deleteAccountLink;

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutLink;

    @FindBy(id = "slider")
    private WebElement slider;

    @FindBy(css = ".features_items")
    private WebElement featuredItems;

    @FindBy(id = "susbscribe_email")
    private WebElement subscriptionEmailInput;

    @FindBy(id = "subscribe")
    private WebElement subscribeButton;

    @FindBy(css = ".alert-success.alert")
    private WebElement subscriptionSuccessMessage;

    @FindBy(xpath = "//h2[contains(text(),'Subscription')]")
    private WebElement subscriptionHeader;

    @FindBy(css = ".recommended_items")
    private WebElement recommendedItems;

    @FindBy(id = "scrollUp")
    private WebElement scrollUpButton;

    // ──────────────────────────────────────────────
    // Actions
    // ──────────────────────────────────────────────

    @Step("Verify home page is visible")
    public boolean isHomePageVisible() {
        LOG.info("Verifying home page visibility");
        return isDisplayed(slider) || isDisplayed(featuredItems);
    }

    @Step("Click on Signup/Login link")
    public void clickSignupLogin() {
        LOG.info("Clicking Signup / Login link");
        click(signupLoginLink);
    }

    @Step("Click on Products link")
    public void clickProducts() {
        LOG.info("Clicking Products link");
        click(productsLink);
    }

    @Step("Click on Cart link")
    public void clickCart() {
        LOG.info("Clicking Cart link");
        click(cartLink);
    }

    @Step("Click on Contact Us link")
    public void clickContactUs() {
        LOG.info("Clicking Contact Us link");
        click(contactUsLink);
    }

    @Step("Click on Test Cases link")
    public void clickTestCases() {
        LOG.info("Clicking Test Cases link");
        click(testCasesLink);
    }

    @Step("Verify user is logged in as '{username}'")
    public boolean isLoggedInAs(String username) {
        try {
            waitForVisibility(loggedInAsText);
            String text = getText(loggedInAsText);
            LOG.info("Logged in text: {}", text);
            return text.contains(username);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify user is logged in")
    public boolean isLoggedIn() {
        try {
            waitForVisibility(loggedInAsText);
            return isDisplayed(loggedInAsText);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Get logged in username")
    public String getLoggedInUsername() {
        waitForVisibility(loggedInAsText);
        String text = getText(loggedInAsText);
        return text.replace("Logged in as ", "").trim();
    }

    @Step("Click Delete Account")
    public void clickDeleteAccount() {
        LOG.info("Clicking Delete Account");
        click(deleteAccountLink);
    }

    @Step("Click Logout")
    public void clickLogout() {
        LOG.info("Clicking Logout");
        click(logoutLink);
    }

    @Step("Subscribe with email: {email}")
    public void subscribeWithEmail(String email) {
        LOG.info("Subscribing with email: {}", email);
        jsScrollTo(subscriptionEmailInput);
        type(subscriptionEmailInput, email);
        click(subscribeButton);
    }

    @Step("Get subscription success message")
    public String getSubscriptionSuccessMessage() {
        waitForVisibility(subscriptionSuccessMessage);
        return getText(subscriptionSuccessMessage);
    }

    @Step("Verify subscription section is visible")
    public boolean isSubscriptionVisible() {
        jsScrollToBottom();
        return isDisplayed(subscriptionHeader);
    }

    @Step("Verify recommended items are visible")
    public boolean isRecommendedItemsVisible() {
        jsScrollToBottom();
        return isDisplayed(recommendedItems);
    }

    @Step("Click scroll up button")
    public void clickScrollUp() {
        jsScrollToBottom();
        click(scrollUpButton);
    }

    @Step("Add product to cart from home page at index {index}")
    public void addProductToCart(int index) {
        String productOverlay = String.format(
                "(//div[@class='productinfo text-center'])[%d]", index);
        WebElement product = waitForPresence(By.xpath(productOverlay));
        jsScrollTo(product);
        hoverOver(product);

        String addToCartXpath = String.format(
                "(//div[@class='overlay-content']//a[contains(@class,'add-to-cart')])[%d]", index);
        WebElement addToCartBtn = waitForClickability(By.xpath(addToCartXpath));
        click(addToCartBtn);
        LOG.info("Added product at index {} to cart", index);
    }

    @Step("Click Continue Shopping in modal")
    public void clickContinueShopping() {
        click(By.xpath("//button[contains(text(),'Continue Shopping')]"));
    }

    @Step("Click View Cart in modal")
    public void clickViewCartInModal() {
        click(By.xpath("//u[contains(text(),'View Cart')]"));
    }
}
