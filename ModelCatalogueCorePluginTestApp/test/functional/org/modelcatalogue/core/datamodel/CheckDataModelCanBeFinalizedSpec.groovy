package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1604')
@Title('Check that user is able to finalize a data model')
@Narrative('''
 - Login to Metadata Exchange As curator | Login successful
 - Select the 'Create Data Model' (plus sign) button from the menu in the top-right of the screen | Redirected to 'Create Data Model' page
 - Fill in Name, Catalogue ID, Description and press Save. | Directed to main page of Data Model
 - Navigate to the top left hand menu and select Data Model button | Data Model drop-down menu appears
 - Scroll down and select option 'Finalize' from the list | 'Finalize Data Model' dialogue box appears
 - Fill in Semantic version number and Revision notes and select the 'Finalize' button | 'Finalizing' process dialogue box appears
 - Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Press 'Hide' on the 'Finalizing' process box | Redirected to finalized version of the Data Model
 - Verify that data model is finalized by Selecting Data Classes from the tree navigation panel on the right | Taken to the 'Active Data Classes' page
 - Verify that you cannot add a new data class to the data model | No green plus button is present to add new data class
''')

class CheckDataModelCanBeFinalizedSpec extends GebSpec {
}
