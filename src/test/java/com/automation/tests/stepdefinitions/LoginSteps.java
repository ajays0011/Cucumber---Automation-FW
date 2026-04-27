package com.automation.tests.stepdefinitions;

import com.automation.framework.pages.HomePage;
import com.automation.framework.pages.LoginPage;
import com.automation.framework.pages.SignupPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

/**
 * Step definitions for login and registration scenarios.
 */
public class LoginSteps {

    private final HomePage homePage = new HomePage();
    private final LoginPage loginPage = new LoginPage();
    private final SignupPage signupPage = new SignupPage();

    @Given("user is on the home page")
    public void userIsOnHomePage() {
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");
    }

    @When("user clicks on Signup\\/Login button")
    public void userClicksSignupLogin() {
        homePage.clickSignupLogin();
    }

    @Then("user should see {string} header")
    public void userShouldSeeHeader(String header) {
        if (header.contains("Login")) {
            Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form should be visible");
        } else if (header.contains("Signup")) {
            Assert.assertTrue(loginPage.isSignupFormVisible(), "Signup form should be visible");
        }
    }

    @When("user enters email {string} and password {string}")
    public void userEntersCredentials(String email, String password) {
        loginPage.login(email, password);
    }

    @Then("user should be logged in as {string}")
    public void userShouldBeLoggedIn(String username) {
        Assert.assertTrue(homePage.isLoggedIn(), "User should be logged in");
    }

    @Then("user should see login error message {string}")
    public void userShouldSeeLoginError(String message) {
        Assert.assertTrue(loginPage.isLoginErrorVisible(), "Login error should be visible");
        Assert.assertEquals(loginPage.getLoginErrorMessage(), message);
    }

    @When("user enters signup name {string} and email {string}")
    public void userEntersSignupDetails(String name, String email) {
        loginPage.enterSignupDetails(name, email);
    }

    @Then("user should see Enter Account Information page")
    public void userShouldSeeAccountInfoPage() {
        Assert.assertTrue(signupPage.isAccountInfoVisible(), 
                "Account Information page should be visible");
    }

    @When("user fills account information with title {string} password {string} day {string} month {string} year {string}")
    public void userFillsAccountInfo(String title, String password, String day, String month, String year) {
        signupPage.fillAccountInfo(title, "TestUser", password, day, month, year);
    }

    @And("user selects newsletter and special offers")
    public void userSelectsCheckboxes() {
        signupPage.selectNewsletterAndOffers();
    }

    @And("user fills address with firstname {string} lastname {string} company {string} address {string} country {string} state {string} city {string} zipcode {string} mobile {string}")
    public void userFillsAddress(String fn, String ln, String co, String addr, String country, String state, String city, String zip, String mobile) {
        signupPage.fillAddressDetails(fn, ln, co, addr, "", country, state, city, zip, mobile);
    }

    @And("user clicks Create Account button")
    public void userClicksCreateAccount() {
        signupPage.clickCreateAccount();
    }

    @Then("user should see Account Created message")
    public void userShouldSeeAccountCreated() {
        Assert.assertTrue(signupPage.isAccountCreated(), "Account Created message should be visible");
    }

    @And("user clicks Continue button")
    public void userClicksContinue() {
        signupPage.clickContinue();
    }

    @When("user clicks Delete Account button")
    public void userClicksDeleteAccount() {
        homePage.clickDeleteAccount();
    }

    @Then("user should see Account Deleted message")
    public void userShouldSeeAccountDeleted() {
        Assert.assertTrue(signupPage.isAccountDeleted(), "Account Deleted message should be visible");
    }

    @When("user clicks Logout button")
    public void userClicksLogout() {
        homePage.clickLogout();
    }

    @Then("user should be on login page")
    public void userShouldBeOnLoginPage() {
        Assert.assertTrue(loginPage.isLoginFormVisible(), "User should be on login page");
    }

    @Then("user should see signup error {string}")
    public void userShouldSeeSignupError(String message) {
        Assert.assertTrue(loginPage.isSignupErrorVisible(), "Signup error should be visible");
        Assert.assertEquals(loginPage.getSignupErrorMessage(), message);
    }
}
