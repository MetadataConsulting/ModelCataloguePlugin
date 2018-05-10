package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1463')
@Title('A curator is able to delete a data class on draft data model that they have administration rights to')
@Narrative($/
 - Login to Metadata Exchange as supervisor | Login successful
 - Select the 'Create Data Model' button (plus sign) from the top right hand menu. | Redirected to 'Create Data Model' page.
 - Fill form field with Name, Catalogue ID and Description. Press the save button. | New Data Model is created. Redirected to the Data Model page.
 - Using the tree-navigation panel, navigate to the Data Classes tag and select it to go to the Data Classes list page. | Display panel redirected to Data Classes list page. Title is 'Active Data Classes'.
 - Select the 'New Data Class' button from the top left hand menu . | Data Class Wizard pop-up dialogue box appears.
 - Fill Data Class Wizard form fields with Name, Catalogue ID and Description. Select the green tick 'Save' button from the top right hand corner of the Data Class Wizard. | New Data Class is created. Data Class Wizard offers option to create another Data Class or Close.
 - Select option to Close Data Class Wizard. | Data Class Wizard closes. Directed to Active Data Classes page.
 - Navigate to the top right hand menu and click on the Settings menu button. | Settings menu button drop-down appears
 - Select option 'Data Model ACL' from list. | Redirected to Data Model ACL page. Title is 'Data Model Permissions'.
 - Select the recently created Data Model from list. | Redirected to Data Models permissions page. Title is [Data Model name].
 - In first drop down select 'curator' from list. In second drop down select 'administration' from list. Then click the 'Grant' button to grant curator administration rights over the model. | Curator's name is listed underneath names of users who have access to Data Model.
 - Logout as Supervisor | Logout successful.
 - Login as Curator | Login successful
 - From list on main homepage of Metadata Exchange, select Data Model recently created. | Directed to main page of Data Model
 - Using tree-navigation panel, select Data Classes tag to open the Active Data Classes list page. | Data Classes list page is opened. Title is 'Active Data Classes'
 - Select the Data Class name/link to open it in the display panel. | Data Class main page is opened up in the display panel
 - Navigate to 'Data Class' top left menu button. | Data Class menu button drop-down appears.
 - Select option 'Delete' to delete Data Class. | Delete pop-up appears asking 'Do you really want to delete Data Class [data class]?'.
 - Select the OK button to continue deleting Data Class. | Data Class is deleted. Redirected to 'Data Classes' page.
/$)

class CuratorWithAdminCanDeleteClassInDraftModelSpec extends GebSpec {
}
