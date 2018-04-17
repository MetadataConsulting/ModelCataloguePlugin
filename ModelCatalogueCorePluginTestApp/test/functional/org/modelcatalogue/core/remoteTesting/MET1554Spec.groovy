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
class MET1554Spec extends GebSpec {
    @Shared
    String dataModelName = "NEW_TESTING_MODEL"
    @Shared
    String dataClassName = "NEW_TESTING_CLASS"
    @Shared
    String dataClassDescription = "NEW_TESTING_CLASS"


    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    /*def "create new data model"() {
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
    }*/

    def "import data model"() {
        when: "delete this one"
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.select(dataModelName)
        then:
        at DataModelPage

        /*when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Imported Data Models")
        then:
        at ImportedDataModelsPage

        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.importDataModel()
        then:
        at ImportedDataModelsPage*/
    }

   /* def "search a data model"() {
        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.searchMore()
        importedDataModelsPage.selectDataModel(1)
//        importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.createRelationship()

        then:
        at ImportedDataModelsPage
    }*/

    def "create new data class"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.select(dataModelName)
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")

        then:
        at DataClassesPage

        /*when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.description = dataClassDescription
        createDataClassPage.finish()
        createDataClassPage.exit()

        then:
        at DataClassesPage*/

    }

    def "select newly created data class"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassByName(dataClassName)
        then:
        at DataClassPage
    }

    def "select parents"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.selectParents()
        dataClassPage.addParent()
        then:
        at ParentClassModalPage

        when:
        ParentClassModalPage parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.searchMore()
        parentClassModalPage.importDataClass()
        parentClassModalPage.createRelationship()
        then:
        at DataClassPage
    }

}