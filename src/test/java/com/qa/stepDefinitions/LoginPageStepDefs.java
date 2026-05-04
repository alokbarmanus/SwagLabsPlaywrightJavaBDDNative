package com.qa.stepDefinitions;

import java.util.Map;

import com.qa.pages.LoginPageObjects;
import com.qa.utils.ScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPageStepDefs extends BaseStepDefinition {

	private LoginPageObjects loginPageObjects;

	/**
	 * Constructor - calls parent constructor
	 * Inherited: scenarioContext, getTestData(), logging helpers
	 */
	public LoginPageStepDefs(ScenarioContext scenarioContext) {
		super(scenarioContext);
	}

	/**
	 * Given: User is on the login page
	 * For data-driven testing, this step launches the application fresh for each
	 * iteration
	 */
	@Given("user is on the login page")
	public void user_is_on_the_login_page() {
		// Use inherited logging helper - much cleaner!
		logIterationStart("User is on the login page");

		// Launch app for each iteration - using inherited helper to initialize page
		// object
		loginPageObjects = initializePage(LoginPageObjects.class);
		loginPageObjects.launchApplication();
	}

	@When("user enters username {string} and {string} from data file")
	public void user_enters_username_and_from_data_file(String username, String password) {
		loginPageObjects = initializePage(LoginPageObjects.class);

		// Get data from ScenarioContext via inherited helper method
		Map<String, Object> testData = getTestData();
		String actualUsername = (String) testData.get("username");
		String actualPassword = (String) testData.get("password");

		// Use inherited logging helper methods
		logAction("Entering username: " + actualUsername);
		logAction("Entering password: " + actualPassword);

		loginPageObjects.enterUsername(actualUsername);
		loginPageObjects.enterPassword(actualPassword);
	}

	@When("user clicks login button")
	public void user_clicks_login_button() {
		loginPageObjects = initializePage(LoginPageObjects.class);
		// Use inherited logging helper
		logAction("Clicking login button");
		loginPageObjects.clickOnLoginButton();
	}

	@Then("user should see the dashboard header text {string}")
	public void user_should_see_the_dashboard_header_text(String expectedHeader) {
		loginPageObjects = initializePage(LoginPageObjects.class);
		String actualPageHeader = loginPageObjects.getDashboardPageHeader();
		logAction("Dashboard header text: " + actualPageHeader);
		assertThat(loginPageObjects.getDashboardPageHeaderLocator()).hasText(expectedHeader);
		logAction("Assertion passed: Header contains '" + expectedHeader + "'");

		// CHECK IF THERE ARE MORE RECORDS TO PROCESS - using inherited helper
		if (hasMoreRecords()) {
			// Use inherited logging helper
			logLoopMessage();

			// Move to next record using inherited helper
			moveToNextRecord();

			// Re-execute the step sequence for the next data record
			executeScenarioForCurrentRecord(expectedHeader);
		} else {
			// Use inherited logging helper
			logCompletionMessage();
		}
	}

	@Then("user print address information from {string} section of the data file")
	public void user_print_address_information_from_section_of_the_data_file(String addressField) {
		// Get data from ScenarioContext via inherited helper method
		Map<String, Object> testData = getTestData();

		// Get the address object from test data (nested object from JSON)
		Object addressObj = testData.get("address");

		if (addressObj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> addressMap = (Map<String, Object>) addressObj;

			System.out.println("\n    === Address Information ===");
			System.out.println("    Street: " + addressMap.get("street"));
			System.out.println("    City: " + addressMap.get("city"));
			System.out.println("    State: " + addressMap.get("state"));
			System.out.println("    Zip: " + addressMap.get("zip"));
			System.out.println("    ========================\n");
		} else {
			logAction("Address information not found in test data");
		}
	}

	/**
	 * Helper method to execute the complete scenario steps for the current data
	 * record
	 * This enables data-driven testing by looping through all JSON records
	 * 
	 * @param expectedHeader - The dashboard header text to verify
	 */
	private void executeScenarioForCurrentRecord(String expectedHeader) {
		logIterationStart("Processing next data record");

		// 🔑 Use inherited restartBrowser() method - was 8+ lines of duplication!
		restartBrowser();

		// Now the application is fresh - navigate to login page
		loginPageObjects = initializePage(LoginPageObjects.class);
		loginPageObjects.launchApplication();
		logAction("Application launched for next iteration");

		// Re-execute steps for this record with new data from context
		user_enters_username_and_from_data_file("${username}", "${password}");
		user_clicks_login_button();

		// Verify dashboard
		loginPageObjects = initializePage(LoginPageObjects.class);
		String actualPageHeader = loginPageObjects.getDashboardPageHeader();
		logAction("Dashboard header text: " + actualPageHeader);
		assertThat(loginPageObjects.getDashboardPageHeaderLocator()).hasText(expectedHeader);
		logAction("Assertion passed: Header contains '" + expectedHeader + "'");

		// Check again if there are more records - using inherited helper
		if (hasMoreRecords()) {
			logLoopMessage();
			moveToNextRecord();
			executeScenarioForCurrentRecord(expectedHeader);
		} else {
			logCompletionMessage();
		}
	}
}
