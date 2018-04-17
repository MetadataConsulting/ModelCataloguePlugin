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
class MET1446Spec extends GebSpec {
    @Shared
    String dataModelName = "NEW_TESTING_MODELS"
    @Shared
    String dataClassOneName = "TESTING_CLASS_ONE"
    @Shared
    String dataClassTwoName = "TESTING_CLASS_TWO"
    @Shared
    String description = 'THIS IS MY DATA CLASS'

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
        createDataClassPage.name = dataClassOneName
        createDataClassPage.description = description
        createDataClassPage.metadata()
        createDataClassPage.fillMetadata one: '1'
        createDataClassPage.parents()

        then:
        at CreateDataClassPage
    }

    def "verification for parents section"() {

        when: 'verification of form (section) for parents'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formSection()
        createDataClassPage.label = "label"
        createDataClassPage.section = "title"
        createDataClassPage.sectionSubtitle = "subtitle"
        createDataClassPage.formPageNumber = 12
        createDataClassPage.checkMergeToSingleSection()
        createDataClassPage.checkExclude()
        createDataClassPage.checkExcludeDataElement()

        then:
        at CreateDataClassPage

        when: 'verification of form (grid) for parents'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formGrid()
        createDataClassPage.selectGrid()

        then:
        createDataClassPage.isEnabled(createDataClassPage.headerInput)
        createDataClassPage.isEnabled(createDataClassPage.initialNumberOfRowsInput)
        createDataClassPage.isEnabled(createDataClassPage.maxNoOfRowsInput)

        when: "min occurance does not accept string"
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.ocurrence()
        createDataClassPage.minOccurs = 1
        createDataClassPage.maxOccurs = '10'

        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.appearance()
        createDataClassPage.appearanceName = "appearance name"
        createDataClassPage.raw()

        then:
        at CreateDataClassPage

    }

    def "verification for children section"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.children()

        then:
        at CreateDataClassPage

        when: 'verification of form (section) for children'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formSection()
        createDataClassPage.label = "label"
        createDataClassPage.section = "title"
        createDataClassPage.sectionSubtitle = "subtitle"
        createDataClassPage.formPageNumber = 12
        createDataClassPage.checkExclude()
        createDataClassPage.checkExclude()
        createDataClassPage.checkExcludeDataElement()
        createDataClassPage.checkMergeToSingleSection()

        then:
        at CreateDataClassPage

        when: 'verification of form (grid) for children'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formGrid()
        createDataClassPage.selectGrid()

        then:
        createDataClassPage.isEnabled(createDataClassPage.headerInput)
        createDataClassPage.isEnabled(createDataClassPage.initialNumberOfRowsInput)
        createDataClassPage.isEnabled(createDataClassPage.maxNoOfRowsInput)
        true

        when: "min occurance does not accept string"
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.ocurrence()
        createDataClassPage.minOccurs = 1
        createDataClassPage.maxOccurs = '10'

        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.appearance()
        createDataClassPage.appearanceName = "appearance name"
        createDataClassPage.raw()

        then:
        at CreateDataClassPage
    }

    def "verification for elements section"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.elements()
        then:
        at CreateDataClassPage

        when: 'verification of form (Item) for elements'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formItem()

        then:
        at CreateDataClassPage

        when: "min occurance does not accept string"
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.ocurrence()
        createDataClassPage.minOccurs = '1'
        createDataClassPage.maxOccurs = '10'

        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.appearance()
        createDataClassPage.appearanceName = "appearance name"
        createDataClassPage.raw()

        then:
        at CreateDataClassPage

    }

    def "save and create another data class"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.finish()
        createDataClassPage.createAnother()

        then:
        at CreateDataClassPage
    }


    def "re- Create a Data Class and select the created data class"() {
        when: ' fill data class step'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassTwoName
        createDataClassPage.description = description
        createDataClassPage.metadata()
        createDataClassPage.fillMetadata one: '1'
        createDataClassPage.parents()

        then:
        at CreateDataClassPage
    }

    def "re- verification for parents section"() {

        when: 'verification of form (section) for parents'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formSection()
        createDataClassPage.label = "label"
        createDataClassPage.section = "title"
        createDataClassPage.sectionSubtitle = "subtitle"
        createDataClassPage.formPageNumber = 12
        createDataClassPage.checkMergeToSingleSection()
        createDataClassPage.checkExclude()
        createDataClassPage.checkExcludeDataElement()

        then:
        at CreateDataClassPage

        when: 'verification of form (grid) for parents'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formGrid()
        createDataClassPage.selectGrid()

        then:
        createDataClassPage.isEnabled(createDataClassPage.headerInput)
        createDataClassPage.isEnabled(createDataClassPage.initialNumberOfRowsInput)
        createDataClassPage.isEnabled(createDataClassPage.maxNoOfRowsInput)
        true

        when: "min occurance does not accept string"
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.ocurrence()
        createDataClassPage.minOccurs = '1'
        createDataClassPage.maxOccurs = '10'

        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.appearance()
        createDataClassPage.appearanceName = "appearance name"
        createDataClassPage.raw()

        then:
        at CreateDataClassPage

    }

    def "re- verification for children section"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.children()

        then:
        at CreateDataClassPage

        when: 'verification of form (section) for children'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formSection()
        createDataClassPage.label = "label"
        createDataClassPage.section = "title"
        createDataClassPage.sectionSubtitle = "subtitle"
        createDataClassPage.formPageNumber = 12
        createDataClassPage.checkExclude()
        createDataClassPage.checkExclude()
        createDataClassPage.checkExcludeDataElement()
        createDataClassPage.checkMergeToSingleSection()

        then:
        at CreateDataClassPage

        when: 'verification of form (grid) for children'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formGrid()
        createDataClassPage.selectGrid()

        then:
        createDataClassPage.isEnabled(createDataClassPage.headerInput)
        createDataClassPage.isEnabled(createDataClassPage.initialNumberOfRowsInput)
        createDataClassPage.isEnabled(createDataClassPage.maxNoOfRowsInput)
        true

        when: "min occurance does not accept string"
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.ocurrence()
        createDataClassPage.minOccurs = '1'
        createDataClassPage.maxOccurs = '10'

        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.appearance()
        createDataClassPage.appearanceName = "appearance name"
        createDataClassPage.raw()

        then:
        at CreateDataClassPage
    }

    def "re- verification for elements section"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.elements()
        then:
        at CreateDataClassPage

        when: 'verification of form (Item) for elements'
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.formItem()

        then:
        at CreateDataClassPage

        when: "min occurance does not accept string"
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.ocurrence()
        createDataClassPage.minOccurs = '1'
        createDataClassPage.maxOccurs = '10'

        then:
        at CreateDataClassPage

        when:
        createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.appearance()
        createDataClassPage.appearanceName = "appearance name"
        createDataClassPage.raw()

        then:
        at CreateDataClassPage

    }

    def "re- save and create another data class"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.finish()
        createDataClassPage.exitWizard()

        then:
        at DataClassesPage
    }

    def "verify new data class has been created"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClassByName(dataClassTwoName)
        then:
        at DataClassPage
    }
}