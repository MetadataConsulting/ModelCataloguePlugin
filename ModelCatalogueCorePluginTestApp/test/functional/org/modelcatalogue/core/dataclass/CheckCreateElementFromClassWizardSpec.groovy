package org.modelcatalogue.core.dataclass

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1496')
@Title('Examine that user can create data elements on the fly from data class wizard')
@Narrative($/
 - Login to Metadata Exchange As curator | Login successful
 - From top-right hand menu, select the 'Create Data Model' button (plus sign) | Redirected to 'Create Data Model' page
 - Fill in form with Name, Catalogue ID, Description. Press the Save button | New data model is created. Redirected the new data model's main page
 - Using the tree-navigation panel, navigate and select Data Classes . | Display panel on right opens up Data Classes main page. Title is 'Active Data Classes'
 - Select the (create) New Data Class button in the menu on the left side. | 'Data Class Wizard' pop-up dialogue box appears
 - Fill the form with Name, Catalogue ID and Description | Form fields are filled
 - From the tabs in the top of the dialogue box, select 'Elements' | Elements section of the Data Class Wizard pop-up appears.
 - In the search bar (underneath the title 'Data Elements'), type in a name of a new Data Element. | Drop-down menu appears from search bar
 - From drop-down menu, select option to 'Create new' Data Element | 'Create Data Element' pop-up dialogue box appears
 - Fill 'Create Data Elements' form with Name, Catalogue ID and description. Press Save button. | Form fields are filled. New Data Element is created. New data element name is listed under 'Data Elements' title in Data Class Wizard.
 - In the search bar (underneath the title 'Data Elements'), type in a name of a new Data Element. | Drop-down menu appears from search bar
 - Ignore drop down. Instead click on Green plus-sign button to the right of the search bar. | New data element is created immediately. Data Element name is shown under 'Data Element' title ( and above the 'Data Elements' search bar in Data Class Wizard.
 - In the Data Class Wizard, select the green tick button to Save | New Data Class is created. Data Class Wizard presents option to Close or Create Another Data Class.
 - Select Close button in Data Class Wizard to close the Wizard. | Directed to Data Classes main page. New Data Class is listed
 - Verify that newly created Data Class is listed in data class main page under 'Active Data Classes' | Data Class is listed.
 - Select the newly created Data Class | Directed to Data Class main page within display panel.
 - Check that both the data element is listed under 'Data Elements' section within the Data Class display panel . | Data Elements are listed.
 - Within the tree-navigation panel, navigate and select 'Data Elements' tag | Display panel opens up Data Elements main page. Page title is 'Active Data Elements'
 - Verify that in list under 'Active Data Elements' that the newly created data elements are displayed | Data Elements are displayed
/$)

class CheckCreateElementFromClassWizardSpec extends GebSpec {
}
