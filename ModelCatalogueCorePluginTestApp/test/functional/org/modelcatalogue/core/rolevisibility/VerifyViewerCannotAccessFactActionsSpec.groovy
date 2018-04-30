package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1959')
@Title('Verify that the viewer does not have access to Fast Actions(Imports)')
@Narrative('''
 - Login to Metadata Exchange As Supervisor | Login successful
 - Select Settings menu button from right-hand top menu | Settings menu Drop-down appears.
 - Select Users from the Settings drop-down menu | Redirected to the backend page with title 'Spring Security Management Console' with a User Search box present.
 - In the form box titled 'Username' type the name of User X (that has a User role) and press the search button | A list appears under the User Search box with results
 - Select User X's name from the list of results | Taken to page with 'Edit User' title. User details are shown
 - Select the tab titled 'Roles' next to 'User Details' | User Roles are shown
 - Verify that User X only has ROLE_USER ticked | Verified that User X has a role of user and nothing more
 - Log out of Metadata Exchange | Log out successful
 - Login to Metadata Exchange As User X | Login successful
 - On the top right hand-menu, Select the Settings menu Button | Settings menu drop-down appears
 - Verify that the only options on the settings menu drop down are: Code Version, Relationship Types, Data Model Policies and Feedbacks | Options for Settings menu drop down are limited to: Code Version, Relationship Types, Data Model Policies and Feedbacks
 - Logout of the Metadata Exchange | Logout successful
''')

class VerifyViewerCannotAccessFactActionsSpec extends GebSpec {
}
