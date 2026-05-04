package com.qa.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

	private Properties prop;

	public Properties init_prop() {

		prop = new Properties();
		try {
			// Load application.properties configuration using try-with-resources for
			// auto-close
			try (FileInputStream ip = new FileInputStream("./src/test/resources/env/application.properties")) {
				prop.load(ip);
			}

			// Get environment from system properties (e.g. mvn test -Denv="sit")
			String envName = System.getProperty("env");

			// Fall back to default env in application.properties if -Denv is not provided
			if (envName == null) {
				envName = prop.getProperty("default.env");
			}

			// Load environment properties configuration
			if (envName != null) {
				System.out.println("Loading configuration for environment: " + envName);
				String envPath = "./src/test/resources/env/" + envName + "/env.properties";
				try (FileInputStream ipEnv = new FileInputStream(envPath)) {
					// This load overrides existing keys from application.properties
					prop.load(ipEnv);
				}
			}

			// Override with Maven system properties (e.g., -Dexecution.headless=true)
			// This ensures CLI parameters take precedence over file-based properties
			String headlessProperty = System.getProperty("execution.headless");
			if (headlessProperty != null) {
				System.out.println("Overriding execution.headless from Maven: " + headlessProperty);
				prop.setProperty("execution.headless", headlessProperty);
			}
		} catch (IOException e) {
			System.out.println("ConfigReader: Error loading properties. Using defaults. " + e.getMessage());
			e.printStackTrace();
		}
		return prop;
	}
}
