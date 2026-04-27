package com.automation.tests.stepdefinitions;

import com.automation.framework.pages.HomePage;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

/**
 * Step definitions for subscription and navigation scenarios.
 */
public class CommonSteps {

    private final HomePage homePage = new HomePage();

    @When("user scrolls down to footer")
    public void userScrollsToFooter() {
        homePage.isSubscriptionVisible();
    }

    @Then("subscription section should be visible")
    public void subscriptionShouldBeVisible() {
        Assert.assertTrue(homePage.isSubscriptionVisible(),
                "Subscription section should be visible");
    }

    @When("user subscribes with email {string}")
    public void userSubscribesWithEmail(String email) {
        homePage.subscribeWithEmail(email);
    }

    @Then("user should see subscription success message")
    public void userShouldSeeSubscriptionSuccess() {
        String message = homePage.getSubscriptionSuccessMessage();
        Assert.assertTrue(message.contains("successfully subscribed"),
                "Should see subscription success message");
    }
}
