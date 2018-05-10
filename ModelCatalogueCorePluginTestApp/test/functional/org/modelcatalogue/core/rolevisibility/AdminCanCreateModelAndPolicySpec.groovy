package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1443')
@Title('Test that Admin user can create a new policy when creating a data model')
@Narrative($/
 - Login to Metadata Exchange As supervisor | Login successful
 - Click on Create Data Model button (plus sign) in top right hand menu | Redirected to Create Data Model page
 - Fill form with Name, Catalogue ID, Description
 - From Policies list select one policy from either Default Checks or Unique of Kind | Checkbox is selected
 - Click on the save button at the bottom of the page | Data Model is created. Redirected to main page of new data model.
 - On display panel, check that in the Policies section the selected policy is shown | Selected policy appears in Policies section
 - To the right hand side menu (right of the Data Model Name), select the edit button. | Display panel is now editable. Search bar appears in policy section
 - In the search bar within the Policy section of the display panel, type a new name for a Policy (that doesn't yet exist) | Drop down appears from search bar with option to 'Create New'
 - Select the 'Create New' option from the drop down that appears under the search bar | 'Create Data Policy' pop-up dialogue box appears
 - Fill form with Name and set Policy Text to: "check dataElement property 'name' is 'unique'". Press the Save button | New Data Policy is created
 - Verify that in the display panel, the new data policy is listed under the Policies section of the Data Model | The new policy is listed (as well as the initial selected policy)
 - Click on the save edits (tick sign) button in the right hand menu of the data model display panel to close the edit view. | Edit view of display panel is closed
 - Select the new data policy. | Redirected to individual data policy page
 - Verify that name and policy text are correct. | Name and policy text are correct
/$)

class AdminCanCreateModelAndPolicySpec extends GebSpec {
}
