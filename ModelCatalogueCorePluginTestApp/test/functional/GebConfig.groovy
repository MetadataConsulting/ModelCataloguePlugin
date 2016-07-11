/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.MarionetteDriverManager
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.MarionetteDriver
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

import java.util.logging.Level

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
baseUrl = 'http://localhost:8080/ModelCatalogueCorePluginTestApp/'

// Default to wraping `at SomePage` declarations in `waitFor` closures
atCheckWaiting = true

MarionetteDriverManager.instance.setup()
ChromeDriverManager.instance.setup()

driver = {
    new MarionetteDriver()
}

waiting {
    timeout = 15
    retryInterval = 0.6
}

environments {


    // run as "grails -Dgeb.env=chrome test-app"
    // See: http://code.google.com/p/selenium/wiki/ChromeDriver
    chrome {
        driver = {
            DesiredCapabilities caps = DesiredCapabilities.chrome();
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            logPrefs.enable(LogType.CLIENT, Level.ALL);
            logPrefs.enable(LogType.DRIVER, Level.ALL);
            caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
            new ChromeDriver(caps);
        }
    }

    firefox {
        driver = {
            new FirefoxDriver()
        }
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
