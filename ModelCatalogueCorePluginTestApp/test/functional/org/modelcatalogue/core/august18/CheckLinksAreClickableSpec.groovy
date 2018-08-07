package org.modelcatalogue.core.august18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1490')
@Title('Check links are clickable')
@Stepwise
class CheckLinksAreClickableSpec extends GebSpec {


    def "login as supervisor"() {
        when: 'login as a supervisor'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "check page titles of different sponsers"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.sponsorLink(0)
        sleep(2_000)
        ArrayList<String> tabs2 = driver.getWindowHandles()
        then:
        driver.switchTo().window(tabs2.get(1))
        browser.title.contains("Genomics England")

        when:
        driver.close()
        driver.switchTo().window(tabs2.get(0))
        dashboardPage = browser.page DashboardPage
        dashboardPage.sponsorLink(1)
        tabs2 = driver.getWindowHandles()
        then:
        driver.switchTo().window(tabs2.get(1))
        browser.title.contains("Home - Medical Research Council")

        when:
        driver.close()
        driver.switchTo().window(tabs2.get(0))
        dashboardPage = browser.page DashboardPage
        dashboardPage.sponsorLink(2)
        tabs2 = driver.getWindowHandles()
        then:
        driver.switchTo().window(tabs2.get(1))
        browser.title.contains("NIHR | National Institute for Health Research")

        when:
        driver.close()
        driver.switchTo().window(tabs2.get(0))
        dashboardPage = browser.page DashboardPage
        dashboardPage.sponsorLink(3)
        tabs2 = driver.getWindowHandles()
        then:
        driver.switchTo().window(tabs2.get(1))
        browser.title.contains("Metadata Consulting Ltd")

        when:
        driver.close()
        driver.switchTo().window(tabs2.get(0))
        dashboardPage = to DashboardPage
        dashboardPage.nav.userMenu()
        dashboardPage.nav.logout()
        then:
        at HomePage

    }

}