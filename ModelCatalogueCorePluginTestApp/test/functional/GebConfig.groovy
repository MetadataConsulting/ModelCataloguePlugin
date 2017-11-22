/*
 This is the Geb configuration file.
 See: http://www.gebish.org/manual/current/configuration.html
 */


import org.openqa.selenium.firefox.FirefoxDriver
import io.github.bonigarcia.wdm.ChromeDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.phantomjs.PhantomJSDriver




reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false
baseUrl = 'http://localhost:8080/'

//ChromeDriverManager.getInstance().setup()
println "GEB SETUP general"

println "GEB SETUP chrome"
//        ChromeOptions options = new ChromeOptions()
//        options.addArguments("start-maximized")
//        options.addArguments("window-size=1920,1080")
//        options.addArguments("test-type")
//        options.addArguments("--disable-extensions")
//        options.addArguments("headless")
//        options.addArguments("--disable-gpu")
//        driver = {  new ChromeDriver(options) }
//        ChromeDriverManager.getInstance().setup()

environments {

    chrome {
        println "GEB SETUP chrome"
        ChromeOptions options = new ChromeOptions()
        options.addArguments("start-maximized")
        options.addArguments("window-size=1920,1080")
        options.addArguments("test-type")
        options.addArguments("--disable-extensions")
        options.addArguments("headless")
        options.addArguments("--disable-gpu")
        driver = {  new ChromeDriver(options) }
        ChromeDriverManager.getInstance().setup()
    }

    phantomJs {
        println "GEB SETUP phantomJs"
        driver = { new PhantomJSDriver() }
    }

    firefox {
        println "GEB SETUP firefox"
        driver = { new FirefoxDriver() }
    }
}

waiting {
    timeout = 15
    retryInterval = 0.6
}

atCheckWaiting = true




