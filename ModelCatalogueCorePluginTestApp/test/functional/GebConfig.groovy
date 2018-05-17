/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/#configuration
*/
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.CapabilityType

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

waiting {
    timeout = 20
}

environments {

    // run via “./gradlew chromeTest”
    // See: http://code.google.com/p/selenium/wiki/ChromeDriver
    chrome {
        if (System.getProperty('downloadFilepath')) {
            String downloadFilepath = System.getProperty('downloadFilepath')
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>()
            chromePrefs.put("profile.default_content_settings.popups", 0)
            chromePrefs.put("download.default_directory", downloadFilepath)

            ChromeOptions options = new ChromeOptions()
            options.setExperimentalOption("prefs", chromePrefs)
            options.addArguments("--test-type")
            options.addArguments("--disable-extensions") //to disable browser extension popup

            DesiredCapabilities cap = DesiredCapabilities.chrome()
            cap.setCapability(ChromeOptions.CAPABILITY, options)
            cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)

            driver = { new ChromeDriver(cap) }
        } else {
            driver = { new ChromeDriver() }
        }
    }

    // run via “./gradlew chromeHeadlessTest”
    // See: http://code.google.com/p/selenium/wiki/ChromeDriver
    chromeHeadless {
        ChromeOptions o = new ChromeOptions()
        o.addArguments('--headless')
        driver = { new ChromeDriver(o) }
    }

    phantomjs {
        DesiredCapabilities caps = new DesiredCapabilities()
        List<String> cliArgsCap = []
        cliArgsCap.add("--web-security=no")
        cliArgsCap.add("--ssl-protocol=any")
        cliArgsCap.add("--ignore-ssl-errors=yes")
        cliArgsCap.add("--webdriver-logfile=/tmp/phantomjsdriver.log")
        cliArgsCap.add("--webdriver-loglevel=ERROR")
        caps.setJavascriptEnabled(true)
        caps.setCapability("takesScreenshot", true)
        caps.setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "/usr/local/share/phantomjs"
        )
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap)
        PhantomJSDriver driver = new PhantomJSDriver(caps)

        driver.manage().window().maximize()
        driver.switchTo().window(driver.getWindowHandle())
        return driver
    }

}

baseNavigatorWaiting = true
atCheckWaiting = true