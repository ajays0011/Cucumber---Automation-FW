@ui @smoke @regression
Feature: User Login Functionality
  As a user of Automation Exercise
  I want to login and manage my account
  So that I can access personalized features

  Background:
    Given user is on the home page

  @smoke @TC002
  Scenario: Login with valid credentials
    When user clicks on Signup/Login button
    Then user should see "Login to your account" header
    When user enters email "testautomation@test.com" and password "Test@1234"
    Then user should be logged in as "TestUser"

  @regression @TC003
  Scenario: Login with invalid credentials
    When user clicks on Signup/Login button
    Then user should see "Login to your account" header
    When user enters email "invalid@email.com" and password "wrong123"
    Then user should see login error message "Your email or password is incorrect!"

  @regression @TC004
  Scenario: Logout user
    When user clicks on Signup/Login button
    When user enters email "testautomation@test.com" and password "Test@1234"
    Then user should be logged in as "TestUser"
    When user clicks Logout button
    Then user should be on login page

  @regression @TC005
  Scenario: Signup with existing email
    When user clicks on Signup/Login button
    Then user should see "New User Signup!" header
    When user enters signup name "TestUser" and email "testautomation@test.com"
    Then user should see signup error "Email Address already exist!"
