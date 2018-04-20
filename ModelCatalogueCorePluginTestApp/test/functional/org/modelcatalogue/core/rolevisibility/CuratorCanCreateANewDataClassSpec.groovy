package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataClassPage
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataClassesPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import spock.lang.Narrative
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
class CuratorCanCreateANewDataClassSpec extends GebSpec {

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
        // TODO fill catalogue ID and description
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    // TODO Click on Data Model menu button from the top Menu Bar |  A drop down list is displayed
    // TODO. Navigate to drop down list and select New Data Class
    def "Create a data class"() {
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
        then:
        dataClassesPage.count() == 2
    }
}
