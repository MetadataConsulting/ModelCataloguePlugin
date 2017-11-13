/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */
import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
baseUrl = 'http://localhost:8080/'
//baseUrl = 'https://gel-mc-test.metadata.org.uk/'

environments {

    chrome {
        ChromeOptions options = new ChromeOptions()
        options.addArguments("test-type")
        options.addArguments("--disable-extensions")

        ChromeDriverManager.getInstance().setup()
        driver = { new ChromeDriver() }
    }

    firefox {
        driver = { new FirefoxDriver() }
    }
}

waiting {
    timeout = 15
    retryInterval = 0.6
}

atCheckWaiting = true
