package com.automation.tests.stepdefinitions;

import com.automation.framework.pages.CartPage;
import com.automation.framework.pages.HomePage;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

/**
 * Step definitions for cart-related scenarios.
 */
public class CartSteps {

    private final HomePage homePage = new HomePage();
    private final CartPage cartPage = new CartPage();

    @When("user adds product {int} to cart from home page")
    public void userAddsProductToCart(int index) {
        homePage.addProductToCart(index);
    }

    @And("user clicks Continue Shopping")
    public void userClicksContinueShopping() {
        homePage.clickContinueShopping();
    }

    @And("user clicks View Cart in modal")
    public void userClicksViewCartModal() {
        homePage.clickViewCartInModal();
    }

    @When("user clicks Cart button")
    public void userClicksCart() {
        homePage.clickCart();
    }

    @Then("cart page should be displayed")
    public void cartPageShouldBeDisplayed() {
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
    }

    @Then("cart should contain {int} product(s)")
    public void cartShouldContainProducts(int count) {
        Assert.assertEquals(cartPage.getCartItemCount(), count,
                "Cart should contain " + count + " product(s)");
    }

    @Then("product at row {int} should have quantity {string}")
    public void productShouldHaveQuantity(int row, String quantity) {
        Assert.assertEquals(cartPage.getProductQuantity(row), quantity,
                "Product quantity should be " + quantity);
    }

    @When("user removes product at row {int}")
    public void userRemovesProduct(int row) {
        cartPage.removeProduct(row);
    }

    @When("user clicks Proceed To Checkout")
    public void userClicksProceedToCheckout() {
        cartPage.clickProceedToCheckout();
    }

    @Then("product prices quantities and totals should be correct")
    public void verifyProductPricesAndTotals() {
        Assert.assertFalse(cartPage.getProductPrice(1).isEmpty(), "Price should not be empty");
        Assert.assertFalse(cartPage.getProductQuantity(1).isEmpty(), "Quantity should not be empty");
        Assert.assertFalse(cartPage.getProductTotal(1).isEmpty(), "Total should not be empty");
    }
}
