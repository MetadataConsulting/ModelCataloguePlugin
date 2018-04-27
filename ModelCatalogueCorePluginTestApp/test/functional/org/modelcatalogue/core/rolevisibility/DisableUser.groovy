package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1728')
@Title('Disable a user')
@Narrative('''
 - Login to Metadata Exchange as curator | Login successful
 - Select the (plus) 'Create New Data Model' button from the top right hand menu | Redirected to 'Create Data Model' page
 - Fill in the Name, Catalogue ID, Description and click Save | New Data Model is created
 - Log out of Metadata Exchange | Log out successful
 - Login to Metadata Exchange as supervisor | Login successful
 - Select a data model that was created by the Curator | Redirected to the main page of the Data Model
 - Scroll down on the main display panel and select the Activity tab | Activity tab is open
 - In the Activity tab click on the username (curator) next to the user icon in the Author column | Redirected to user profile page
 - Navigate to the right side and click on the disable user button in the 'User Profile' menu (below the normal top right hand menu). It looks like a circle with a line through it | A pop-up dialogue box appears asking 'Do you want to disable user?'
 - In the disable user pop-up dialogue box, select the OK button | User is disabled
 - Log out of Metadata Exchange | Logout successful
 - Login to Metadata Exchange as curator | 'Sorry your account is disabled' appears in the login dialogue box and curator cannot login
''')

class DisableUser extends GebSpec {
}
