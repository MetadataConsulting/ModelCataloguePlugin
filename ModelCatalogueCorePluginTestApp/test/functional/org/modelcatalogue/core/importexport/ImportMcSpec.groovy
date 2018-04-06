package org.modelcatalogue.core.importexport

import geb.spock.GebSpec
import groovy.transform.NotYetImplemented
import org.modelcatalogue.core.geb.AssetPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.ImportModelCatalogueDslPage

class ImportMcSpec extends GebSpec {

    @NotYetImplemented
    def "import mc file"() {
        when:
        URL url = ImportMcSpec.getResource('Java.mc')

        then:
        File f = new File(url.toURI())

        then:
        f.exists()

        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.importMenu()
        dashboardPage.nav.importDsl()

        then:
        at ImportModelCatalogueDslPage

        when:
        ImportModelCatalogueDslPage importModelCatalogueDslPage = browser.page ImportModelCatalogueDslPage
        importModelCatalogueDslPage.upload(f.absolutePath)

        then:
        at AssetPage

        when:
        AssetPage assetPage = browser.page AssetPage
        assetPage.treeView.dataTypes()

        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage


        then:
        dataTypesPage.count() == numberOfLinesContains('dataType name:')
    }

    int numberOfLinesContains(File f, String value) {
        int numberOfLinesContainsDataType = 0
        f.eachLine { String line ->
            if ( line.contains() ) {
                numberOfLinesContainsDataType++
            }
        }
        numberOfLinesContainsDataType
    }
}
