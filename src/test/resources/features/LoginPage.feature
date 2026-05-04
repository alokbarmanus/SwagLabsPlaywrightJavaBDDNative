@loginPage
Feature: Login functionality
  I want to use this template for my feature file

  @loginPage01 @regression @dataFile:env/{env}/data/loginData.json
  Scenario: Login to application with multiple user credentials from data file
    Given user is on the login page
    When user enters username "${username}" and "${password}" from data file
    And user clicks login button
    Then user should see the dashboard header text "Products"
    Then user print address information from "${address}" section of the data file
