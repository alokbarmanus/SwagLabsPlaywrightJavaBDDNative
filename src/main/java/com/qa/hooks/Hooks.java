package com.qa.hooks;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.qa.factory.DriverFactory;
import com.qa.utils.ConfigReader;
import com.qa.utils.JsonDataReader;
import com.qa.utils.ScenarioContext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
	private ConfigReader configReader;
	private Properties prop;
	private ScenarioContext scenarioContext;

	/**
	 * Constructor - Initialize ScenarioContext
	 * ScenarioContext is passed by Cucumber's PicoContainer for dependency
	 * injection
	 */
	public Hooks(ScenarioContext scenarioContext) {
		this.scenarioContext = scenarioContext;
	}

	/**
	 * Order 0: Read configuration properties
	 */
	@Before(order = 0)
	public void getProperty() {
		configReader = new ConfigReader();
		prop = configReader.init_prop();
	}

	/**
	 * Order 1: Check if scenario has @dataFile tag and read JSON data
	 * This hook reads the JSON file ONLY ONCE on the first iteration
	 * Data is stored in ScenarioContext which persists across iterations
	 * 
	 * IMPORTANT: PicoContainer creates a new Hooks instance per iteration,
	 * so we store testDataList in ScenarioContext (shared across iterations)
	 * to enable proper data-driven execution with multiple JSON records
	 * 
	 * @param scenario - Current Cucumber scenario
	 */
	@Before(order = 1)
	public void readJsonDataFile(Scenario scenario) {
		// Check if JSON has already been read in a previous iteration
		// If yes, skip reading again (data is managed by @After hook)
		if (scenarioContext.hasTestDataList()) {
			// JSON already read and stored in context
			// Data iteration is handled by @After hook
			return;
		}

		// Check if scenario has @dataFile tag
		String dataFilePath = null;

		for (String tag : scenario.getSourceTagNames()) {
			if (tag.startsWith("@dataFile:")) {
				// Extract file path from tag: @dataFile:env/{env}/data/loginData.json
				dataFilePath = tag.replace("@dataFile:", "");
				break;
			}
		}

		if (dataFilePath != null) {
			// Replace {env} placeholder with actual environment
			String environment = prop.getProperty("env", "dev");
			dataFilePath = dataFilePath.replace("{env}", environment);

			// Resolve absolute path from resources folder
			String projectRoot = System.getProperty("user.dir");
			String absolutePath = projectRoot + File.separator + "src" + File.separator + "test"
					+ File.separator + "resources" + File.separator + dataFilePath;

			// Read JSON file (FIRST TIME ONLY)
			System.out.println("\n>>> Reading JSON data from: " + absolutePath);
			JsonDataReader jsonDataReader = new JsonDataReader(absolutePath);
			List<Map<String, Object>> testDataList = jsonDataReader.getDataAsListOfMaps();

			// Store testDataList in context (persists across iterations)
			scenarioContext.setTestDataList(testDataList);
			scenarioContext.setTotalDataRecords(testDataList.size());

			System.out.println(">>> Total test data records: " + testDataList.size());

			// Set the first data record in context
			if (!testDataList.isEmpty()) {
				scenarioContext.setCurrentDataIndex(0);
				scenarioContext.setTestData(testDataList.get(0));
				System.out.println(">>> Setting test data [Record 1 of " + testDataList.size() + "]");
			}
		}
	}

	/**
	 * Order 2: Launch browser for each iteration
	 */
	@Before(order = 2)
	public void launchBrowser() {
		DriverFactory driverFactory = new DriverFactory();
		driverFactory.initPlaywrightEngin(prop);
	}

	/**
	 * Order 0 (After): Close browser after scenario execution
	 * All iteration handling is done within step definitions using recursive
	 * looping
	 * 
	 * NOTE: During data-driven looping, restartBrowser() already closes the page.
	 * This hook adds null checks to handle already-closed resources gracefully.
	 */
	@After(order = 0)
	public void quitBrowser(Scenario scenario) {
		try {
			// Close page if it exists
			if (DriverFactory.getPage() != null) {
				DriverFactory.getPage().close();
				System.out.println(">>> Page closed");
			}
		} catch (Exception e) {
			System.out.println(">>> Page already closed or error: " + e.getMessage());
		}

		try {
			// Close context if it exists
			if (DriverFactory.getContext() != null) {
				DriverFactory.getContext().close();
				System.out.println(">>> Context closed");
			}
		} catch (Exception e) {
			System.out.println(">>> Context already closed or error: " + e.getMessage());
		}

		try {
			// Close browser if it exists
			if (DriverFactory.getBrowser() != null) {
				DriverFactory.getBrowser().close();
				System.out.println(">>> Browser closed");
			}
		} catch (Exception e) {
			System.out.println(">>> Browser already closed or error: " + e.getMessage());
		}

		try {
			// Close playwright if it exists
			if (DriverFactory.getPlaywright() != null) {
				DriverFactory.getPlaywright().close();
				System.out.println(">>> Playwright closed");
			}
		} catch (Exception e) {
			System.out.println(">>> Playwright already closed or error: " + e.getMessage());
		}

		System.out.println("\n>>> Scenario completed and browser cleanup finished");
	}
}
