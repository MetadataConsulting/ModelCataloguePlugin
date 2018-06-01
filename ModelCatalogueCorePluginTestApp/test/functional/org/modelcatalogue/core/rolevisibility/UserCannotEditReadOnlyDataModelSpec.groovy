package org.modelcatalogue.core.rolevisibility

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Ignore

@Issue('https://metadata.atlassian.net/browse/MET-1512')
@Title('Check that User should not be able to edit when logged as a viewer with read only rights for current Data Model')
@Narrative('''
- Login to model Catalogue ( as supervisor )
- Create a Data Model 
- Navigate to the Settings Menu button in the top left hand menu.
- Select Data Model ACL  from drop-down menu 
- Select newly created Data model from list 
- Grant user "user" read only access to the created data model by selecting their name from the first drop down and their permission level in the second drop down. Press 'Grant' button to update permission levels.
- Logout as supervisor  
- Login as user ( as user ) 
- Select Data Model that User has been granted read-only rights for  | Take to Data Model Page
- Check the inline edit button ( top right corner of data model homepage)  for the data model is disabled | User cannot edit the data model
''')
@Ignore
class UserCannotEditReadOnlyDataModelSpec extends Specification {
}
