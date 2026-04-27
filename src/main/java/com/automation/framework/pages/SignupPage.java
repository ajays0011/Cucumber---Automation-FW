package com.automation.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Account Registration Page.
 * Handles the full account creation form after initial signup.
 */
public class SignupPage extends BasePage {

    // ──────────────────────────────────────────────
    // Account Information
    // ──────────────────────────────────────────────

    @FindBy(xpath = "//b[contains(text(),'Enter Account Information')]")
    private WebElement accountInfoHeader;

    @FindBy(id = "id_gender1")
    private WebElement titleMr;

    @FindBy(id = "id_gender2")
    private WebElement titleMrs;

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "days")
    private WebElement daysDropdown;

    @FindBy(id = "months")
    private WebElement monthsDropdown;

    @FindBy(id = "years")
    private WebElement yearsDropdown;

    @FindBy(id = "newsletter")
    private WebElement newsletterCheckbox;

    @FindBy(id = "optin")
    private WebElement specialOffersCheckbox;

    // ──────────────────────────────────────────────
    // Address Information
    // ──────────────────────────────────────────────

    @FindBy(id = "first_name")
    private WebElement firstNameInput;

    @FindBy(id = "last_name")
    private WebElement lastNameInput;

    @FindBy(id = "company")
    private WebElement companyInput;

    @FindBy(id = "address1")
    private WebElement address1Input;

    @FindBy(id = "address2")
    private WebElement address2Input;

    @FindBy(id = "country")
    private WebElement countryDropdown;

    @FindBy(id = "state")
    private WebElement stateInput;

    @FindBy(id = "city")
    private WebElement cityInput;

    @FindBy(id = "zipcode")
    private WebElement zipcodeInput;

    @FindBy(id = "mobile_number")
    private WebElement mobileNumberInput;

    @FindBy(css = "button[data-qa='create-account']")
    private WebElement createAccountButton;

    // ──────────────────────────────────────────────
    // Account Created Page
    // ──────────────────────────────────────────────

    @FindBy(xpath = "//b[contains(text(),'Account Created!')]")
    private WebElement accountCreatedHeader;

    @FindBy(css = "a[data-qa='continue-button']")
    private WebElement continueButton;

    // ──────────────────────────────────────────────
    // Account Deleted Page
    // ──────────────────────────────────────────────

    @FindBy(xpath = "//b[contains(text(),'Account Deleted!')]")
    private WebElement accountDeletedHeader;

    // ──────────────────────────────────────────────
    // Actions
    // ──────────────────────────────────────────────

    @Step("Verify 'Enter Account Information' header is visible")
    public boolean isAccountInfoVisible() {
        try {
            waitForVisibility(accountInfoHeader);
            return isDisplayed(accountInfoHeader);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Fill account information")
    public void fillAccountInfo(String title, String name, String password,
                                String day, String month, String year) {
        LOG.info("Filling account information for: {}", name);

        if ("Mr".equalsIgnoreCase(title)) {
            click(titleMr);
        } else {
            click(titleMrs);
        }

        type(passwordInput, password);
        selectByValue(daysDropdown, day);
        selectByValue(monthsDropdown, month);
        selectByValue(yearsDropdown, year);
    }

    @Step("Select newsletter and special offers checkboxes")
    public void selectNewsletterAndOffers() {
        if (!newsletterCheckbox.isSelected()) {
            click(newsletterCheckbox);
        }
        if (!specialOffersCheckbox.isSelected()) {
            click(specialOffersCheckbox);
        }
    }

    @Step("Fill address details")
    public void fillAddressDetails(String firstName, String lastName, String company,
                                   String address1, String address2, String country,
                                   String state, String city, String zipcode,
                                   String mobileNumber) {
        LOG.info("Filling address for: {} {}", firstName, lastName);

        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(companyInput, company);
        type(address1Input, address1);
        type(address2Input, address2);
        selectByVisibleText(countryDropdown, country);
        type(stateInput, state);
        type(cityInput, city);
        type(zipcodeInput, zipcode);
        type(mobileNumberInput, mobileNumber);
    }

    @Step("Click Create Account button")
    public void clickCreateAccount() {
        LOG.info("Clicking Create Account");
        jsScrollTo(createAccountButton);
        click(createAccountButton);
    }

    @Step("Verify 'Account Created!' is visible")
    public boolean isAccountCreated() {
        try {
            waitForVisibility(accountCreatedHeader);
            return isDisplayed(accountCreatedHeader);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Click Continue after account creation")
    public void clickContinue() {
        click(continueButton);
    }

    @Step("Verify 'Account Deleted!' is visible")
    public boolean isAccountDeleted() {
        try {
            waitForVisibility(accountDeletedHeader);
            return isDisplayed(accountDeletedHeader);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Complete registration with all details in one call.
     */
    @Step("Complete full registration")
    public void completeRegistration(String title, String name, String password,
                                     String day, String month, String year,
                                     String firstName, String lastName, String company,
                                     String address1, String address2, String country,
                                     String state, String city, String zipcode,
                                     String mobileNumber) {
        fillAccountInfo(title, name, password, day, month, year);
        selectNewsletterAndOffers();
        fillAddressDetails(firstName, lastName, company, address1, address2,
                country, state, city, zipcode, mobileNumber);
        clickCreateAccount();
    }
}
