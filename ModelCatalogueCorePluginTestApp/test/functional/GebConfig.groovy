/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
baseUrl = 'http://localhost:8080/'
cacheDriver = false

ChromeDriverManager.getInstance().setup()

driver = {
    new ChromeDriver()
}

waiting {
    timeout = 15
    retryInterval = 0.6
}

atCheckWaiting = true
