package org.modelcatalogue.core.datatype

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-2014')
@Title('Check that user is not able to create Data Element with unauthorized Data Type')
@Narrative('''
- Login to Model Catalogue As Supervisor
- Select 'create new data model ' button ( black plus sign) in the right hand top menu to create a data model | Redirected to Create New Data Model page
- Populate Data Model Name Catalogue ID, Description and click save | Data model is created. Redirected to Data Model page
- In data model, navigate to data types and click the green plus button to create new data type | Data Type pop - up Wizard appears
- Populate data wizard form with Data Type name, catalogue ID and description . Press save | Data type is created
- Repeat steps 2-5 | Second new data model containing a new data type is created
- Repeat steps 2-5 | Third new data model containing a new data type is created
- Navigate and click on Settings menu button ( top right hand side) and from drop-down menu, select Data Model ACL | Redirected to Data Model ACL page - Data Model Permissions is title
- Select the first data model that you created . | Go to data model permissions page. Data Model name is the title .
- Select Curator from drop down list and from second drop down list give them administration rights over this data model | Curator appears in list of users with access to data model
- repeat steps 9-10 for the second data model created | The curator is given administration rights for the second data model
- Do nothing in regards to permissions for the third model. | By doing nothing, assurance is given that Curator is not authorised to view the third data model.
- Log out as supervisor | supervisor is logged out
- Log in as curator | Curator is logged in
- Select first data model created from list | Directed to Data Model page
- On the tree view, select Data Elements to go to Data Elements page. | check that right side title is Active Data Elements. Page displays list of Data Elements within Data Model.
- Click on the green button at the bottom of the list | Pop up appears. Check that Model Header is Create Data Element
- Fill the name, catalogue ID and Description for the Data Element 
- At the bottom of the form, select the button on the left hand side of the box titled 'Data Type', to add a data type via import. | Pop up with search ability to search for Data type appears
- click on show All underneath the search bar to display all possible Data Types to import and check that only Data types from the first and second data mode that the curator is authorised to view appear. ( and any other data models the curator is authorised to view) | Only data models that the curator has been granted permission to view are in the list . The Data types from the third model are NOT shown
- Type in the name of the data type from second data model to see if it appears in search bar. If appears, a pop-up demanding password / stating you aren't authorised to use data model should appear. | Pop up demands you log in as a user that has been authorised to use Data Model. Click cancel and return to Data type search pop up .
- Confirm that user can only select data types that they are authorised to view .
- Select a data type from the second data model that curator is authorised to view | data type is is added into the form.
- Press save | Data type is saved
-  Confirm that new data type has been added to the data model | Data type appears in list under data types. tag
''')
class CannotCreateDataElementWithUnauthorizedDataTypeSpec extends Specification {
}
