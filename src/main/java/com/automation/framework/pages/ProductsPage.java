package com.automation.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the Products and Product Detail pages.
 */
public class ProductsPage extends BasePage {

    @FindBy(xpath = "//h2[contains(text(),'All Products')]")
    private WebElement allProductsHeader;

    @FindBy(css = ".features_items .col-sm-4")
    private List<WebElement> productCards;

    @FindBy(id = "search_product")
    private WebElement searchInput;

    @FindBy(id = "submit_search")
    private WebElement searchButton;

    @FindBy(xpath = "//h2[contains(text(),'Searched Products')]")
    private WebElement searchedProductsHeader;

    @FindBy(css = ".product-information h2")
    private WebElement productName;

    @FindBy(xpath = "//p[contains(text(),'Category')]")
    private WebElement productCategory;

    @FindBy(css = ".product-information span span")
    private WebElement productPrice;

    @FindBy(xpath = "//b[contains(text(),'Availability')]/..")
    private WebElement productAvailability;

    @FindBy(xpath = "//b[contains(text(),'Condition')]/..")
    private WebElement productCondition;

    @FindBy(xpath = "//b[contains(text(),'Brand')]/..")
    private WebElement productBrand;

    @FindBy(id = "quantity")
    private WebElement quantityInput;

    @FindBy(css = "button.cart")
    private WebElement addToCartButton;

    @FindBy(xpath = "//a[contains(text(),'Write Your Review')]")
    private WebElement writeReviewLink;

    @FindBy(id = "name")
    private WebElement reviewNameInput;

    @FindBy(id = "email")
    private WebElement reviewEmailInput;

    @FindBy(id = "review")
    private WebElement reviewTextInput;

    @FindBy(id = "button-review")
    private WebElement submitReviewButton;

    @FindBy(xpath = "//span[contains(text(),'Thank you for your review')]")
    private WebElement reviewSuccessMessage;

    @Step("Verify All Products page is visible")
    public boolean isAllProductsPageVisible() {
        try { waitForVisibility(allProductsHeader); return true; } catch (Exception e) { return false; }
    }

    public int getProductCount() { return productCards.size(); }

    @Step("Click View Product at index {index}")
    public void clickViewProduct(int index) {
        String xpath = String.format("(//a[contains(text(),'View Product')])[%d]", index);
        WebElement el = waitForClickability(By.xpath(xpath));
        jsScrollTo(el);
        click(el);
    }

    @Step("Search for product: {name}")
    public void searchProduct(String name) {
        type(searchInput, name);
        click(searchButton);
    }

    public boolean isSearchedProductsVisible() {
        try { waitForVisibility(searchedProductsHeader); return true; } catch (Exception e) { return false; }
    }

    public List<String> getSearchedProductNames() {
        return driver.findElements(By.cssSelector(".features_items .productinfo p"))
                .stream().map(WebElement::getText).toList();
    }

    public String getProductName() { waitForVisibility(productName); return getText(productName); }
    public String getProductCategory() { return getText(productCategory); }
    public String getProductPrice() { return getText(productPrice); }
    public String getProductAvailability() { return getText(productAvailability); }
    public String getProductCondition() { return getText(productCondition); }
    public String getProductBrand() { return getText(productBrand); }
    public boolean isProductDetailVisible() { return isDisplayed(productName) && isDisplayed(productPrice); }

    @Step("Set quantity to {qty}")
    public void setQuantity(String qty) { quantityInput.clear(); type(quantityInput, qty); }

    @Step("Click Add to Cart")
    public void clickAddToCart() { click(addToCartButton); }

    public boolean isWriteReviewVisible() { jsScrollTo(writeReviewLink); return isDisplayed(writeReviewLink); }

    @Step("Submit review")
    public void submitReview(String name, String email, String review) {
        type(reviewNameInput, name);
        type(reviewEmailInput, email);
        type(reviewTextInput, review);
        click(submitReviewButton);
    }

    public boolean isReviewSuccessVisible() {
        try { waitForVisibility(reviewSuccessMessage); return true; } catch (Exception e) { return false; }
    }

    @Step("Add product at index {index} to cart from list")
    public void addProductToCartFromList(int index) {
        String xpath = String.format("(//div[@class='productinfo text-center']//a[contains(@class,'add-to-cart')])[%d]", index);
        WebElement btn = waitForPresence(By.xpath(xpath));
        jsScrollTo(btn);
        hoverOver(btn);
        click(btn);
    }
}
