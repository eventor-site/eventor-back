package com.eventorback.crawler.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {

	public static AutoCloseableWebDriver createChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox");
		options.addArguments("--headless=new");
		options.addArguments("--no-zygote");   // 중요: 좀비 프로세스 방지
		options.addArguments("--disable-gpu");

		// AutoCloseableWebDriver 래퍼로 감싸서 try-with-resources 지원
		WebDriver driver = new ChromeDriver(options);
		return new AutoCloseableWebDriver(driver);
	}
}