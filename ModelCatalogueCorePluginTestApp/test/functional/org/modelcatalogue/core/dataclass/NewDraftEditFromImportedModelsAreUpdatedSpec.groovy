package org.modelcatalogue.core.dataclass

import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1554')
@Title('Examine that when creating a new draft - if there are Data Classes from other models -edits from imported models are updated in your model.')
@Narrative('''
- Login to Model Catalogue As curator
- Navigate to top left hand side menu, select 'create new data model' button ( looks like a black plus sign) to create a draft Model | Directed to Create new Data Model Page
- Fill form with Data Model name, catalogue ID, Description, Click save | Data Model is created and user is taken to Data Model page
- On Data Model page, using the left-hand side tree view navigate to and select "Imported Data Models" | Taken to Data Model (name) Imports page
- Select the green button to import a data model | Imports pop up appears
- In the pop-up, Under title 'Destination', either type the name of a data model in the search bar or select the button (with the book icon) to the left hand side to bring up pop-up box with a list of available data models to import. | Select a DRAFT data model to import
- Select 'Create Relationship ' button to import Data Model | Data Model is imported and shows up in list of imported data models. User is taken back to main page of model
- Navigate to the tree view and click on the Data Classes Link | Active Data Class title is displayed with list of data classes.
- Select the green plus button to create a new data class | Create Data Class wizard/pop-up appears
- Fill form with Data Class Name, Catalogue ID and Description . Click the green save button ( top right) to save Data Class | Data Class is created and appears in list of Data classes
- Click on the newly created data class and from tabs at the bottom of the Data Class info page select 'parent\' | parent tab is opened
- Select the green plus button to add a Data Class parent to the your new data class | Pop up wizard appears with destination form to select parent data class
- Select the book icon to the left of the search bar under title 'Destination' to search for Data Classes from the data model imported into this current one. | pop-up with list of data classes appears
- Add data class from imported model and save | Parent data class relationship is displayed under the parent tab of data class
- Navigate back to home page of the data model and click on data model menu at the top of the page | data model menu drop-down is displayed
- select Finalise Data model | Finalise Data Model pop-up wizard appears
- Fill in revision notes and press finalise button | Data Model is finalised
- In finalised version, navigate to top menu and select 'Data model' button | Data Model drop-down menu shown
- Select 'new version' from drop-down menu to create new version of the flnalised data model | new version pop-up wizard appears
- Fill in new version pop-up wizard semantic version form box with version number for the new version. then select 'create new version' button to create new version.
- While creating a new version tick the checkbox if available (prefer drafts for following dependent data models) | New Version of Data Model is created
- Navigate back to the Data model that you had imported and to the Data Class used in your Data Model. Select the edit button to the top left of the Data Models page. | The page changes to have editable form/text boxes
- Make some small edits to the description or edit the name by clicking the edit button on the top left corner of the data model page and selecting the tick button in its place to save the edits. | edits are saved to the data class used from the imported model.
- Navigate back to your Data Model and to the data class.
- Check that when you search in the parents tab, edits made to the data class in the imported model are carried through to your data model. | Edits are present from imported data model.
''')
@Ignore
class NewDraftEditFromImportedModelsAreUpdatedSpec extends Specification {
}
