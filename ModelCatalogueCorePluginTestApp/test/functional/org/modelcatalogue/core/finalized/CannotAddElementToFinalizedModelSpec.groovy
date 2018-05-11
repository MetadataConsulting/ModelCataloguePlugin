package org.modelcatalogue.core.finalized

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1566')
@Title('Check that When Data Model is finalized,you are not able to add new elements')
@Narrative($/
 - Login to Metadata Exchange as curator | Login Successful
 - Click on the 'Create New Data Model' (plus sign) button in the top right hand menu. | Redirected to Create Data Model page
 - Populate form fields with Name, Catalogue ID and description. Click the Save button. | Data Model is created. Redirected to new Data Model main page
 - Select the 'Data Model' menu button from the top left hand menu. | Data Model menu button drop-down appears
 - Select option to 'Finalize' data model | Finalize Data Model pop-up appears.
 - Click OK button within the 'Finalize' data model pop up. | Data Model is finalized. Verify that it is shown as finalized in display panel.
 - In the now- finalised Data Model, Navigate using the tree-navigation and click on the Data Elements tag | Data Elements page is shown in display panel. 'Active Data Elements' is title
 - Verify that you can not create a new data element. There is no plus button or top-left hand 'Data Elements' menu button present. | No button / way to create new Data Element
/$)

class CannotAddElementToFinalizedModelSpec extends GebSpec {
}
