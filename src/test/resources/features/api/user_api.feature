@api @regression
Feature: User Account API
  As a QA engineer
  I want to validate user account APIs
  So that I can ensure authentication and account management work correctly

  @smoke @API007
  Scenario: Verify login with valid credentials
    When user sends POST verify login with email "testautomation@test.com" and password "Test@1234"
    Then API response code should be 200
    And response message should be "User exists!"

  @regression @API008
  Scenario: Verify login without email parameter
    When user sends POST verify login without email with password "Test@1234"
    Then API response code should be 200
    And response message should be "Bad request, email or password parameter is missing in POST request."

  @regression @API009
  Scenario: DELETE method on verify login - Not Supported
    When user sends DELETE request to verify login API
    Then API response code should be 200
    And response message should be "This request method is not supported."

  @regression @API010
  Scenario: Verify login with invalid credentials
    When user sends POST verify login with email "nonexistent@test.com" and password "wrong123"
    Then API response code should be 200
    And response message should be "User not found!"

  @regression @API011
  Scenario: Create user account via API
    When user sends POST request to create account with valid details
    Then API response code should be 200
    And response message should be "User created!"
