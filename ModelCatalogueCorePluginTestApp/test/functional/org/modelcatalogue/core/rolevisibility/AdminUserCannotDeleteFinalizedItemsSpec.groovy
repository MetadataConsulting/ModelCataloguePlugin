package org.modelcatalogue.core.rolevisibility

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1769')
@Title('Examine that a user with administration rights is not able to delete a finalised data class, data element, data type etc')
@Narrative($/
 - 1. Login to Metadata Exchange As supervisor | Login Successful
 - 2. Click on the 'Create Data Model' button (plus sign) from top right hand menu. | Redirected to 'Create Data Model' page
 - 3. Fill the form with Name, Catalogue ID, Description and press the 'Save' button. | New Data Model is created. Redirected to the Data Model main page.
 - 4. Using tree-navigation structure, select Data Classes tag to go to the 'Active Data Classes' page. | Redirected in Display panel to the Data Classes page. Title is 'Active Data Classes'
 - 5. Navigate to the top left hand menu and click on the 'New Data Class' menu button. | New Data Class Wizard pop-up appears. Title is 'Data Class Wizard'.
 - 6. Fill form with Name, Catalogue ID and Description. Click on the green tick save button in the top right hand of the Data Class Wizard pop-up. | New Data Class is created. Option given in Data Class Wizard to create another or close.
 - 7. Choose option to Close Data Class Wizard. | Data Class Wizard is closed. Redirected to Draft or Active Data Classes page.
 - 8. Navigate using tree-navigation panel and select Data Elements tag to open up Data Elements main list page. | Redirected to Data Elements list page in display panel. Title is 'Active Data Elements'
 - 9. Select the green plus button to create a new Data Element. | Create Data Element pop-up dialogue box appears.
 - 10. Fill 'Create Data Element' pop-up form fields with Name, Catalogue ID and Description. Click the 'Save' button. | New Data Element is created. Directed back to 'Active Data Elements' list page.
 - 11. Navigate using tree-navigation panel and select Data Types tag to open up Data Types main list page. | Redirected to Data Types list page in display panel. Title is 'Active Data Types'
 - 12. Select the green plus button to create a new Data Type. | Create Data Type pop-up dialogue box appears.
 - 13. Fill 'Create Data Type' pop-up form fields with Name, Catalogue ID and Description. Select Enumeration type as the Data Type from the radio check buttons . | Enumerations key and value form fields appear underneath radio checkboxes.
 - 14. Fill in up to four Key and Value enumerations. Press the 'Save' button. | Data Type is created. Redirected to 'Active Data Types' list page.
 - 15. Navigate using tree-navigation panel to Measurement Units tag to open the Measurement Units list page. | Redirected to Measurement units list page in display panel. Title is 'Active Measurement Units'
 - 16. Click on the green plus-sign button to create a new measurement type. | Create Measurement Unit pop-up dialogue box appears.
 - 17. Fill form fields in Create Measurement Unit pop-up dialogue box with Name, Symbol, Catalogue ID, Description and click the Save button. | New Measurement Unit is created. Redirected to 'Active Measurement Units' list page.
 - 18. Navigate using tree-panel navigation panel and select Business Rules tag to open up Business Rules page. | Redirected in display panel to business rules page. Title is 'Active Business Rules' .
 - 19. Click on green plus sign button to create new Business Rule. | Create Business Rule pop-up dialogue box appears.
 - 20. Fill form fields of Create Business Rule dialogue box and click Save button. | New business rule is created.
 - 21. Navigate using tree-panel navigation panel and select Assets tag to open up Assets page. | Redirected in display panel to Assets page. Title is 'Active Assets' .
 - 22. Click on green plus button to create a new asset. | Create Asset pop-up dialogue box appears.
 - 23. Fill form fields of Create Asset pop-up dialogue box . Browse to select an asset to import. Click the save button. | New asset is created.
 - 24. Using tree-navigation panel, select Tags tag to go to Tags list page. | Redirected in display panel to Tags list page. Title is 'Active Tags'
 - 25. Click on green plus sign button to create new tag. | Create Tag pop-up dialogue box appears.
 - 26. Fill 'Create Tag' form fields and click Save button. | New Tag is created.
 - 27. Navigate back to the main page of the data model by selecting the Data Model name in the tree-navigation panel. | Redirected to main Data Model page.
 - 28. Click on the 'Data Model' menu button in top left hand menu | 'Data Model' button menu drop-down appears.
 - 29. Select option to 'Finalize' data model from Data model button menu drop down. | Finalize Data Model drop-down appears.
 - 30. Fill in Semantic Version Number and Revision notes and click the 'Finalize' button. | 'Finalizing' process dialogue box appears
 - 31. Wait until text in messages panel ends with 'COMPLETED SUCCESSFULLY'. Click 'Hide' on the 'Finalizing' process box | Data Model is finalized. Redirected to finalized Data Model.
 - 32. Navigate to top right hand menu and click on the Settings menu button. | Settings menu button drop-down opens
 - 33. Select option 'Data Model ACL' to go to the Data Model ACL permissions page. | Redirected to Data Model ACL page. Title is 'Data Model Permissions'.
 - 34. Select the Data Model created out of the list of Data Models. | Redirected to Data Models' permissions page. Title is [Data Model Name]
 - 35. From first drop-down list, select 'Curator' . From the second drop-down select 'Administration' and click the 'Grant' button to grant Curator Administration rights to the Data Model. | Curator is granted Administration rights to the Data Model. Their name and details appear in list of users with rights to view/administer the data model
 - 36. Logout as Supervisor | Logout successful
 - 37. Login as Curator | Login Successful
 - 38. Select the Data Model created by Supervisor. | Directed to Data Model main page.
 - 39. Using the tree-navigation panel, select the Data Classes tag. | Redirected to Data Classes page.
 - 40. Select Data Class from list. | Directed to Data Class main page in display panel.
 - 41. Navigate to top left menu and click on the 'Data Class' menu button. | Data Class menu drop-down appears
 - 42. Select option to 'Delete' the data class. | Nothing happens
 - 43. Verify that the Delete option/ button is disabled. | Delete is disabled on finalised model.
 - 44. Navigate to Data Elements tag using tree navigation panel. | Redirected in display panel to Data Elements list page.
 - 45. Select a Data Element from list . | Redirected in display panel to Data Element main page
 - 46. Navigate to 'Data Element' menu button from top left menu . | Data Element menu drop-down appears.
 - 47. Select option to 'Delete' the data element | Nothing happens.
 - 48. Verify that the Delete option/button is disabled. | Delete is disabled in the finalised model.
 - 49. Repeat steps 45 - 48 with Data Types, | Delete is disabled for Data Types in the finalised model
 - 50. Repeat steps 45 - 48 with Measurement Units. | Delete is disabled for Measurement Units in the finalised model
 - 51. Repeat steps 45 - 48 with Business Rules. (Menu button is called 'Validation Rule') | Delete is disabled for Business Rules in the finalized model.
 - 52. Repeat steps 45 - 48 with Assets. | Delete is disabled for Assets in the finalised model.
 - 53. Repeat steps 45 - 48 with Tags. | Delete is disabled or absent for Tags in the finalised model.
/$)

class AdminUserCannotDeleteFinalizedItemsSpec extends GebSpec {
}
