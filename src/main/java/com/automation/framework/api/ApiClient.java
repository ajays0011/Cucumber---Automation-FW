package com.automation.framework.api;

import com.automation.framework.config.ConfigReader;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * REST Assured API client for automationexercise.com API endpoints.
 * Provides type-safe methods for all available API operations.
 */
public class ApiClient {

    private static final Logger LOG = LogManager.getLogger(ApiClient.class);
    private final String baseUrl;

    public ApiClient() {
        this.baseUrl = ConfigReader.get("api.base.url", "https://automationexercise.com/api");
        RestAssured.baseURI = baseUrl;
    }

    private RequestSpecification givenSetup() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.URLENC)
                .log().all();
    }

    // ──────────────────────────────────────────────
    // Products APIs
    // ──────────────────────────────────────────────

    @Step("GET - Get all products list")
    public Response getAllProducts() {
        LOG.info("API: GET all products");
        return givenSetup()
                .when().get("/productsList")
                .then().log().all().extract().response();
    }

    @Step("POST - Post to all products list (unsupported)")
    public Response postToProductsList() {
        LOG.info("API: POST to products list");
        return givenSetup()
                .when().post("/productsList")
                .then().log().all().extract().response();
    }

    // ──────────────────────────────────────────────
    // Brands APIs
    // ──────────────────────────────────────────────

    @Step("GET - Get all brands list")
    public Response getAllBrands() {
        LOG.info("API: GET all brands");
        return givenSetup()
                .when().get("/brandsList")
                .then().log().all().extract().response();
    }

    @Step("PUT - Put to all brands list (unsupported)")
    public Response putToBrandsList() {
        LOG.info("API: PUT to brands list");
        return givenSetup()
                .when().put("/brandsList")
                .then().log().all().extract().response();
    }

    // ──────────────────────────────────────────────
    // Search APIs
    // ──────────────────────────────────────────────

    @Step("POST - Search product: {searchTerm}")
    public Response searchProduct(String searchTerm) {
        LOG.info("API: Search product — {}", searchTerm);
        return givenSetup()
                .formParam("search_product", searchTerm)
                .when().post("/searchProduct")
                .then().log().all().extract().response();
    }

    @Step("POST - Search product without parameter")
    public Response searchProductWithoutParam() {
        LOG.info("API: Search product without param");
        return givenSetup()
                .when().post("/searchProduct")
                .then().log().all().extract().response();
    }

    // ──────────────────────────────────────────────
    // Login/Auth APIs
    // ──────────────────────────────────────────────

    @Step("POST - Verify login with email: {email}")
    public Response verifyLogin(String email, String password) {
        LOG.info("API: Verify login for: {}", email);
        return givenSetup()
                .formParam("email", email)
                .formParam("password", password)
                .when().post("/verifyLogin")
                .then().log().all().extract().response();
    }

    @Step("POST - Verify login without email")
    public Response verifyLoginWithoutEmail(String password) {
        return givenSetup()
                .formParam("password", password)
                .when().post("/verifyLogin")
                .then().log().all().extract().response();
    }

    @Step("DELETE - Delete verify login (unsupported)")
    public Response deleteVerifyLogin() {
        return givenSetup()
                .when().delete("/verifyLogin")
                .then().log().all().extract().response();
    }

    // ──────────────────────────────────────────────
    // Account APIs
    // ──────────────────────────────────────────────

    @Step("POST - Create user account")
    public Response createAccount(Map<String, String> userData) {
        LOG.info("API: Create account for: {}", userData.get("email"));
        RequestSpecification req = givenSetup();
        userData.forEach(req::formParam);
        return req.when().post("/createAccount")
                .then().log().all().extract().response();
    }

    @Step("DELETE - Delete user account")
    public Response deleteAccount(String email, String password) {
        LOG.info("API: Delete account: {}", email);
        return givenSetup()
                .formParam("email", email)
                .formParam("password", password)
                .when().delete("/deleteAccount")
                .then().log().all().extract().response();
    }

    @Step("PUT - Update user account")
    public Response updateAccount(Map<String, String> userData) {
        LOG.info("API: Update account: {}", userData.get("email"));
        RequestSpecification req = givenSetup();
        userData.forEach(req::formParam);
        return req.when().put("/updateAccount")
                .then().log().all().extract().response();
    }

    @Step("GET - Get user detail by email: {email}")
    public Response getUserDetailByEmail(String email) {
        LOG.info("API: Get user detail by email: {}", email);
        return givenSetup()
                .queryParam("email", email)
                .when().get("/getUserDetailByEmail")
                .then().log().all().extract().response();
    }
}
