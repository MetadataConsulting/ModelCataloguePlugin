package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
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
@Stepwise
class MET1469Spec extends GebSpec {
    @Shared
    String nameLabel = "NEW_TESTING_MODEL"
    @Shared
    String wizardSummary = 'td.col-md-4'
    @Shared
    String saveElement = "a#role_modal_modal-save-elementBtn"
    @Shared
    String search = "input#dataType"
    @Shared
    String myName = " testing data element "
    @Shared
    String myCatalogue = UUID.randomUUID().toString()
    @Shared
    String myDescription = "This a test element"
    @Shared
    String tagName = "myTag"

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "Select any Data Model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Test 1')

        then:
        at DataModelPage
    }

    def "Create a data class"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Classes')

        then:
        at DataClassesPage
    }

    def "Navigate to Create data classes page"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.createDataClass()

        then:
        at CreateDataClassPage
    }

    def "Create a Data Class and select the created data class"() {
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

        then:
        at DataClassesPage
    }

    def "Create Data Element"() {
        when:
        DataClassesPage dataClassesPage = browser.page(DataClassesPage)
        Thread.sleep(1000)
        dataClassesPage.treeView.select('Data Elements')

        then:
        at DataElementsPage
    }

    def "navigate to data element creation page"() {
        when:
        DataElementsPage dataElementsPage = browser.page(DataElementsPage)
        dataElementsPage.createDataElement()

        then:
        at CreateDataElementPage
    }

    def "fill the create data element form"() {
        when:
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
        Thread.sleep(2000)
        at CreateDataElementPage

        when:
        CreateDataElementPage createDataElementPage1 = browser.page(CreateDataElementPage)
        createDataElementPage1.finish()

        then: 'verify that data is created'
        at DataElementsPage
    }

    def "create tag"() {
        when:
        DataElementsPage dataElementsPage = browser.page(DataElementsPage)
        dataElementsPage.treeView.select('Tags')

        then:
        at TagsPage
    }

    def "navigate to tag creation page"() {
        when:
        TagsPage tagsPage = browser.page(TagsPage)
        tagsPage.createTag()

        then:
        at CreateTagPage
    }

    def "fill tag form"() {
        when:
        CreateTagPage createTagPage = browser.page(CreateTagPage)
        createTagPage.name = tagName
        createTagPage.description = myDescription
        createTagPage.save()

        then:
        at TagsPage

        when:
        TagsPage tagsPage = browser.page(TagsPage)

        then:
        tagsPage.count() == 1
    }
}