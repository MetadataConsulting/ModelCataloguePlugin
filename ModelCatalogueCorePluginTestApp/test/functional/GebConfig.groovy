/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


import org.openqa.selenium.Platform
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
baseUrl = 'http://localhost:8080/ModelCatalogueCorePluginTestApp'

driver = {
    new FirefoxDriver()
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


    // run as "grails -Dgeb.env=chrome test-app"
    // See: http://code.google.com/p/selenium/wiki/ChromeDriver
    chrome {
        def chromeDriver = new File('test/drivers/chrome/chromedriver')
        downloadDriver(chromeDriver, "http://chromedriver.googlecode.com/files/chromedriver_mac_23.0.1240.0.zip")
        System.setProperty('webdriver.chrome.driver', chromeDriver.absolutePath)
        driver = { new ChromeDriver() }
    }

    // run as "grails -Dgeb.env=firefox test-app"
    // See: http://code.google.com/p/selenium/wiki/FirefoxDriver
    firefox {
        driver = { new FirefoxDriver() }
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
        DesiredCapabilities capabillities = DesiredCapabilities.chrome();
        capabillities.setCapability("name", "ModelCatalogueCoreTestApp");
        capabillities.setCapability("platform", Platform.LINUX);
        capabillities.setCapability("selenium-version", "2.40.0");
        driver = { new RemoteWebDriver(new URL("http://${username}:${apiKey}@ondemand.saucelabs.com:80/wd/hub"), capabillities) }
    }
}