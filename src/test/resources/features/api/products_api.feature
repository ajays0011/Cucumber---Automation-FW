@api @smoke @regression
Feature: Products and Brands API
  As a QA engineer
  I want to validate the products and brands APIs
  So that I can ensure backend services work correctly

  @smoke @API001
  Scenario: Get All Products List
    When user sends GET request to products list API
    Then API response code should be 200
    And response should contain products list

  @regression @API002
  Scenario: POST to All Products List - Method Not Supported
    When user sends POST request to products list API
    Then API response code should be 200
    And response message should be "This request method is not supported."

  @smoke @API003
  Scenario: Get All Brands List
    When user sends GET request to brands list API
    Then API response code should be 200
    And response should contain brands list

  @regression @API004
  Scenario: PUT to All Brands List - Method Not Supported
    When user sends PUT request to brands list API
    Then API response code should be 200
    And response message should be "This request method is not supported."

  @smoke @API005
  Scenario: Search Product with valid parameter
    When user sends POST search request for "top"
    Then API response code should be 200
    And response should contain searched products

  @regression @API006
  Scenario: Search Product without parameter
    When user sends POST search request without parameter
    Then API response code should be 200
    And response message should be "Bad request, search_product parameter is missing in POST request."
