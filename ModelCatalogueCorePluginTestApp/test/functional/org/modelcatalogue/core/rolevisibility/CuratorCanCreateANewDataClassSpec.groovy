package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.*
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1446')
@Title('Verify that curator can create a new Data class')
@Narrative('''
- 1. Login to Model Catalogue as curator/curator | Login Successfully
- 2. Select the 'Create New Data Model' button ( black plus sign) from the top right menu to create a new data model. | Redirected to 'create new data model' page
- 3. Populate data model name, catalogue ID and description. Click save | Data model is saved . User directed to data model page
- 4. Click on Data Model menu button from the top Menu Bar |  A drop down list is displayed
- 5. Navigate to drop down list and select New Data Class  | A new Data Class Wizard window open
- 6. Type 
* the Name 
* Catalogue ID
* Description  | Data are entered correctly
- 7. Click on Metadata button | Metadata window open
- 8. Enter Key and Value | Key and Value are entered successfuly
- 9. Click on Parents Tag | Parents Tag display
- 10. Verify that you Can search for Parent Data Class | Search successfuly
- 11. Click on Form Section and verify that user can type in *Label and Title *Subtitle and instructions * page Number | Data Type Successfully
- 12. Verify that checkbox on Form Section | Checkbox working as expected
- 13. Click on Form(Grid) | Form display
- 14. Verify when tick grid checkbox . Header, Initial number of rows and Max Number of row are enabled | as expected  
- 15. Verify when uncheck grid checkbox . Header, Initial number of rows and Max Number of row are disabled | As expected
- 16. Click on Ocurrence | Occurrence display
- 17. Verify that Min Occurs does not accepted String 
- 18. Click on Appearance | Appearance display
- 19. Enter name | name is entered
- 20. Click on Raw | Raw is displayed
- 21. Verify that all data input into the data class is Present in list. (for instance Key and value)
- 22. Verify that you can add new row (+) or delete row with (-)
- 23. Navigate to Children Tab on the Menu and click | Children tab open
- 24. repeat Steps 10-21 for Children data class | result as expected
- 25. Navigate to element Tab and Click | Element Tab is displayed
- 26. Repeat steps 10-21 for Data Elements | Results as expected
- 27. Click on green button | Message: data class Created is displayed. create another and close options present
- 28. click on Create Another option | Data Class create page display
- 29. Repeat steps 5-27
- 30. click on close. Verify that user is returned  to the initial data model page
- 31. Verify that new Data class has been created     
''')
@Stepwise
@Ignore
class CuratorCanCreateANewDataClassSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataClassOneName = "TESTING_CLASS_ONE"
    @Shared
    String dataClassTwoName = "TESTING_CLASS_TWO"
    @Shared
    String description = 'THIS IS MY DATA CLASS'
    @Shared
    String dataModelDescription = 'TESTING MODEL DESCRIPTION'

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
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueId = "${UUID.randomUUID()}"
        createDataModelPage.submit()

        then:
        at DataModelPage
    }


    def "Create a data class"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()

        then:
        at DataModelPage

        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.dropdownMenu.createDataClass()

        then:
        at CreateDataClassPage
    }

    def "Create a Data Class and select the created data class"() {
        when: ' fill data class step'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassOneName
        createDataClassPage.description = description
        createDataClassPage.topNavigator.metadata()

        then:
        at CreateDataClassMetadataPage
    }

    def "verification for metadata section"() {
        when:
        CreateDataClassMetadataPage createDataClassMetadataPage = browser.page CreateDataClassMetadataPage
        createDataClassMetadataPage.fillMetadata one: '1'
        createDataClassMetadataPage.topNavigator.parentz()
        then:
        at CreateDataClassParentsPage
    }

    def "verification for parents section"() {

        when: 'verification of form (section) for parents'
        CreateDataClassParentsPage createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.formSection()
        createDataClassParentsPage.label = "label"
        createDataClassParentsPage.section = "title"
        createDataClassParentsPage.sectionSubtitle = "subtitle"
        createDataClassParentsPage.formPageNumber = 12
        createDataClassParentsPage.checkExclude()
        createDataClassParentsPage.checkExclude()
        createDataClassParentsPage.checkExcludeDataElement()
        createDataClassParentsPage.checkMergeToSingleSection()

        then:
        at CreateDataClassParentsPage

        when: 'verification of form (grid) for parents'
        createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.formGrid()
        createDataClassParentsPage.selectGrid()

        then:
        createDataClassParentsPage.headerInputIsEnabled()
        createDataClassParentsPage.initialNumberOfRowsIsEnabled()
        createDataClassParentsPage.maxNumberOfRowsIsEnabled()

        when: "min occurance does not accept string"
        createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.ocurrence()
        createDataClassParentsPage.minOccurs = '1'
        createDataClassParentsPage.maxOccurs = '10'

        then:
        at CreateDataClassParentsPage

        when:
        createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.appearance()
        createDataClassParentsPage.appearanceName = "appearance name"
        createDataClassParentsPage.raw()

        then:
        at CreateDataClassParentsPage

    }

    def "verification for children section"() {
        when:
        CreateDataClassParentsPage createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.topNavigator.childrens()

        then:
        at CreateDataClassChildrenPage

        when: 'verification of form (section) for children'
        CreateDataClassChildrenPage createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.formSection()
        createDataClassChildrenPage.label = "label"
        createDataClassChildrenPage.section = "title"
        createDataClassChildrenPage.sectionSubtitle = "subtitle"
        createDataClassChildrenPage.formPageNumber = 12
        createDataClassChildrenPage.checkExclude()
        createDataClassChildrenPage.checkExclude()
        createDataClassChildrenPage.checkExcludeDataElement()
        createDataClassChildrenPage.checkMergeToSingleSection()

        then:
        at CreateDataClassChildrenPage

        when: 'verification of form (grid) for children'
        createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.formGrid()
        createDataClassChildrenPage.selectGrid()

        then:
        createDataClassChildrenPage.headerInputIsEnabled()
        createDataClassChildrenPage.initialNumberOfRowsIsEnabled()
        createDataClassChildrenPage.maxNumberOfRowsIsEnabled()
        true

        when: "min occurance does not accept string"
        createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.ocurrence()
        createDataClassChildrenPage.minOccurs = 1
        createDataClassChildrenPage.maxOccurs = '10'

        then:
        at CreateDataClassChildrenPage

        when:
        createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.appearance()
        createDataClassChildrenPage.appearanceName = "appearance name"
        createDataClassChildrenPage.raw()

        then:
        at CreateDataClassChildrenPage
    }

    def "verification for elements section"() {
        when:
        CreateDataClassChildrenPage createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.topNavigator.elements()
        then:
        at CreateDataClassElementsPage

        when: 'verification of form (Item) for elements'
        CreateDataClassElementsPage createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.formItem()

        then:
        at CreateDataClassElementsPage

        when: "min occurance does not accept string"
        createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.ocurrence()
        createDataClassElementsPage.minOccurs = '1'
        createDataClassElementsPage.maxOccurs = '10'

        then:
        at CreateDataClassElementsPage

        when:
        createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.appearance()
        createDataClassElementsPage.appearanceName = "appearance name"
        createDataClassElementsPage.raw()

        then:
        at CreateDataClassElementsPage

    }

    def "save and create another data class"() {
        when:
        CreateDataClassElementsPage createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.topNavigator.finish()
        then:
        at CreateDataClassFinishedPage

        when:
        CreateDataClassFinishedPage createDataClassFinishedPage = browser.page CreateDataClassFinishedPage
        createDataClassFinishedPage.createAnother()
        then:
        at CreateDataClassPage
    }


    def "re - Create a Data Class and select the created data class"() {
        when: ' fill data class step'
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.name = dataClassTwoName
        createDataClassPage.description = description
        createDataClassPage.topNavigator.metadata()

        then:
        at CreateDataClassMetadataPage
    }

    def "re - verification for metadata section"() {
        when:
        CreateDataClassMetadataPage createDataClassMetadataPage = browser.page CreateDataClassMetadataPage
        createDataClassMetadataPage.fillMetadata one: '1'
        createDataClassMetadataPage.topNavigator.parentz()
        then:
        at CreateDataClassParentsPage
    }

    def "re - verification for parents section"() {

        when: 'verification of form (section) for parents'
        CreateDataClassParentsPage createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.formSection()
        createDataClassParentsPage.label = "label"
        createDataClassParentsPage.section = "title"
        createDataClassParentsPage.sectionSubtitle = "subtitle"
        createDataClassParentsPage.formPageNumber = 12
        createDataClassParentsPage.checkExclude()
        createDataClassParentsPage.checkExclude()
        createDataClassParentsPage.checkExcludeDataElement()
        createDataClassParentsPage.checkMergeToSingleSection()

        then:
        at CreateDataClassParentsPage

        when: 'verification of form (grid) for parents'
        createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.formGrid()
        createDataClassParentsPage.selectGrid()

        then:
        createDataClassParentsPage.headerInputIsEnabled()
        createDataClassParentsPage.initialNumberOfRowsIsEnabled()
        createDataClassParentsPage.maxNumberOfRowsIsEnabled()

        when: "min occurance does not accept string"
        createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.ocurrence()
        createDataClassParentsPage.minOccurs = '1'
        createDataClassParentsPage.maxOccurs = '10'

        then:
        at CreateDataClassParentsPage

        when:
        createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.appearance()
        createDataClassParentsPage.appearanceName = "appearance name"
        createDataClassParentsPage.raw()

        then:
        at CreateDataClassParentsPage

    }

    def "re - verification for children section"() {
        when:
        CreateDataClassParentsPage createDataClassParentsPage = browser.page CreateDataClassParentsPage
        createDataClassParentsPage.topNavigator.childrens()

        then:
        at CreateDataClassChildrenPage

        when: 'verification of form (section) for children'
        CreateDataClassChildrenPage createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.formSection()
        createDataClassChildrenPage.label = "label"
        createDataClassChildrenPage.section = "title"
        createDataClassChildrenPage.sectionSubtitle = "subtitle"
        createDataClassChildrenPage.formPageNumber = 12
        createDataClassChildrenPage.checkExclude()
        createDataClassChildrenPage.checkExclude()
        createDataClassChildrenPage.checkExcludeDataElement()
        createDataClassChildrenPage.checkMergeToSingleSection()

        then:
        at CreateDataClassChildrenPage

        when: 'verification of form (grid) for children'
        createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.formGrid()
        createDataClassChildrenPage.selectGrid()

        then:
        createDataClassChildrenPage.headerInputIsEnabled()
        createDataClassChildrenPage.initialNumberOfRowsIsEnabled()
        createDataClassChildrenPage.maxNumberOfRowsIsEnabled()
        true

        when: "min occurance does not accept string"
        createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.ocurrence()
        createDataClassChildrenPage.minOccurs = 1
        createDataClassChildrenPage.maxOccurs = '10'

        then:
        at CreateDataClassChildrenPage

        when:
        createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.appearance()
        createDataClassChildrenPage.appearanceName = "appearance name"
        createDataClassChildrenPage.raw()

        then:
        at CreateDataClassChildrenPage
    }

    def "re - verification for elements section"() {
        when:
        CreateDataClassChildrenPage createDataClassChildrenPage = browser.page CreateDataClassChildrenPage
        createDataClassChildrenPage.topNavigator.elements()
        then:
        at CreateDataClassElementsPage

        when: 'verification of form (Item) for elements'
        CreateDataClassElementsPage createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.formItem()

        then:
        at CreateDataClassElementsPage

        when: "min occurance does not accept string"
        createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.ocurrence()
        createDataClassElementsPage.minOccurs = '1'
        createDataClassElementsPage.maxOccurs = '10'

        then:
        at CreateDataClassElementsPage

        when:
        createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.appearance()
        createDataClassElementsPage.appearanceName = "appearance name"
        createDataClassElementsPage.raw()

        then:
        at CreateDataClassElementsPage

    }


    def "save and exit"() {
        when:
        CreateDataClassElementsPage createDataClassElementsPage = browser.page CreateDataClassElementsPage
        createDataClassElementsPage.topNavigator.finish()
        then:
        waitFor { at CreateDataClassFinishedPage }

        when:
        CreateDataClassFinishedPage createDataClassFinishedPage = browser.page CreateDataClassFinishedPage
        createDataClassFinishedPage.exitWizard()
        then:
        waitFor { at DraftDataModelListPage }

    }

    def "verify new data class has been created"() {
        when:
        DraftDataModelListPage draftDataModelListPage = browser.page DraftDataModelListPage
        draftDataModelListPage.treeView.select("Data Classes")
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        then:
        dataClassesPage.count() == 2
    }
}
