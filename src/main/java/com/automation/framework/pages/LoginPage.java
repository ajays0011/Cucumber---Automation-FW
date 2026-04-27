package com.automation.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Signup / Login Page.
 * Handles both login and new user registration.
 */
public class LoginPage extends BasePage {

    // ──────────────────────────────────────────────
    // Login Form Locators
    // ──────────────────────────────────────────────

    @FindBy(xpath = "//h2[contains(text(),'Login to your account')]")
    private WebElement loginHeader;

    @FindBy(css = "input[data-qa='login-email']")
    private WebElement loginEmailInput;

    @FindBy(css = "input[data-qa='login-password']")
    private WebElement loginPasswordInput;

    @FindBy(css = "button[data-qa='login-button']")
    private WebElement loginButton;

    @FindBy(xpath = "//p[contains(text(),'Your email or password is incorrect!')]")
    private WebElement loginErrorMessage;

    // ──────────────────────────────────────────────
    // Signup Form Locators
    // ──────────────────────────────────────────────

    @FindBy(xpath = "//h2[contains(text(),'New User Signup!')]")
    private WebElement signupHeader;

    @FindBy(css = "input[data-qa='signup-name']")
    private WebElement signupNameInput;

    @FindBy(css = "input[data-qa='signup-email']")
    private WebElement signupEmailInput;

    @FindBy(css = "button[data-qa='signup-button']")
    private WebElement signupButton;

    @FindBy(xpath = "//p[contains(text(),'Email Address already exist!')]")
    private WebElement signupErrorMessage;

    // ──────────────────────────────────────────────
    // Login Actions
    // ──────────────────────────────────────────────

    @Step("Verify 'Login to your account' is visible")
    public boolean isLoginFormVisible() {
        try {
            waitForVisibility(loginHeader);
            return isDisplayed(loginHeader);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Login with email: {email}")
    public void login(String email, String password) {
        LOG.info("Logging in with email: {}", email);
        type(loginEmailInput, email);
        type(loginPasswordInput, password);
        click(loginButton);
    }

    @Step("Verify login error message is visible")
    public boolean isLoginErrorVisible() {
        try {
            waitForVisibility(loginErrorMessage);
            return isDisplayed(loginErrorMessage);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Get login error message text")
    public String getLoginErrorMessage() {
        waitForVisibility(loginErrorMessage);
        return getText(loginErrorMessage);
    }

    // ──────────────────────────────────────────────
    // Signup Actions
    // ──────────────────────────────────────────────

    @Step("Verify 'New User Signup!' is visible")
    public boolean isSignupFormVisible() {
        try {
            waitForVisibility(signupHeader);
            return isDisplayed(signupHeader);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Enter signup details — name: {name}, email: {email}")
    public void enterSignupDetails(String name, String email) {
        LOG.info("Entering signup details — name: {}, email: {}", name, email);
        type(signupNameInput, name);
        type(signupEmailInput, email);
        click(signupButton);
    }

    @Step("Verify signup error message is visible")
    public boolean isSignupErrorVisible() {
        try {
            waitForVisibility(signupErrorMessage);
            return isDisplayed(signupErrorMessage);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Get signup error text")
    public String getSignupErrorMessage() {
        waitForVisibility(signupErrorMessage);
        return getText(signupErrorMessage);
    }
}
