package com.automation.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the Shopping Cart page.
 */
public class CartPage extends BasePage {

    @FindBy(id = "cart_info_table")
    private WebElement cartTable;

    @FindBy(css = "#cart_info_table tbody tr")
    private List<WebElement> cartItems;

    @FindBy(css = ".cart_quantity_delete .cart_quantity_delete")
    private List<WebElement> deleteButtons;

    @FindBy(css = ".check_out")
    private WebElement proceedToCheckoutButton;

    @FindBy(xpath = "//u[contains(text(),'Register / Login')]")
    private WebElement registerLoginLink;

    @FindBy(id = "empty_cart")
    private WebElement emptyCartMessage;

    @Step("Verify cart page is displayed")
    public boolean isCartPageDisplayed() {
        try { waitForVisibility(cartTable); return true; } catch (Exception e) { return false; }
    }

    public int getCartItemCount() { return cartItems.size(); }

    @Step("Get product name at row {index}")
    public String getProductName(int index) {
        return getText(By.cssSelector(String.format("#cart_info_table tbody tr:nth-child(%d) .cart_description h4 a", index)));
    }

    @Step("Get product price at row {index}")
    public String getProductPrice(int index) {
        return getText(By.cssSelector(String.format("#cart_info_table tbody tr:nth-child(%d) .cart_price p", index)));
    }

    @Step("Get product quantity at row {index}")
    public String getProductQuantity(int index) {
        return getText(By.cssSelector(String.format("#cart_info_table tbody tr:nth-child(%d) .cart_quantity button", index)));
    }

    @Step("Get product total at row {index}")
    public String getProductTotal(int index) {
        return getText(By.cssSelector(String.format("#cart_info_table tbody tr:nth-child(%d) .cart_total_price", index)));
    }

    @Step("Remove product at row {index}")
    public void removeProduct(int index) {
        LOG.info("Removing product at row: {}", index);
        WebElement deleteBtn = driver.findElement(
                By.cssSelector(String.format("#cart_info_table tbody tr:nth-child(%d) .cart_quantity_delete a", index)));
        click(deleteBtn);
    }

    @Step("Click Proceed To Checkout")
    public void clickProceedToCheckout() { click(proceedToCheckoutButton); }

    @Step("Click Register/Login from checkout modal")
    public void clickRegisterLogin() { click(registerLoginLink); }

    public boolean isCartEmpty() { return cartItems.isEmpty(); }
}
