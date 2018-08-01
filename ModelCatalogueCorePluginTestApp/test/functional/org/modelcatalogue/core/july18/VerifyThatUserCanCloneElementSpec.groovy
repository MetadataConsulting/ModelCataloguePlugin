package org.modelcatalogue.core.july18

import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1439')
@Title('Verify that user can clone an Element')
@Stepwise
class VerifyThatUserCanCloneElementSpec extends AbstractModelCatalogueGebSpec {

    @Shared
    String dataTypeName = UUID.randomUUID().toString()
    @Shared
    String importDataModelName = UUID.randomUUID().toString()+"imp"
    @Shared
    String sampleDataModel = UUID.randomUUID().toString()
    @Shared
    String parentDataClass = UUID.randomUUID().toString()
    @Shared
    String childDataClass = UUID.randomUUID().toString()
    @Shared
    String consent = UUID.randomUUID().toString()
    @Shared
    String testDataClass = UUID.randomUUID().toString()+"test"

    def "Login as supervisor"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }


    def "create sample model and data class"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = sampleDataModel
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        sleep(2_000)
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = testDataClass
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "Create data model having child and parent data class"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = importDataModelName
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Classes")
        sleep(2_000)
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = parentDataClass
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage

        when:
        dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = childDataClass
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage

        when:
        dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()
        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = consent
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage

        when:
        dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassLink(consent)
        then:
        at DataClassPage

        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.selectParents()
        dataClassPage.addParent()
        then:
        at ParentClassModalPage

        when:
        ParentClassModalPage parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.searchMore()
        then:
        at SearchClassPage

        when:
        SearchClassPage searchClassPage = browser.page SearchClassPage
        searchClassPage.search(parentDataClass)
        then:
        at ParentClassModalPage

        when:
        parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.createRelationship()
        then:
        at ParentClassModalPage

        when:
        parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.selectChildren()
        sleep(2_000)
        parentClassModalPage.searchMore()
        then:
        at SearchClassPage

        when:
        searchClassPage = browser.page SearchClassPage
        searchClassPage.search(childDataClass)
        then:
        at ParentClassModalPage

        when:
        parentClassModalPage = browser.page ParentClassModalPage
        parentClassModalPage.createRelationship()
        then:
        at ParentClassModalPage
    }

    def "select data class to clone"() {

        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.search(sampleDataModel)
        dashboardPage.select(sampleDataModel)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassLink(testDataClass)
        then:
        at DataClassPage

        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        sleep(2_000)
        dataClassPage.dataClassDropdown()
        dataClassPage.cloneIntoAnother()
        dataClassPage.seachMore()
        dataClassPage.seachMoreList(importDataModelName)
        sleep(2_000)
        dataClassPage.finishButton()
        then:
        at DataClassPage

        when:
        dataClassPage = browser.page DataClassPage
        dataClassPage.treeView.dataModel()
        dataClassPage.treeView.dataClasses()
        sleep(2_000)
        then:
        at DataClassesPage

        when:
        dataClassesPage=browser.page DataClassesPage
        dataClassesPage.selectDataClassLink(consent)
        then:
        sleep(1_000)
        dataClassesPage.titleText().contains(consent)


    }
}