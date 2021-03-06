### Info
This project exercises Selenium driver [logging](https://code.google.com/p/selenium/wiki/Logging) functionality.

### Usage

![Ubuntu Example](https://github.com/sergueik/remote_browser_logs/blob/master/screenshots/browser_logs.png)

The driver instance is created with a instance of specific `LoggingPreferences` class in `DesiredCapabilities`:

```java
DesiredCapabilities capabilities;
LoggingPreferences loggingPreferences = new LoggingPreferences();

capabilities = DesiredCapabilities.chrome();
loggingPreferences.enable(LogType.BROWSER, Level.ALL);
capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
driver = new ChromeDriver(capabilities);
```
At the end of the test collect the logs:
```java
LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
for (LogEntry entry : logEntries) {
	System.err.println("time stamp: " + new Date(entry.getTimestamp()) + "\t" + "log level: "
	+ entry.getLevel() + "\t" + "message: " + entry.getMessage());
	}
```
for more details see the source

* Works with Selenium versions (including legacy releases):
  - __2.53.1__
  - __2.44.0__
  - __3.13.0__
  - __4.0.0.alpha-6__

Note: with __3.13.0__ Selenium only appear to capture the __[SEVERE]__ and __[WARNING]__ level logs,
the __[INFO]__ ones appear to be ignored

### Testing

```sh
mvn clean test
```
```sh
mvn -P2.53.0 clean test
```
```sh
mvn -P2.44.0 clean test
```
### Note
Technically Chrome and Firefox are supported. Temporarily project was cleaned up to use Chrome. To access Firefox compatible *legacy* version  switch to `legacy` branch in git and explore the `legacy` directory in the project.

### See Also
 - [Log entries](https://logentries.com/doc/java/)
 - [ChromeDriver Capabilities ](https://sites.google.com/a/chromium.org/chromedriver/capabilities)
 - [Capturing Browser logs with-Selenium] (http://stackoverflow.com/questions/25431380/capturing-browser-logs-with-selenium)
 - [klepikov/Test.java](https://gist.github.com/klepikov/5457750)
 - [discussion of missing C# bindings for WebDriver logging](https://code.google.com/p/selenium/issues/detail?id=6832)
 - interacing with JSErrorCollector.xpi [java](https://github.com/mguillem/JSErrorCollector) client
 - interacing with JSErrorCollector.xpi [.net](https://github.com/protectedtrust/JSErrorCollector.NET) client
 * [intro to Selenium Webdriver logging](https://comaqa.gitbook.io/selenium-webdriver-lectures/selenium-webdriver.-problemnye-momenty/loggirovanie-v-selenium-webdriver)(in Russian)
 * another blog about [collecting browser console and network logs](https://automated-testing.info/t/logi-brauzera-so-vkladok-network-i-console-i-dobavlenie-ih-v-allure/24130) (in Russian)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)

