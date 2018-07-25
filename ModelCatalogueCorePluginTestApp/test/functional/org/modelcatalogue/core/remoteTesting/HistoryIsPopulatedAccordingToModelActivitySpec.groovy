package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1469')
@Title('Verify that the history is populated according to activity made on a model')
@Narrative('''
- Login as curator
- Select any Data Model
- Create a data class
- Create a data element
- Edit the created data class and save
- Create a new Tag
''')
class HistoryIsPopulatedAccordingToModelActivitySpec extends GebSpec {
    def "Verify that the history is populated according to activity made on a model"() {
        given:
        final String nameLabel = "NEW_TESTING_MODEL"
        final String search = "input#dataType"
        final String myName = " testing data element "
        final String myCatalogue = UUID.randomUUID().toString()
        final String myDescription = "This a test element"
        final String tagName = "myTag"

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'Select any Data Model'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Test 1')
        dashboardPage.select('Test 1')

        then:
        at DataModelPage

        when: "Create a data class"
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Classes')

        then:
        at DataClassesPage

        when: "Navigate to Create data classes page"
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then:
        at CreateDataClassPage

        when: ' fill data class step'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = nameLabel
        createDataClassPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataClassPage.description = 'THIS IS MY DATA CLASS'
        createDataClassPage.metadata()
        createDataClassPage.fillMetadata foo: 'one', bar: 'two', baz: 'three', fubor: 'four'
        createDataClassPage.parents()

        createDataClassPage.formSection()
        createDataClassPage.label = 'TEST_LABEL'
        createDataClassPage.section = 'MY_TITLE'
        createDataClassPage.sectionInstructions = 'this is my instruction'
        createDataClassPage.formPageNumber = '1'

        createDataClassPage.ocurrence()

        createDataClassPage.minOccurs = '1'
        createDataClassPage.maxOccurs = '10'

        createDataClassPage.appearance()
        createDataClassPage.appearanceName = ' this is my name'

        createDataClassPage.elements()
        createDataClassPage.dataElement = 'TEST_ELEMENT'
        createDataClassPage.clickPlus()
        createDataClassPage.raw()
        createDataClassPage.finish()
        createDataClassPage.exit()
        sleep(2_000)

        then:
        at DataClassesPage
    }

    def "verify that data is created"() {
        when: 'Create Data Element'
        DataClassesPage dataClassesPage = browser.page(DataClassesPage)
        sleep(2_000)
        dataClassesPage.treeView.select('Data Elements')

        then:
        at DataElementsPage

        when: 'navigate to data element creation page'
        DataElementsPage dataElementsPage = browser.page(DataElementsPage)
        dataElementsPage.createDataElement()

        then:
        at CreateDataElementPage

        when: 'fill the create data element form'
        CreateDataElementPage createDataElementPage = browser.page(CreateDataElementPage)
        createDataElementPage.name = myName
        createDataElementPage.modelCatalogueId = myCatalogue
        createDataElementPage.description = myDescription
        createDataElementPage.search('boolean')
        createDataElementPage.selectFirstItem()

        then:
        at CloneOrImportPage

        when:
        CloneOrImportPage cloneOrImportPage = browser.page(CloneOrImportPage)
        cloneOrImportPage.allowClone()

        then:
        sleep(2000)
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage1 = browser.page(CreateDataElementPage)
        createDataElementPage1.finish()

        then: 'verify that data is created'
        at DataElementsPage

        when: 'create tag'
        dataElementsPage = browser.page(DataElementsPage)
        sleep(2000)
        dataElementsPage.treeView.select('Tags')

        then:
        at TagsPage

        when: 'navigate to tag creation page'
        TagsPage tagsPage = browser.page(TagsPage)
        tagsPage.createTag()

        then:
        at CreateTagPage

        when: 'fill tag form'
        CreateTagPage createTagPage = browser.page(CreateTagPage)
        createTagPage.name = tagName
        createTagPage.description = myDescription
        createTagPage.save()

        then:
        at TagsPage

        when:
        tagsPage = browser.page(TagsPage)

        then:
        tagsPage.count() >= 1
    }
}