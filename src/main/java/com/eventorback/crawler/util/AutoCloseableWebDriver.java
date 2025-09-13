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
		driver.get(url);
	}

	@Override
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	@Override
	public String getTitle() {
		return driver.getTitle();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	@Override
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}

	@Override
	public String getPageSource() {
		return driver.getPageSource();
	}

	@Override
	public void close() {
		try {
			driver.quit();
		} catch (Exception ignored) {

		} finally {
			killZombieChrome();
		}
	}

	@Override
	public void quit() {
		try {
			driver.quit();
		} catch (Exception ignored) {

		} finally {
			killZombieChrome();
		}
	}

	@Override
	public Set<String> getWindowHandles() {
		return driver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		return driver.switchTo();
	}

	@Override
	public Navigation navigate() {
		return driver.navigate();
	}

	@Override
	public Options manage() {
		return driver.manage();
	}

	private void killZombieChrome() {
		try {
			ProcessHandle.allProcesses()
				.filter(ph -> ph.info().command().isPresent() &&
					ph.info().command().get().contains("chromedriver"))
				.forEach(ProcessHandle::destroy);
		} catch (Exception ignored) {
		}
	}
}
