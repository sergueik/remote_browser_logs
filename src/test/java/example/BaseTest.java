package example;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

public class BaseTest {

	protected static String osName = getOSName();
	protected static final String driverPath = Paths.get(System.getProperty("user.home")).resolve("Downloads")
			.resolve(osName.equals("windows") ? "chromedriver.exe" : "chromedriver").toAbsolutePath().toString();
	protected final boolean debug = (System.getenv("DEBUG") != null);
	public WebDriverWait wait;
	public RemoteWebDriver driver;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() throws InterruptedException, MalformedURLException {
		final LoggingPreferences loggingPreferences = new LoggingPreferences();

		System.setProperty("webdriver.chrome.driver", driverPath);
		loggingPreferences.enable(LogType.BROWSER, Level.ALL);
		final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
		driver = new ChromeDriver(capabilities);
		wait = new WebDriverWait(driver, 60);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@AfterMethod
	public void afterMethod() {
		// there is probably no way to detect that
		if (debug) {
			sleep(10000);
		}
	}

	@AfterTest(alwaysRun = true, enabled = true)
	// this annotated method scheduled to run after the execution of all the test
	// methods present in the classes
	public void afterTest() {
	}

	@AfterClass(alwaysRun = true, enabled = true)
	public void afterClass() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	protected List<Map<String, Object>> analyzeLog() {
		final List<Map<String, Object>> logs = new ArrayList<>();
		final Map<String, Object> row = new HashMap<>();
		final LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		// TODO: sqlite ? ELK ?
		if (debug) {
			System.err.println("Collecting " + logEntries.getAll().size() + " entries");
			System.err.println("Collecting(raw data): " + logEntries.getAll());

		}
		Iterator<LogEntry> logEntryIterator = logEntries.iterator();

		while (logEntryIterator.hasNext()) {
			LogEntry entry = logEntryIterator.next();
			row.clear();
			row.put("time_stamp", new Date(entry.getTimestamp()));
			row.put("log_level", entry.getLevel());
			row.put("message", entry.getMessage());
			logs.add(row);
		}
		return logs;
	}

	protected void printLogs(List<Map<String, Object>> logData) {
		for (Map<String, Object> row : logData) {
			System.err.println(String.format("%s : %s %s ", row.get("time_stamp").toString(), row.get("log_level"),
					row.get("message")));
		}
	}

	protected static JSONObject extractObject(HttpResponse httpResponse) throws IOException, JSONException {
		InputStream contents = httpResponse.getEntity().getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(contents, writer, "UTF8");
		return new JSONObject(writer.toString());
	}

	protected static String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}
		final Pattern p = Pattern.compile("\\$(?:\\{(?:env:)?(\\w+)\\}|(\\w+))");
		final Matcher m = p.matcher(input);
		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb, null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// NOTE: cannot make this version static
	protected String getPageContent(String pagename) {
		try {
			URI uri = this.getClass().getClassLoader().getResource(pagename).toURI();
			if (debug) {
				System.err.println("Testing local file: " + uri.toString());
			}
			return uri.toString();
		} catch (URISyntaxException | NullPointerException e) {
			if (debug) {
				// mask the exception when debug
				return String.format("file:///%s/target/test-classes/%s", System.getProperty("user.dir"), pagename);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	protected Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	protected static String getScriptContent(String scriptName) {
		try {
			final InputStream stream = BaseTest.class.getClassLoader().getResourceAsStream(scriptName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(scriptName);
		}
	}

	protected void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Utilities
	protected static String getOSName() {
		if (osName == null) {
			osName = System.getProperty("os.name").toLowerCase();
			if (osName.startsWith("windows")) {
				osName = "windows";
			}
		}
		return osName;
	}

}
