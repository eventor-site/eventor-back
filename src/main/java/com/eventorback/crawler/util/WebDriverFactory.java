package com.eventorback.crawler.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {

	public static AutoCloseableWebDriver createChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-extensions");
		options.addArguments("--disable-background-timer-throttling");
		options.addArguments("--disable-backgrounding-occluded-windows");
		options.addArguments("--disable-renderer-backgrounding");
		options.addArguments("--disable-features=TranslateUI");
		options.addArguments("--disable-ipc-flooding-protection");
		options.addArguments("--force-device-scale-factor=1");
		options.addArguments("--window-size=1920,1080");
		options.setExperimentalOption("useAutomationExtension", false);
		options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

		WebDriver driver = new ChromeDriver(options);
		return new AutoCloseableWebDriver(driver);
	}
}