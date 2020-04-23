### Info
This project exercises Selenium driver [logging](https://code.google.com/p/selenium/wiki/Logging) functionality.


### Usage

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
* Works with Selenium versions:
  - __2.53.1__
  - __2.44.0__
* Does not work with Selenium version:
  - __3.13.0__

### Testing
```sh
mvn -P2.53.0 clean test
```
```sh
mvn -P2.44.0 clean test
```
### Note
Technically Chrome and Firefox are supported. Temporarily project was cleaned up to use Chrome.

### See Also
 - [Log entries](https://logentries.com/doc/java/)
 - [ChromeDriver Capabilities ](https://sites.google.com/a/chromium.org/chromedriver/capabilities)
 - [Capturing Browser logs with-Selenium] (http://stackoverflow.com/questions/25431380/capturing-browser-logs-with-selenium)
 - [klepikov/Test.java](https://gist.github.com/klepikov/5457750)
 - [discussion of missing C# bindings for WebDriver logging](https://code.google.com/p/selenium/issues/detail?id=6832)
 - interacing with JSErrorCollector.xpi [java](https://github.com/mguillem/JSErrorCollector) client
 - interacing with JSErrorCollector.xpi [.net](https://github.com/protectedtrust/JSErrorCollector.NET) client
 * [intro to Selenium Webdriver logging](https://comaqa.gitbook.io/selenium-webdriver-lectures/selenium-webdriver.-problemnye-momenty/loggirovanie-v-selenium-webdriver)(in Russian)

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)

