package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1441')
@Title('Verify that a curator is not able to delete a finalized Data Model after creation')
@Narrative('''
 - Login to Metadata Exchange as Curator | Login Successful
 - Click on 'Create New Data Model' button ( plus sign) in the top-right hand menu | Redirected to the 'Create New Data Model' page
 - Fill in Name, Catalogue Id, Description, and press save | Data Model is created. Redirected to main page of new data model
 - Navigate to the top left hand menu and click on the Data Model menu button | Data Model menu drop-down appears
 - Scroll down and select option 'finalize' | The 'Finalize Data Model' pop-up dialogue box appears
 - Fill in the semantic version number and the revision notes. Click 'Finalize' button | Data Model is finalized. Redirected to the finalized data model main page
 - On the top left hand menu, click on the data model button on the left hand top menu | Data Model menu drop-down appears
 - If present, Click on the delete option to delete data model. | The delete option is disabled or absent
 - The data model is not deleted
''')

class VerifyCuratorCannotDeleteFinalizedDataModelSpec extends GebSpec {
}
