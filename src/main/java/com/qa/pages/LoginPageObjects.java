package com.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPageObjects extends BasePage {

	// Page-specific locators only (no duplication!)
	private final Locator usernameField;
	private final Locator passwordField;
	private final Locator loginBtn;
	private final Locator dashboardPageHeader;
	private final Locator menuBtn;
	private final Locator logoutBtn;

	// Constructor - calls parent constructor
	public LoginPageObjects(Page page) {
		super(page); // Inherited: page, configReader, prop initialization
		// Only page-specific locators
		this.usernameField = page.locator("#user-name");
		this.passwordField = page.locator("#password");
		this.loginBtn = page.locator("id=login-button");
		this.dashboardPageHeader = page.locator(".title");
		this.menuBtn = page.locator("#react-burger-menu-btn");
		this.logoutBtn = page.locator("#logout_sidebar_link");
	}

	// Page-specific actions only
	public void enterUsername(String username) {
		usernameField.fill(username);
	}

	public void enterPassword(String password) {
		passwordField.fill(password);
	}

	public void clickOnLoginButton() {
		loginBtn.click();
	}

	public String getDashboardPageHeader() {
		return dashboardPageHeader.innerText();
	}

	public Locator getDashboardPageHeaderLocator() {
		return dashboardPageHeader;
	}

	/**
	 * Logout from the application and return to login page
	 * Clicks the menu button, then the logout link
	 */
	public void logout() {
		menuBtn.click();
		logoutBtn.click();
		// Wait for navigation back to login page
		page.waitForURL(url -> url.toString().contains("saucedemo.com"));
	}
}
