package com.eventorback.crawler.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {

	public static AutoCloseableWebDriver createChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox");
		options.addArguments("--headless=new");
		options.addArguments("--no-zygote");
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-extensions");
		options.addArguments("--disable-crash-reporter");
		options.addArguments("--disable-in-process-stack-traces");
		options.addArguments("--disable-logging");
		options.addArguments("--disable-web-security");
		options.addArguments("--remote-debugging-port=0");
		options.addArguments("--window-size=1920,1080");

		// AutoCloseableWebDriver 래퍼로 감싸서 try-with-resources 지원
		WebDriver driver = new ChromeDriver(options);
		return new AutoCloseableWebDriver(driver);
	}
}