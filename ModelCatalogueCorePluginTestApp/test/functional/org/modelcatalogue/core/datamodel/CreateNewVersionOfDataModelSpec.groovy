package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1756')
@Title('Create a new version of a data model')
@Narrative($/
 - Login to Metadata Exchange as curator | Login successful
 - Click on the 'Create Data Model' (plus sign) button in the top right hand menu. | Redirected to 'Create Data Model' page
 - Populate form fields with Name, Catalogue ID and description. Click the Save button. | Data Model is created. Redirected to new Data Model main page
 - Select the 'Data Model' menu button from the top left hand menu. | Data Model menu button drop-down appears
 - Select option to 'Finalize' data model | Finalize Data Model pop-up appears.
 - Fill in 'Finalize Data Model' pop-up with Semantic Version number and revision notes. Click on 'Finalize' button | 'Finalizing' process dialogue box appears
 - Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Click 'Hide' on the 'Finalizing' process box | Data Model is finalized. Redirected to main page of Finalized data model in both display and tree navigation panel. Verify that it is shown as finalized in display panel
 - Navigate to the top left menu and click on the Data Model menu button | Data Model menu button drop-down appears
 - Select option to create 'New Version' from drop-down | New Version of Data Model pop-up dialogue box appears.
 - Fill form with Semantic Version number. Click the 'Create New Version' button | 'Create new version' process dialogue box appears. New Version of Data Model page is open
 - Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Click 'Hide' on the 'Create new version' process box | New Version of Data Model is created
 - Using tree-navigation panel select Versions tag. | Versions tag opens and Versions page is displayed in Display panel. Title is [Data Model name] History.
 - Select the (plus sign) 'Show more' button next to the title of the new draft version of the Data Model | Draft data model info panel appears below name
 - From menu in the info panel, select the 'Data Model' button | Data Model drop-down menu appears
 - Select option to 'Delete' draft version of data model. | Delete data model pop up appears - asking 'Do you really want to delete Data Model [Data Model Name]?'
 - Select the OK button in the 'Delete Data Model' pop up. | Data Model is deleted. Redirected to Dashboard. Click the Data Model link to return to Data Model page.
 - Select 'Data Model' button from top-left menu . | Data Model button menu drop-down appears
 - Verify that you can select the option 'New Version' | Option is selectable
/$)

class CreateNewVersionOfDataModelSpec extends GebSpec {
}
