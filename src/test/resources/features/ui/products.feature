@ui @regression
Feature: Product Browsing and Search
  As a user of Automation Exercise
  I want to browse and search products
  So that I can find items I want to purchase

  Background:
    Given user is on the home page

  @smoke @TC008
  Scenario: Verify All Products and product detail page
    When user clicks on Products button
    Then user should see All Products page
    And products list should be visible
    When user clicks View Product of first product
    Then user should see product detail with name category price availability condition brand

  @regression @TC009
  Scenario: Search for a product
    When user clicks on Products button
    Then user should see All Products page
    When user searches for product "Top"
    Then user should see Searched Products header
    And search results should contain products related to "Top"

  @regression @TC013
  Scenario: Verify product quantity in cart
    When user clicks on View Product at index 1
    When user sets product quantity to "4"
    And user clicks Add to Cart button on product detail
    And user clicks View Cart in modal
    Then cart page should be displayed
    And product at row 1 should have quantity "4"

  @regression @TC021
  Scenario: Add review on product
    When user clicks on Products button
    Then user should see All Products page
    When user clicks View Product of first product
    Then user should see Write Your Review section
    When user submits review with name "TestReviewer" email "review@test.com" review "Great product! Highly recommended."
    Then user should see review success message
