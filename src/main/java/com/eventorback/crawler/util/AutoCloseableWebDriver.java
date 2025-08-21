package com.eventorback.crawler.util;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AutoCloseableWebDriver implements WebDriver, AutoCloseable {
	private final WebDriver driver;

	public AutoCloseableWebDriver(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public void get(String url) {

	}

	@Override
	public String getCurrentUrl() {
		return "";
	}

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public List<WebElement> findElements(By by) {
		return List.of();
	}

	@Override
	public WebElement findElement(By by) {
		return null;
	}

	@Override
	public String getPageSource() {
		return "";
	}

	@Override
	public void close() {
		driver.quit();
	}

	@Override
	public void quit() {
		driver.quit();
	}

	@Override
	public Set<String> getWindowHandles() {
		return Set.of();
	}

	@Override
	public String getWindowHandle() {
		return "";
	}

	@Override
	public TargetLocator switchTo() {
		return null;
	}

	@Override
	public Navigation navigate() {
		return null;
	}

	@Override
	public Options manage() {
		return driver.manage();
	}
}
