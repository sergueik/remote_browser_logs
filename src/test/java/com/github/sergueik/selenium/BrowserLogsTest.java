package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class BrowserLogsTest extends BaseTest {

	private static final String filePath = "logger.html";

	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {
		assertThat("Driver object should be defined", driver, notNullValue());
	}

	@Test(description = "Opens the local file", enabled = true)
	public void consoleLogTest() {
		String url = getPageContent(filePath);
		assertThat(String.format("Testing local file: \"%s\"", url), url,
				notNullValue());
		driver.navigate().to(url);
		WebElement element = driver
				.findElement(By.cssSelector("input[name=\"clock\"]"));
		final String script = "console.log('Test from client: ' + arguments[0].value); return";
		sleep(10000);
		executeScript(script, element);
		if (driver != null) {
			// hanging ?
			analyzeLog("After Test");
		}
	}

	protected void analyzeLog(String context) {
		List<Map<String, Object>> logData = super.analyzeLog();
		Set<Object> logLevels = new HashSet<>();
		logData.stream().forEach(row -> {
			logLevels.add(row.get("log_level"));
		});
		if (debug) {
			System.err.println(String.format("Analyze log %s:", context));
			for (Map<String, Object> dataRow : logData) {
				System.err.println("time stamp: " + dataRow.get("time_stamp").toString()
						+ "\t" + "log level: " + dataRow.get("log_level").toString() + "\t"
						+ "message: " + dataRow.get("message"));
			}
		}
		assertThat(logLevels,
				hasItems(new Object[] { Level.INFO, Level.SEVERE, Level.WARNING }));
	}
}
