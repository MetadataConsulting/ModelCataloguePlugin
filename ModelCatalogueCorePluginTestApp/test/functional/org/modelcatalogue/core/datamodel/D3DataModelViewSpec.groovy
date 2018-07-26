package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.AdvancedDataModelViewPage
import org.modelcatalogue.core.geb.BasicDataModelViewPage
import org.modelcatalogue.core.geb.CreateDataClassPage
import org.modelcatalogue.core.geb.CreateDataElementPage
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataClassesPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Shared
import spock.lang.Stepwise



@Stepwise
class D3DataModelViewSpec extends GebSpec {


    @Shared
    String dataModelName = "TestDataModel_${UUID.randomUUID().toString()}"
    @Shared
    String dataModelCatalogueId = UUID.randomUUID().toString()
    @Shared
    String dataModelDescription = "description"
    @Shared
    String dataClassName = "DC1_${UUID.randomUUID().toString()}" // "TESTING_CLASS"
    @Shared
    String dataClassDescription = "TESTING_CLASS_DESCRIPTION"
    @Shared
    String dataElementName = "DE1_${UUID.randomUUID().toString()}" // "TESTING_ELEMENT_ONE"
    @Shared
    String dataElementDescription = "TESTING_ELEMENT_DESCRIPTION"
    @Shared
    String dataElementTwoName ="DE2_${UUID.randomUUID().toString()}" // "TESTING_ELEMENT_TWO"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')
        then:
        at DashboardPage
    }

    def "Create data model and filling data model form"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage
        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.modelCatalogueId = dataModelCatalogueId
        createDataModelPage.description = dataModelDescription
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    // The following code copied from CheckCreateElementFromClassWizardSpec


    def "go to data classes"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
        then:
        at DataClassesPage
    }

    def "create new data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataClassPage.description = dataClassDescription
        createDataClassPage.elements()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.dataElement = dataElementName
        createDataClassPage.createNewElement()
        then:
        at CreateDataElementPage
    }

    def "create new data element"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.description = dataElementDescription
        createDataElementPage.finish()
        then:
        at CreateDataClassPage
    }

    def "create another data element and save data class"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.dataElement = dataElementTwoName
        createDataClassPage.createNewElementFromPlusButton()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }


    def "Navigate to Basic Data Model View page from Advanced Data Model View Page"() {
        when:
        AdvancedDataModelViewPage advancedDataModelViewPage = browser.page AdvancedDataModelViewPage
        advancedDataModelViewPage.goToBasicDataModelView()

        then:
        at BasicDataModelViewPage
    }

    def "Click around"() {
        when:
        BasicDataModelViewPage basicDataModelViewPage = browser.page BasicDataModelViewPage
        basicDataModelViewPage.clickElementWithName(dataModelName)

        then:
        noExceptionThrown()
    }

    // TODO: click dataClass node to open that as well
    // TODO: test content of content panels

    def "Go back to Advanced from Basic"() {
        when:
        BasicDataModelViewPage basicDataModelViewPage = browser.page BasicDataModelViewPage
        basicDataModelViewPage.goToAdvancedDataModelView()

        then:
        at DataModelPage
    }
}
