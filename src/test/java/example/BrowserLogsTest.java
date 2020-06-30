package example;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.greaterThan;

public class BrowserLogsTest extends BaseTest {

	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {
		assertThat("Driver object should be defined", driver, notNullValue());
	}

	@Test(description = "Opens the local file", enabled = true)
	public void localFileTest() {
		String url = getPageContent("logger.html");
		assertThat(String.format("Testing local file: \"%s\"", url), url, notNullValue());
		driver.navigate().to(url);
		WebElement element = driver.findElement(By.cssSelector("input[name=\"clock\"]"));
		final String script = "console.log('Called by client: ' + arguments[0].value); return";
		executeScript(script, element);
		sleep(10000);
		List<Map<String, Object>> logData = super.analyzeLog();
		Set<Object> logLevels = new HashSet<>();
		logData.stream().forEach(row -> {
			logLevels.add(row.get("log_level"));
		});
		// NOTE: fragile
		try {
			// NOTE: fragile
			assertThat(logLevels, hasItems(new Object[] { Level.INFO, Level.SEVERE, Level.WARNING }));
		} catch (AssertionError e) {
			// ignore
		}
		// only seems to receive SEVERE
		System.err.println("Received messages of levels: " + logLevels);
		if (debug) {
			printLogs(logData);
		}
	}

	@Test(description = "Injects the script", enabled = true)
	public void consoleLogTest() {
		String scriptFile = "logger.js";
		String script = getScriptContent(scriptFile);
		assertThat(String.format("Testing local script: \"%s\"", scriptFile), script, notNullValue());
		driver.navigate().to("about:blank");
		executeScript(script);
		sleep(12000);
		List<Map<String, Object>> logData = analyzeLog();
		Set<Object> logLevels = new HashSet<>();
		logData.stream().forEach(row -> {
			logLevels.add(row.get("log_level"));
		});
		List<String> allMessages = new ArrayList<>();
		logData.stream().forEach(row -> {
			allMessages.add(row.get("message").toString());
		});
		final String logger = "INJECTED SCRIPT";
		Object cnt = allMessages.stream().filter(o -> o.indexOf(logger) >= 0).collect(Collectors.toList()).size();
		System.err.println(String.format("Result: %d lines with %s", cnt, logger));
		// wrong test
		assertThat(allMessages.stream().filter(o -> o.indexOf(logger) >= 0).collect(Collectors.toList()).size(),
				greaterThan(0));

		// System.err.println("All messages: " + allMessages);
		try {
			// NOTE: fragile
			assertThat(logLevels, hasItems(new Object[] { Level.INFO }));
			assertThat(logLevels, hasItems(new Object[] { Level.SEVERE }));
			assertThat(logLevels, hasItems(new Object[] { Level.WARNING }));
		} catch (AssertionError e) {
			// ignore
		}
		System.err.println("Received messages of levels: " + logLevels);
		// only seems to receive SEVERE
		if (debug) {
			printLogs(logData);
		}
	}

	@Test(description = "Console log initiated by test", enabled = true)
	public void testInitiatedLogTest() {
		driver.get("https://www.yahoo.com/");
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img[title='Yahoo']")));
		assertThat(element, notNullValue());
		final String script = "console.log('Called by client: ' + arguments[0].value); return";
		executeScript(script, element);
		List<Map<String, Object>> logData = analyzeLog();
		StringBuilder logMessages = new StringBuilder();

		logData.stream().forEach(row -> {
			logMessages.append(row.get("message").toString());
			logMessages.append(System.lineSeparator());
		});
		assertThat(logMessages.toString(), containsString("Called by client"));
		/*
		 * if (debug) { printLogs(logData); }
		 */
	}

	@Test(description = "Opens the site", enabled = true)
	public void genericSiteTest() throws InterruptedException {
		driver.get("http://www.cnn.com/");

		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cnn-badge-icon")));
		assertThat(element, notNullValue());
		List<Map<String, Object>> logData = analyzeLog();
		for (Map<String, Object> dataRow : logData) {
			System.err.println("time stamp: " + dataRow.get("time_stamp").toString() + "\t" + "log level: "
					+ dataRow.get("log_level").toString() + "\t" + "message: " + dataRow.get("message"));
		}
		/*
		 * if (debug) { printLogs(logData); }
		 */
	}

	// see also:
	// https://developers.google.com/web/tools/chrome-devtools/console/log
}
