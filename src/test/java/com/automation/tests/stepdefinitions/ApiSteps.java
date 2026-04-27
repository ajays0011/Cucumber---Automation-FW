package com.automation.tests.stepdefinitions;

import com.automation.framework.api.ApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Step definitions for API test scenarios.
 */
public class ApiSteps {

    private final ApiClient apiClient = new ApiClient();
    private Response response;

    // ──────────────────────────────────────────────
    // Products API
    // ──────────────────────────────────────────────

    @When("user sends GET request to products list API")
    public void userSendsGetProductsList() {
        response = apiClient.getAllProducts();
    }

    @Then("API response code should be {int}")
    public void apiResponseCodeShouldBe(int expectedCode) {
        Assert.assertEquals(response.getStatusCode(), expectedCode,
                "API response code mismatch");
    }

    @Then("response should contain products list")
    public void responseShouldContainProducts() {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("products"),
                "Response should contain products data");
    }

    @When("user sends POST request to products list API")
    public void userSendsPostProductsList() {
        response = apiClient.postToProductsList();
    }

    @Then("response message should be {string}")
    public void responseMessageShouldBe(String message) {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains(message),
                "Response should contain message: " + message);
    }

    // ──────────────────────────────────────────────
    // Brands API
    // ──────────────────────────────────────────────

    @When("user sends GET request to brands list API")
    public void userSendsGetBrandsList() {
        response = apiClient.getAllBrands();
    }

    @Then("response should contain brands list")
    public void responseShouldContainBrands() {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("brands"),
                "Response should contain brands data");
    }

    @When("user sends PUT request to brands list API")
    public void userSendsPutBrandsList() {
        response = apiClient.putToBrandsList();
    }

    // ──────────────────────────────────────────────
    // Search API
    // ──────────────────────────────────────────────

    @When("user sends POST search request for {string}")
    public void userSendsSearchRequest(String searchTerm) {
        response = apiClient.searchProduct(searchTerm);
    }

    @Then("response should contain searched products")
    public void responseShouldContainSearchedProducts() {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("products"),
                "Response should contain search results");
    }

    @When("user sends POST search request without parameter")
    public void userSendsSearchWithoutParam() {
        response = apiClient.searchProductWithoutParam();
    }

    // ──────────────────────────────────────────────
    // Login API
    // ──────────────────────────────────────────────

    @When("user sends POST verify login with email {string} and password {string}")
    public void userSendsVerifyLogin(String email, String password) {
        response = apiClient.verifyLogin(email, password);
    }

    @When("user sends POST verify login without email with password {string}")
    public void userSendsVerifyLoginWithoutEmail(String password) {
        response = apiClient.verifyLoginWithoutEmail(password);
    }

    @When("user sends DELETE request to verify login API")
    public void userSendsDeleteVerifyLogin() {
        response = apiClient.deleteVerifyLogin();
    }

    // ──────────────────────────────────────────────
    // Account API
    // ──────────────────────────────────────────────

    @When("user sends POST request to create account with valid details")
    public void userCreatesAccount() {
        Map<String, String> userData = new HashMap<>();
        userData.put("name", "TestUser_API");
        userData.put("email", "testuser_api_" + System.currentTimeMillis() + "@test.com");
        userData.put("password", "Test@1234");
        userData.put("title", "Mr");
        userData.put("birth_date", "15");
        userData.put("birth_month", "6");
        userData.put("birth_year", "1990");
        userData.put("firstname", "Test");
        userData.put("lastname", "User");
        userData.put("company", "TestCorp");
        userData.put("address1", "123 Test St");
        userData.put("address2", "Suite 100");
        userData.put("country", "United States");
        userData.put("zipcode", "10001");
        userData.put("state", "New York");
        userData.put("city", "New York");
        userData.put("mobile_number", "1234567890");
        response = apiClient.createAccount(userData);
    }

    @When("user sends DELETE request to delete account with email {string} and password {string}")
    public void userDeletesAccount(String email, String password) {
        response = apiClient.deleteAccount(email, password);
    }

    @When("user sends GET request to get user detail by email {string}")
    public void userGetsDetailByEmail(String email) {
        response = apiClient.getUserDetailByEmail(email);
    }

    @Then("response should contain user detail")
    public void responseShouldContainUserDetail() {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("user"),
                "Response should contain user detail");
    }
}
