package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1474')
@Title('Verify that user can add model in to favourite or remove it')
@Stepwise
class VerifyUserCanAddModelIntoFavouriteOrRemoveSpec extends GebSpec {

    @Shared
    String dataModelOneName = UUID.randomUUID().toString()
    @Shared
    String dataClassId = UUID.randomUUID().toString()
    @Shared
    String dataClassName = "New data class"
    @Shared
    String dataClassName2 = "New data class 2"

    def "login as curator"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')
        then:
        at DashboardPage
    }

    def "create first data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelOneName
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "Create data class"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Classes')
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName
        createDataClassPage.modelCatalogueId = dataClassId
        createDataClassPage.description = 'THIS IS MY DATA CLASS'
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "Create second data class with same id"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Classes')
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then:
        at CreateDataClassPage

        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassName2
        createDataClassPage.modelCatalogueId = dataClassId
        createDataClassPage.description = 'THIS IS MY DATA CLASS'
        createDataClassPage.finish()
        then:
        createDataClassPage.isAlertDisplayed()

        when:
        createDataClassPage = browser.page CreateDataClassPage
        then:
        createDataClassPage.exit()
    }

    def"check second data class not created"(){
        when:
        DataClassesPage dataClassesPage=browser.page DataClassesPage
        then:
        !dataClassesPage.selectDataClassDisplayed(dataClassName2)
    }
}