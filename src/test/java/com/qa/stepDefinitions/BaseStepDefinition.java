package com.qa.stepDefinitions;

import java.util.Map;
import java.util.Properties;

import com.qa.factory.DriverFactory;
import com.qa.utils.ConfigReader;
import com.qa.utils.ScenarioContext;

/**
 * Base Step Definition class
 * Provides common functionality for all step definition classes
 * Eliminates boilerplate code across step definition classes
 * 
 * Benefits:
 * - Centralized ScenarioContext dependency injection
 * - Common helper methods (getTestData())
 * - Centralized browser lifecycle management
 * - Common logging/assertion patterns
 * - Iteration management helpers
 */
public abstract class BaseStepDefinition {

    protected ScenarioContext scenarioContext;

    /**
     * Constructor - ScenarioContext is injected by Cucumber's PicoContainer
     * All step definition classes inherit this and have access to scenarioContext
     * 
     * @param scenarioContext - Shared data holder across hooks and steps
     */
    public BaseStepDefinition(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Helper method to get current test data from ScenarioContext
     * Centralizes access to test data - used by all step definitions
     * Hides ScenarioContext implementation details from step classes
     * 
     * @return Map containing current record's data from JSON file
     */
    protected Map<String, Object> getTestData() {
        return scenarioContext.getTestData();
    }

    /**
     * Helper method to check if more records exist
     * 
     * @return true if there are more data records to process
     */
    protected boolean hasMoreRecords() {
        return scenarioContext.hasMoreRecords();
    }

    /**
     * Helper method to move to next data record
     * Updates current record and index in context
     */
    protected void moveToNextRecord() {
        scenarioContext.moveToNextRecord();
    }

    /**
     * Helper method to get current iteration info
     * 
     * @return String like "Iteration 1/2"
     */
    protected String getIterationInfo() {
        int current = scenarioContext.getCurrentDataIndex() + 1;
        int total = scenarioContext.getTotalDataRecords();
        return "Iteration " + current + "/" + total;
    }

    /**
     * Generic helper to initialize any page object
     * Eliminates repetitive: pageObject = new PageObject(DriverFactory.getPage());
     * 
     * Works with any page object class that has a constructor accepting Page object
     * 
     * Usage:
     * loginPageObjects = initializePage(LoginPageObjects.class);
     * productPageObjects = initializePage(ProductPageObjects.class);
     * cartPageObjects = initializePage(CartPageObjects.class);
     * 
     * @param <T>       - Page object type (must extend BasePage and have Page
     *                  constructor)
     * @param pageClass - The page object class to instantiate
     * @return Initialized page object with current Playwright Page
     * @throws RuntimeException if page object cannot be instantiated
     */
    protected <T> T initializePage(Class<T> pageClass) {
        try {
            return pageClass.getConstructor(com.microsoft.playwright.Page.class)
                    .newInstance(DriverFactory.getPage());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize page object: " + pageClass.getName()
                            + ". Ensure it has a constructor that accepts Page parameter.",
                    e);
        }
    }

    /**
     * Close browser completely and restart fresh
     * Used between iterations in data-driven testing
     * Centralizes browser cleanup logic
     */
    protected void restartBrowser() {
        System.out.println("    > Closing browser after iteration...");

        try {
            if (DriverFactory.getPage() != null) {
                DriverFactory.getPage().close();
            }
        } catch (Exception e) {
            System.out.println("    > Page already closed: " + e.getMessage());
        }

        try {
            if (DriverFactory.getContext() != null) {
                DriverFactory.getContext().close();
            }
        } catch (Exception e) {
            System.out.println("    > Context already closed: " + e.getMessage());
        }

        try {
            if (DriverFactory.getBrowser() != null) {
                DriverFactory.getBrowser().close();
            }
        } catch (Exception e) {
            System.out.println("    > Browser already closed: " + e.getMessage());
        }

        try {
            if (DriverFactory.getPlaywright() != null) {
                DriverFactory.getPlaywright().close();
            }
        } catch (Exception e) {
            System.out.println("    > Playwright already closed: " + e.getMessage());
        }

        System.out.println("    > Browser terminated successfully");

        // Relaunch browser for next iteration
        System.out.println("    > Relaunching browser for next iteration...");
        DriverFactory driverFactory = new DriverFactory();
        ConfigReader configReader = new ConfigReader();
        Properties prop = configReader.init_prop();
        driverFactory.initPlaywrightEngin(prop);
        System.out.println("    > Browser launched successfully");
    }

    /**
     * Log iteration start message
     * 
     * @param message - Custom message to log
     */
    protected void logIterationStart(String message) {
        System.out.println("\n>>> [" + getIterationInfo() + "] " + message);
    }

    /**
     * Log step action message with indentation
     * 
     * @param message - Action message to log
     */
    protected void logAction(String message) {
        System.out.println("    > " + message);
    }

    /**
     * Log data-driven loop message
     */
    protected void logLoopMessage() {
        System.out.println("\n>>> [Data-Driven Loop] More records found. Repeating scenario steps...");
    }

    /**
     * Log completion message
     */
    protected void logCompletionMessage() {
        System.out.println("\n>>> All test data records have been processed successfully!");
    }
}