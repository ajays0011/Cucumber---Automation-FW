package com.automation.tests.stepdefinitions;

import com.automation.framework.pages.HomePage;
import com.automation.framework.pages.ProductsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

import java.util.List;

/**
 * Step definitions for product-related scenarios.
 */
public class ProductSteps {

    private final HomePage homePage = new HomePage();
    private final ProductsPage productsPage = new ProductsPage();

    @When("user clicks on Products button")
    public void userClicksProducts() {
        homePage.clickProducts();
    }

    @Then("user should see All Products page")
    public void userShouldSeeAllProducts() {
        Assert.assertTrue(productsPage.isAllProductsPageVisible(),
                "All Products page should be visible");
    }

    @Then("products list should be visible")
    public void productsListShouldBeVisible() {
        Assert.assertTrue(productsPage.getProductCount() > 0,
                "Products list should contain items");
    }

    @When("user clicks View Product of first product")
    public void userClicksFirstViewProduct() {
        productsPage.clickViewProduct(1);
    }

    @Then("user should see product detail with name category price availability condition brand")
    public void userShouldSeeProductDetail() {
        Assert.assertTrue(productsPage.isProductDetailVisible(),
                "Product detail should be visible");
        Assert.assertFalse(productsPage.getProductName().isEmpty(),
                "Product name should not be empty");
        Assert.assertFalse(productsPage.getProductCategory().isEmpty(),
                "Product category should not be empty");
        Assert.assertFalse(productsPage.getProductPrice().isEmpty(),
                "Product price should not be empty");
    }

    @When("user searches for product {string}")
    public void userSearchesForProduct(String productName) {
        productsPage.searchProduct(productName);
    }

    @Then("user should see Searched Products header")
    public void userShouldSeeSearchedProducts() {
        Assert.assertTrue(productsPage.isSearchedProductsVisible(),
                "Searched Products header should be visible");
    }

    @Then("search results should contain products related to {string}")
    public void searchResultsShouldContain(String searchTerm) {
        List<String> productNames = productsPage.getSearchedProductNames();
        Assert.assertFalse(productNames.isEmpty(),
                "Search results should not be empty");
    }

    @When("user sets product quantity to {string}")
    public void userSetsQuantity(String quantity) {
        productsPage.setQuantity(quantity);
    }

    @And("user clicks Add to Cart button on product detail")
    public void userClicksAddToCartOnDetail() {
        productsPage.clickAddToCart();
    }

    @When("user clicks on View Product at index {int}")
    public void userClicksViewProductAtIndex(int index) {
        productsPage.clickViewProduct(index);
    }

    @Then("user should see Write Your Review section")
    public void userShouldSeeWriteReview() {
        Assert.assertTrue(productsPage.isWriteReviewVisible(),
                "Write Your Review section should be visible");
    }

    @When("user submits review with name {string} email {string} review {string}")
    public void userSubmitsReview(String name, String email, String review) {
        productsPage.submitReview(name, email, review);
    }

    @Then("user should see review success message")
    public void userShouldSeeReviewSuccess() {
        Assert.assertTrue(productsPage.isReviewSuccessVisible(),
                "Review success message should be visible");
    }
}
