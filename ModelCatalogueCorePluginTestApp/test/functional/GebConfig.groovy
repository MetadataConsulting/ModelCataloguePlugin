/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/#configuration
*/
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.CapabilityType

waiting {
    timeout = 2
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
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
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

}

baseNavigatorWaiting = true
atCheckWaiting = true