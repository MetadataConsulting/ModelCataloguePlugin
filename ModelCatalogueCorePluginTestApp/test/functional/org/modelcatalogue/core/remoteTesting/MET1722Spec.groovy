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
class MET1722Spec extends GebSpec {

    @Shared
    String dataModelName = "NEW_TESTING_MODEL"
    @Shared
    String dataTypeName = "TESTING_DATATYPE"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "create data model"() {
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

   def "create data type"() {
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
   }

   def "fill data type values"() {
       when:
       CreateDataTypePage createDataTypePage = browser.page(CreateDataTypePage)
       createDataTypePage.name = dataTypeName
       createDataTypePage.enumerated()
       createDataTypePage.fillMetadata(one: 1)
       createDataTypePage.buttons.save()

       then:
       at DataTypesPage
   }

    def "select newly created data type"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        Thread.sleep(2000)
        dataTypesPage.selectDataType(dataTypeName)

        then:
        at DataTypePage
    }

    def "select validate value"() {
        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        Thread.sleep(2000)
        dataTypePage.enumeratedType()
        dataTypePage.validateValue()
        then:
        at DataTypePage
    }

    def "validate values"() {
        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        dataTypePage.validateKeyField = "one"
        Thread.sleep(2000)
        dataTypePage = browser.page DataTypePage
        then:
        dataTypePage.outputIsValid()
    }
}