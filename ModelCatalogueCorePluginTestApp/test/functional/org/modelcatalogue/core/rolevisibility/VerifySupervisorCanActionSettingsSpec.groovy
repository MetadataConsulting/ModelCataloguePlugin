package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1485')
@Title('Verify that user ( role Supervisor) can click on settings menu button and navigate')
@Narrative('''
 - Login to Metadata Exchange As Supervisor | Login successful
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Scroll down and click on the Data Model ACL option | Directed to Data Model ACL page ( title is 'Data Model Permissions')
 - On the top right hand menu, select the Settings menu button | Settings menu drop-down appears
 - Select 'Code version' option from the drop down menu | Redirected to Code version page. Code version is displayed.
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Mapping Utility' option from the drop down menu | Redirected to Mapping Utility page. 'Mapping Batches is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Activity' option from the drop down menu | Redirected to User Activity page. 'User Activity' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Reindex Catalogue' option from the drop down menu | Redirected to Reindex Catalogue page. 'Reindex Catalogue' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Relationship Types' option from the drop down menu | Redirected to Relationship Types page. 'Relationship Types' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Data Model Policies' option from the drop down menu | Redirected to Data Model Policies page. 'Data Model Policies' is displayed as title
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Monitoring' option from the drop down menu | A new tab in browser should open onto the Monitoring page.
 - Navigate back to page with Metadata Exchange
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Logs' option from the drop down menu | A new tab in browser should open onto the Logs page ( still within the Metadata Exchange). Title should display as 'Logs'.
 - Navigate to top menu and click on the Settings menu button from the top-right menu | Settings menu drop-down appears
 - Select 'Feedbacks' option from the drop down menu | Redirected to Feedbacks page. 'Feedbacks' is displayed as title
''')

class VerifySupervisorCanActionSettingsSpec extends GebSpec {
}
