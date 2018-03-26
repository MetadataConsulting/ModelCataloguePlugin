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

    private static final myModel = "#my-models"
    private static final String modelHeaderName = 'h3.ce-name'
    private static final String metadataStep = "button#step-metadata"
    private static final String label = "textarea#section-label"
    private static final String section_title = "textarea#section-title"
    private static final String instruction = "textarea#section-instructions"
    private static final String page_number = 'input#form-page-number'
    private static final String occurrence = 'ul.nav-pills>li:nth-child(3)>a'
    private static final String finishButton = "button#step-finish"
    private static final String appearance = 'ul.nav-pills>li:nth-child(4)>a'
    private static final String name = "input#local-name"
    private static final String elementStep = "button#step-elements"
    private static final String dataElement = "input#data-element"
    private static final String plusButton = "span.input-group-btn>button"
    private static final String raw = "ul.nav-pills>li:nth-child(4)>a"
    private static final String wizardSummary = 'td.col-md-4'
    private static final String parentStep = "button#step-parents"
    private static final String formSection = 'ul.nav-pills>li:nth-child(1)>a'
    private static final long TIME_TO_REFRESH_SEARCH_RESULTS = 4000L
    private static final String exitButton = 'button#exit-wizard'
    private static final String saveElement = "a#role_modal_modal-save-elementBtn"
    private static final String tagElement = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(2)"
    private static final String search = "input#dataType"

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
        createDataClassPage.addMetadata()
        createDataClassPage.fillMetadata foo: 'five'
        createDataClassPage.finish()
        createDataClassPage.exit()

        then:
        at DataClassesPage
    }

    def "Create Data Element"() {
        when:
        DataClassesPage dataClassesPage = browser.page(DataClassesPage)
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

        and: 'select a data type'

        fill search with 'boolean'
        Thread.sleep(2000l)

        and: 'click on the save button'
        println("////////////////////111111111111/////////////////")
        click saveElement

        then: 'verify that data is created'
        check wizardSummary contains 'testing data element'
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