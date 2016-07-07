/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.MarionetteDriverManager

import java.util.logging.Level

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
baseUrl = 'http://localhost:8080/ModelCatalogueCorePluginTestApp/'

ChromeDriverManager.getInstance().setup()
MarionetteDriverManager.getInstance().setup()


// See: http://code.google.com/p/selenium/wiki/ChromeDriver
driver = {
    DesiredCapabilities caps = DesiredCapabilities.chrome()
    LoggingPreferences logPrefs = new LoggingPreferences()
    logPrefs.enable(LogType.BROWSER, Level.WARNING)
    caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs)
    new ChromeDriver(caps)
}

waiting {
    timeout = 15
    retryInterval = 0.6
}
// Default to wraping `at SomePage` declarations in `waitFor` closures
atCheckWaiting = true

// Download the driver and set it up automatically

private void downloadDriver(File file, String path) {
    if (!file.exists()) {
        def ant = new AntBuilder()
        ant.get(src: path, dest: 'driver.zip')
        ant.unzip(src: 'driver.zip', dest: file.parent)
        ant.delete(file: 'driver.zip')
        ant.chmod(file: file, perm: '700')
    }
}

environments {


    // run as "grails -Dgeb.env=firefox test-app"
    firefox {
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.WARNING);
        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        caps.setCapability("marionette", true);
        new FirefoxDriver(caps)
    }

    sauce {
        waiting {
            timeout = 15
            retryInterval = 1
        }

        String username = System.getenv("SAUCE_USER_NAME");
        String apiKey = System.getenv("SAUCE_API_KEY");
        if(username == null || apiKey == null){
            System.err.println("Sauce OnDemand credentials not set.");
        }

        DesiredCapabilities caps = DesiredCapabilities.chrome();
        caps.setCapability("name", "ModelCatalogueCoreTestApp");
        caps.setCapability("platform", "Linux");
        driver = {
            new RemoteWebDriver(new URL("http://${username}:${apiKey}@ondemand.saucelabs.com:80/wd/hub"), caps)
        }
    }
}
