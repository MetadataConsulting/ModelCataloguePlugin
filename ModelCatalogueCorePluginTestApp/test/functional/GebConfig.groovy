/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

ChromeOptions options = new ChromeOptions()
options.addArguments("test-type")
options.addArguments("--disable-extensions")

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
//baseUrl = 'http://localhost:8080/'
baseUrl = 'https://gel-mc-test.metadata.org.uk/'

ChromeDriverManager.getInstance().setup()

driver = {
    new ChromeDriver(options)
}

waiting {
    timeout = 15
    retryInterval = 0.6
}

atCheckWaiting = true
