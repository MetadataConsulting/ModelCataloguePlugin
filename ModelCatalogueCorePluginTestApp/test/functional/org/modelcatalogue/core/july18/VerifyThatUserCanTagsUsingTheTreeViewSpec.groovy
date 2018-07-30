package org.modelcatalogue.core.july18

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.*

@Issue('https://metadata.atlassian.net/browse/MET-1434')
@Title('​Verify that user can Tags using the tree view')
@Stepwise
class VerifyThatUserCanTagsUsingTheTreeViewSpec extends GebSpec {

    @Shared
    String datamodelName = UUID.randomUUID().toString()
    @Shared
    String datamodelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String tagNme = "Tag Name"
    @Shared
    String tagDescription = "Description"
    @Shared
    String dataElementName = "dataElementName"

    def "login as supervisor"() {
        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')
        then:
        at DashboardPage
    }

    def "create data model and data element"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = datamodelName
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.description = datamodelDescription
        createDataModelPage.submit()
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select("Data Elements")
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.createDataElement()
        then:
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.name = dataElementName
        createDataElementPage.finish()

        then:
        at DataElementsPage
    }

    def "Create Tags"() {
        when: 'create tag'
        DataModelPage dataModelPage = browser.page(DataModelPage)
        sleep(2000)
        dataModelPage.treeView.select('Tags')

        then:
        at TagsPage

        when: 'navigate to tag creation page'
        TagsPage tagsPage = browser.page(TagsPage)
        tagsPage.createTag()

        then:
        at CreateTagPage

        when: 'fill tag form'
        CreateTagPage createTagPage = browser.page(CreateTagPage)
        createTagPage.name = tagNme
        createTagPage.description = tagDescription
        createTagPage.save()
        then:
        at TagsPage
    }

    def "Select data element and create relationship"() {
        when:
        TagsPage tagsPage = browser.page TagsPage
        tagsPage.treeView.select("Data Elements")
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page(DataElementsPage)
        dataElementsPage.selectdataElements(dataElementName)
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page(DataElementPage)
        dataElementPage.createTagRelationShip()
        then:
        at CreateRelationshipPage

        when:
        CreateRelationshipPage createRelationshipPage = browser.page(CreateRelationshipPage)
        createRelationshipPage.destinationalue(tagNme)
        createRelationshipPage.createRelationship()
        then:
        at DataElementPage
    }

    def "Verify tag added to data element"() {
        when:
        DataElementPage dataElementsPage = browser.page(DataElementPage)
        then:
        dataElementsPage.displayTagName(tagNme)
    }
}