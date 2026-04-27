@ui @regression
Feature: Shopping Cart Functionality
  As a user of Automation Exercise
  I want to manage items in my cart
  So that I can proceed with my purchase

  Background:
    Given user is on the home page

  @smoke @TC012
  Scenario: Add products to cart
    When user adds product 1 to cart from home page
    And user clicks Continue Shopping
    And user adds product 2 to cart from home page
    And user clicks View Cart in modal
    Then cart page should be displayed
    And cart should contain 2 products
    And product prices quantities and totals should be correct

  @regression @TC017
  Scenario: Remove product from cart
    When user adds product 1 to cart from home page
    And user clicks Continue Shopping
    And user adds product 2 to cart from home page
    And user clicks View Cart in modal
    Then cart page should be displayed
    And cart should contain 2 products
    When user removes product at row 1
    Then cart should contain 1 product

  @smoke @TC010
  Scenario: Verify subscription on home page
    When user scrolls down to footer
    Then subscription section should be visible
    When user subscribes with email "subscriber@test.com"
    Then user should see subscription success message
