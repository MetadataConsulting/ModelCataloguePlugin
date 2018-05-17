package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Shared
import org.modelcatalogue.core.geb.*
import spock.lang.Stepwise

@Issue('https://metadata.atlassian.net/browse/MET-1625')
@Title('Examine that a Curator is not able to edit finalized models')
@Narrative($/
 - 1. Login to Metadata Exchange as curator | Login successful
 - 2. Select the 'Create Data Model' button (plus sign) from top right menu | Redirected to 'Create Data Model' page
 - 3. Fill form with Name, Catalogue ID and Description. Select a Data Model Policy. Click on the Save button. | New Data Model is created. Redirected to new Data Model
 - 4. Using tree-navigation panel, select Data Classes tag. | Display panel opens up Data Classes main page. Title is 'Active Data Classes'
 - 5. Click on the 'Create New' green plus sign button to create a new Data Class. | Create new 'Data Class Wizard' pop-up dialogue box appears.
 - 6. Fill form with Name, Catalogue ID, Description | Form fields are filled
 - 7. Select 'Elements' tab from tabs at top of Data Class Wizard. | Elements tab opens in Data Class Wizard.
 - 8. In the search bar underneath title 'Data Element' type in a new Data Element name. | Drop down from search bar appears offering option 'Search' and 'Create New'
 - 9. Select 'Create New' option from drop-down | Create new Data Element pop-up appears. Title is 'Create Data Element'
 - 10. In 'Create Data Element' pop-up, enter Name, catalogue ID Description and type a new Data Type name in the Data Type search bar. | Drop-down menu appears from search bar with options 'Search' and 'Create New'
 - 11. Select option to 'Create New' | Create Data Type pop-up appears. Title is 'Create Data Type'.
 - 12. Fill form with Name, Catalogue ID, Description and select type of Data Type. Click 'Save' button. | Data Type is saved and Data Type name populates Data Type form field in Create Data Element pop up
 - 13. In 'Create Data Element' pop-up, Click on 'Save' button. | Data Element is created and Data Element name appears under Data Element in Data Class Wizard.
 - 14. In Data Class Wizard, click on the save button ( green tick) in the upper right corner. | Data Class is created. Option to Create Another Data Class or Close Data Class Wizard appears.
 - 15. Select option to Close Data Class Wizard. | Data Class Wizard is closed. Display panel directs to Draft or Active Data Classes page.
 - 16. Using Tree-navigation panel, select Data Model name tag to go to Data Model main page. | Redirected to Data Model main page in the display panel
 - 17. Click on 'Data Model' menu button in top left menu . | Data Model menu drop-down appears
 - 18. Select option to Finalize Data Model from Data Model menu drop-down. | 'Finalize Data Model' pop-up appears.
 - 19. Fill form field with Semantic version number and revision notes. Click 'Finalize' button to finalize. | 'Finalizing' process dialogue box appears
 - 20. Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Click 'Hide' on the 'Finalizing' process box | Data Model is finalized. Redirected to Finalized data model page.
 - 21. Verify that in the Display panel, to the right of the Data Model Name in the icon menu there is no option Edit button present to edit the Data Model. | Edit Data Model button not enabled/present.
 - 22. Using tree-navigation panel, select Data Classes tag. | Redirected to Data Classes main list page. Title is 'Active Data Classes'
 - 23. Verify that there is no Create New 'plus' button under list of Data Classes within 'Active Data Classes' page. . | Not able to create new data classes
 - 24. Select the plus sign/ show more button to the left hand side of one of the Data Classes to open up the 'more information' panel below the Data Class name. | The 'show more' information panel appears underneath the Data Class name
 - 25. Verify that the edit button to the right of the Data Class title/name in the icon menu is disabled. | Edit button is disabled.
 - 26. Click on the Data Class name/link to open up it's own page in the display panel | Data Class's individual main page opens up in display panel . Title is [name of data class].
 - 27. Verify that in the panel's menu to the right of the Data Class name, the edit button is disabled. | Edit button in 'Show more' panel is disabled.
 - 28. Using tree-navigation panel, select the Data Elements tag to go to the Active Data Elements page in the display panel. | Display panel displays Data Elements list page . Title is 'Active Data Elements'.
 - 29. Repeat steps 23-27 with Data Elements. | Verify that there is no option to edit Data Elements or create new ones.
 - 30. Using tree-navigation panel, select the Data Types tag to go to the Active Data Types page in the display panel. | Display panel displays Data Types list page. Title is 'Active Data Types'.
 - 31. Repeat steps 23-27 with Data Types. | Verify that there is no option to edit Data Types or create new ones.
 - 32. Using tree-navigation panel, select the Measurement Units tag to go to the Measurement Units page in the display panel. | Display panel displays Measurement Units list page. Title is 'Active Measurement Units'.
 - 33. Verify that there is no option to Create new Measurement unit. No green - plus sign button . | No option to create more or edit existing.
 - 34. Using tree-navigation panel, Select the Business Rules tag to go to the Business Rules page in the display panel. | Display panel displays Business Rules list page. Title is 'Active Business Rules'
 - 35. Verify that there is no option to Create New Business Rule. No green - plus sign button. | No option to create more or edit existing.
 - 36. Using tree-navigation panel, Select the Assets tag to go to the Assets page in the display panel. | Display panel displays Assets list page. Title is 'Active Assets'.
 - 37. Verify that there is no option to Import a new asset. No green - plus sign button. | No option to import new asset or edit existing.
 - 38. Using tree-navigation panel, Select the Tags tag to go to the Assets page in the display panel. | Display panel displays Assets list page. Title is 'Active Tags'.
 - 39. Verify that there is no option to Create New Tag. No green - plus sign button. | No option to create new or edit existing.
 - 40. Using tree-navigation panel, Select the 'Deprecated Items' tag to go to the Deprecated Items page in the display panel | Display panel displays Deprecated Items list page. Title is 'Deprecated Catalogue Elements'.
 - 41. Verify that there is no option to add another Deprecated Item. No plus sign button. | No option to add Deprecated item.
 - 42. Using tree-navigation panel, Select the 'Imported Data Models' tag to go to the Imported Data Models page in the display panel | Display panel displays Imported Data Models list page. Title is 'Imported Data Models'
 - 43. Verify that there is no option to Import a new Data Model. No green - plus sign button . | No option to import new Data Model.
 - 44. Using tree-navigation panel, Select the 'Versions' tag to go to the Versions page in the display panel | Display panel displays Versions list page. Title is '[Data Model Name] History'.
 - 45. In one of the versions listed - click on the 'Show More' plus sign to the left of its name - to open up the 'Show more' panel underneath . | 'Show More' panel opens up underneath.
 - 46. Verify that in menu in the show more panel, there is no option to edit. | No option to edit.
/$)
@Stepwise
class CuratorCannotEditFinalizedModelSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString() // "TESTING_MODEL"
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"
    @Shared
    String dataClassName = UUID.randomUUID().toString() // "TESTING_CLASS"
    @Shared
    String dataClassDescription = "TESTING_CLASS_DESCRIPTION"
    @Shared
    String dataElementName = UUID.randomUUID().toString() // "TESTING_ELEMENT"
    @Shared
    String dataElementDescription = "TESTING_ELEMENT_DESCRIPTION"
    @Shared
    String dataTypeName = UUID.randomUUID().toString() // "TESTING_DATATYPE"
    @Shared
    String dataTypeDescription = "TESTING_DATATYPE_DESCRIPTION"

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
        createDataModelPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataModelPage.description = dataModelDescription
        createDataModelPage.submit()
        then:
        at DataModelPage
    }

    def "create new data class"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
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
        createDataClassPage.description = dataClassDescription
        createDataClassPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataClassPage.elements()
        then:
        at CreateDataClassPage
    }

    def "search data element"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.dataElement = dataElementName
        createDataClassPage.createNewElement()
        then:
        at CreateDataElementPage
    }

    def "create new data element"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.description = dataElementDescription
        createDataElementPage.modelCatalogueId = UUID.randomUUID().toString()
        createDataElementPage.search(dataTypeName)
        createDataElementPage.createNewDataType()
        then:
        at CreateDataTypePage
    }

    def "create new data type"() {
        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.description = dataTypeDescription
        createDataTypePage.modelCatalogueId = UUID.randomUUID().toString()
        createDataTypePage.buttons.save()
        then:
        at CreateDataElementPage
    }

    def "save data element"() {
        when:
        CreateDataElementPage createDataElementPage = browser.page CreateDataElementPage
        createDataElementPage.finish()
        then:
        at CreateDataClassPage
    }

    def "save data class"() {
        when:
        CreateDataClassPage createDataClassPage = browser.page CreateDataClassPage
        createDataClassPage.finish()
        createDataClassPage.exit()
        then:
        at DataClassesPage
    }

    def "goto data model main page"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.treeView.dataModel()
        then:
        at DataModelPage
    }

    def "finalize data model"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
        dataModelPage.finalizedDataModel()
        then:
        at FinalizeDataModelPage

        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.version = "0.0.2"
        finalizeDataModelPage.versionNote = 'THIS IS THE VERSION NOTE'
        finalizeDataModelPage.submit()
        then:
        at FinalizedDataModelPage

        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
        then:
        at DataModelPage
    }

    def "verify data model is not editable"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        then:
        !dataModelPage.editButtonVisible()
    }

    def "verify new data class cannot be created"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.dataClasses()
        then:
        at DataClassesPage

        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        then:
        !dataClassesPage.isAddItemIconVisible()
    }

    def "verify data class cannot be edited"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.showMore()
        then:
        dataClassesPage.editDataClassDisabled()
    }

    def "open data class and verify edit button is disabled"() {
        when:
        DataClassesPage dataClassesPage = browser.page DataClassesPage
        dataClassesPage.selectDataClass(dataClassName)
        then:
        at DataClassPage

        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        then:
        dataClassPage.editDataClassDisabled()
    }

    def "goto data elements"() {
        when:
        DataClassPage dataClassPage = browser.page DataClassPage
        dataClassPage.treeView.dataElements()
        then:
        at DataElementsPage

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        then:
        !dataElementsPage.isAddItemIconVisible()
    }

    def "verify data element cannot be edited"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.showMore()
        then:
        dataElementsPage.editDataElementDisabled()
    }

    def "open data element and verify edit button is disabled"() {
        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectDataElement(dataElementName)
        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        then:
        dataElementsPage.editDataElementDisabled()
    }

    def "goto data types"() {
        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.treeView.dataTypes()
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        then:
        !dataTypesPage.isAddItemIconVisible()
    }

    def "verify data type cannot be edited"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.showMore()
        then:
        dataTypesPage.editDataTypeDisabled()
    }

    def "open data type and verify edit button is disabled"() {
        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.selectDataType(dataTypeName)
        then:
        at DataTypePage

        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        then:
        dataTypePage.editDataTypeDisabled()
    }

    def "goto measurement units and verify there is no option to Create new Measurement unit"() {
        when:
        DataTypePage dataTypePage = browser.page DataTypePage
        dataTypePage.treeView.measurementUnits()
        then:
        at MeasurementUnitsPage

        when:
        MeasurementUnitsPage measurementUnitsPage = browser.page MeasurementUnitsPage
        then:
        !measurementUnitsPage.isAddItemIconVisible()
    }

    def "goto business rules and verify there is no option to Create New Business Rule"() {
        when:
        MeasurementUnitsPage measurementUnitsPage = browser.page MeasurementUnitsPage
        measurementUnitsPage.treeView.businessRules()
        then:
        at BusinessRulesPage

        when:
        BusinessRulesPage businessRulesPage = browser.page BusinessRulesPage
        then:
        !businessRulesPage.isAddItemIconVisible()
    }

    def "goto assests and verify there is no option to Import a new asset"() {
        when:
        BusinessRulesPage businessRulesPage = browser.page BusinessRulesPage
        businessRulesPage.treeView.assets()
        then:
        at AssetsPage

        when:
        AssetsPage assetsPage = browser.page AssetsPage
        then:
        !assetsPage.isAddItemIconVisible()
    }

    def "goto tags verify there is no option to Create New Tag"() {
        when:
        AssetsPage assetsPage = browser.page AssetsPage
        assetsPage.treeView.tags()
        then:
        at TagsPage

        when:
        TagsPage tagsPage = browser.page TagsPage
        then:
        !tagsPage.isAddItemIconVisible()
    }

    def "goto deprecated items and verify that there is no option to add another Deprecated Item"() {
        when:
        TagsPage tagsPage = browser.page TagsPage
        tagsPage.treeView.deprecatedItems()
        then:
        at DepricatedItemsPage

        when:
        DepricatedItemsPage depricatedItemsPage = browser.page DepricatedItemsPage
        then:
        !depricatedItemsPage.isAddItemIconVisible()
    }

    def "goto imported data model and verify there is no option to Import a new Data Model"() {
        when:
        DepricatedItemsPage depricatedItemsPage = browser.page DepricatedItemsPage
        depricatedItemsPage.treeView.importedDataModels()
        then:
        at ImportedDataModelsPage

        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        then:
        !importedDataModelsPage.areCreateButtonsVisible()
    }

    def "goto versions and verify there is no option to edit it"() {
        when:
        ImportedDataModelsPage importedDataModelsPage = browser.page ImportedDataModelsPage
        importedDataModelsPage.treeView.versions()
        then:
        at VersionsPage

        when:
        VersionsPage versionsPage = browser.page VersionsPage
        versionsPage.showMore()
        then:
        at VersionsPage

        when:
        versionsPage = browser.page VersionsPage
        then:
        !versionsPage.editButtonVisible()
    }

}
