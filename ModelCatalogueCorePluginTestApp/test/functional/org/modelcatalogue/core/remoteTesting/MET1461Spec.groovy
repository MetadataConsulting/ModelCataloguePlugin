package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.junit.Ignore
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title


@Stepwise
class MET1461Spec extends GebSpec {
    @Shared
    String dataModelName = "NEW_TESTING_MODEL"
    @Shared
    String dataTypeName = "NEW_DATATYPE"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "create new data model"() {
         when:
         DashboardPage dashboardPage = to DashboardPage
         dashboardPage.nav.createDataModel()

         then:
         at CreateDataModelPage

         when:
         CreateDataModelPage createDataModelPage = to CreateDataModelPage
         createDataModelPage.name = dataModelName
         createDataModelPage.submit()

         then:
         at DataModelPage

    }

    def "create new data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = dataTypeName
        createDataTypePage.enumerated()
        createDataTypePage.fillMetadata(one: 1, two: 2)
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "export"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.select(dataModelName)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = at DataModelPage
        dataModelPage.export()
        dataModelPage.exportCatalogXml()
        then:
        at DataModelPage
    }

    def "update the xml file"() {}

    def "import catalog xml"() {

    }
}