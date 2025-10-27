package com.eventorback.crawler.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverFactory {

	public static AutoCloseableWebDriver createChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox");
		options.addArguments("--headless=new");
		options.addArguments("--no-zygote");   // 좀비 프로세스 방지
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-dev-shm-usage");  // 공유 메모리 사용 안함
		options.addArguments("--disable-extensions");     // 확장 프로그램 비활성화
		options.addArguments("--disable-background-timer-throttling");
		options.addArguments("--disable-backgrounding-occluded-windows");
		options.addArguments("--disable-renderer-backgrounding");
		options.addArguments("--single-process");         // 단일 프로세스 모드

		// AutoCloseableWebDriver 래퍼로 감싸서 try-with-resources 지원
		WebDriver driver = new ChromeDriver(options);
		return new AutoCloseableWebDriver(driver);
	}
}