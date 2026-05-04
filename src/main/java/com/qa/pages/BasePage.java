package com.qa.pages;

import java.util.Properties;

import com.microsoft.playwright.Page;
import com.qa.utils.ConfigReader;

/**
 * Base Page Object Model class
 * Provides common functionality for all page object classes
 * Eliminates code duplication across page classes
 * 
 * Benefits:
 * - Centralized page initialization
 * - Common navigation methods
 * - Consistent locator management
 * - Configuration access across all pages
 */
public abstract class BasePage {

    protected Page page;
    protected ConfigReader configReader;
    protected Properties prop;

    /**
     * Constructor - Initialize page and configuration
     * Called by all child page classes
     * 
     * @param page - Playwright Page object
     */
    public BasePage(Page page) {
        this.page = page;
        this.configReader = new ConfigReader();
        this.prop = configReader.init_prop();
    }

    /**
     * Get the base URL from configuration
     * 
     * @return Base URL of the application
     */
    protected String getBaseUrl() {
        return prop.getProperty("base.url");
    }

    /**
     * Launch application - Navigate to base URL
     * Can be overridden in child classes if custom launch logic needed
     */
    public void launchApplication() {
        String appUrl = getBaseUrl();
        page.navigate(appUrl);
    }

    /**
     * Get Page object for direct Playwright access if needed
     * 
     * @return Playwright Page object
     */
    public Page getPage() {
        return page;
    }

    /**
     * Get configuration properties
     * 
     * @return Properties object
     */
    public Properties getProperties() {
        return prop;
    }
}
