package com.qa.factory;

import java.util.Properties;

import com.microsoft.playwright.*;

public class DriverFactory {
	public static ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
	public static ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
	public static ThreadLocal<BrowserContext> tlContext = new ThreadLocal<>();
	public static ThreadLocal<Page> tlPage = new ThreadLocal<>();
	
	public static synchronized Playwright getPlaywright() {
    	return tlPlaywright.get();
    }
	
	public static synchronized Browser getBrowser() {
		return tlBrowser.get();
	}
	
	public static synchronized BrowserContext getContext() {
		return tlContext.get();
	}
	
    public static synchronized Page getPage() {
        return tlPage.get();
    }
    
    public Page initPlaywrightEngin(Properties prop) {
    	String browserName = prop.getProperty("browser.name").trim();
    	boolean headless = Boolean.parseBoolean(prop.getProperty("execution.headless"));
    	
        Playwright playwright = Playwright.create();
        tlPlaywright.set(playwright);
        
        switch (browserName.toLowerCase()) {
            case "chromium":
            	tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
                break;
            case "Chrome":
            	tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless).setChannel("chrome")));
            	break;
            case "edge":
            	tlBrowser.set(getPlaywright().chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless).setChannel("msedge")));
            	break;
            case "firefox":
            	tlBrowser.set(getPlaywright().firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
                break;
            case "webkit":
            	tlBrowser.set(getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
            	break;
            case "safari":
            	tlBrowser.set(getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
            	break;
            default:
                throw new RuntimeException("Browser Name: " + browserName + " is not valid");
        }
        
        tlContext.set(getBrowser().newContext(new Browser.NewContextOptions().setIgnoreHTTPSErrors(true)));
        tlPage.set(getContext().newPage());
        return getPage();
    }
    
}
