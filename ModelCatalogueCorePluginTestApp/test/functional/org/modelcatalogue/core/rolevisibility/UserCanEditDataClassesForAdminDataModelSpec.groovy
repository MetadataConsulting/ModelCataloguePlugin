package org.modelcatalogue.core.rolevisibility

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1495')
@Title('Examine that User can edit data classes for Data Model they have administration rights to')
@Narrative('''
- Login to Model Catalogue as supervisor
- Select the 'create new data model' button ( black plus sign) from the top right hand menu. | Redirected to 'Create new data model' page
- Populate data model Name, Catalogue ID and description. Click save | Data Model is created. 
- Click on the Settings menu button in the top left hand menu  | Drop- down menu appears
- Select Data Model ACL from the drop-down menu | Redirected to Data Model ACL page . Data Model Permissions is the title 
- In the Data Model ACL (Access Clearance Level) page, Select the data model you just created from the list | Go to Data Model Users Permissions page  (title is name of data model) .  List  shown of users and permissions
- In Data Models Users Permissions page, From first drop down, select User's name ( User) and in the second drop down select Administration to give them administration rights
- Press the button 'Grant' in order to grand User administration rights to the data model  | User's name appears in list with Administration written in next column showing user rights. 
- Log out of Mx. | Supervisor is logged out
- Log in as User | User is Logged in
- Select a Draft Data Model
- on the tree view, select data Classes | Active Data Classes is displayed
- Select a data class | Taken to Data Class page
- Navigate to the right side and click on the form metadata link | Form Metadata section expands
- Click on the Edit button | Form Metadata Becomes a writable form with for boxes
- Fill the form and press the save button in the right hand corner ( looks like a tick )
- Check form metadata is edited
- Click on the Stewardship Metadata link if present
- Click on the edit button and fill the form
- Check that Stewardship Metadata is edited
- Click on the Metadata link if present
- Click on the edit button and fill the form
- Check that Metadata is edited     
''')
class UserCanEditDataClassesForAdminDataModelSpec extends Specification {

}
