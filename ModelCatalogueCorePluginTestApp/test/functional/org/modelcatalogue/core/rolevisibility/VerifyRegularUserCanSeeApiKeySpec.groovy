package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1563')
@Title('Verify that regular user can see api key')
@Narrative('''
 - Login to Metadata Exchange as supervisor | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
 - Logout of the Metadata Exchange | Logout successful
 - Login to Metadata Exchange as curator | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
 - Log out of the Metadata Exchange | Logout successful
 - Login to Metadata Exchange as viewer | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
 - Logout of Metadata Exchange | Logout successful.
 - Login to Metadata Exchange as user | Login successful
 - Navigate to the top right hand menu and select the User profile menu button | Profile menu drop-down appears
 - Select the 'API Key' option visible on the drop down list | Redirected to API key page. 'Api Key' is visible as title.
 - On the API key page, Press the 'Regenerate API Key' button and check that the API key either changes or is generated | API changes
''')

class VerifyRegularUserCanSeeApiKeySpec extends GebSpec {
}
