package org.modelcatalogue.core.rolevisibility

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Ignore

@Issue('https://metadata.atlassian.net/browse/MET-1446')
@Title('Verify that curator can create a new Data class')
@Narrative('''
- 1. Login to Model Catalogue as curator/curator | Login Successfully
- 2. Select the 'Create New Data Model' button ( black plus sign) from the top right menu to create a new data model. | Redirected to 'create new data model' page
- 3. Populate data model name, catalogue ID and description. Click save | Data model is saved . User directed to data model page
- 4. Click on Data Model menu button from the top Menu Bar |  A drop down list is displayed
- 5. Navigate to drop down list and select New Data Class  | A new Data Class Wizard window open
- 6. Type 
* the Name 
* Catalogue ID
* Description  | Data are entered correctly
- 7. Click on Metadata button | Metadata window open
- 8. Enter Key and Value | Key and Value are entered successfuly
- 9. Click on Parents Tag | Parents Tag display
- 10. Verify that you Can search for Parent Data Class | Search successfuly
- 11. Click on Form Section and verify that user can type in *Label and Title *Subtitle and instructions * page Number | Data Type Successfully
- 12. Verify that checkbox on Form Section | Checkbox working as expected
- 13. Click on Form(Grid) | Form display
- 14. Verify when tick grid checkbox . Header, Initial number of rows and Max Number of row are enabled | as expected  
- 15. Verify when uncheck grid checkbox . Header, Initial number of rows and Max Number of row are disabled | As expected
- 16. Click on Ocurrence | Occurrence display
- 17. Verify that Min Occurs does not accepted String 
- 18. Click on Appearance | Appearance display
- 19. Enter name | name is entered
- 20. Click on Raw | Raw is displayed
- 21. Verify that all data input into the data class is Present in list. (for instance Key and value)
- 22. Verify that you can add new row (+) or delete row with (-)
- 23. Navigate to Children Tab on the Menu and click | Children tab open
- 24. repeat Steps 10-21 for Children data class | result as expected
- 25. Navigate to element Tab and Click | Element Tab is displayed
- 26. Repeat steps 10-21 for Data Elements | Results as expected
- 27. Click on green button | Message: data class Created is displayed. create another and close options present
- 28. click on Create Another option | Data Class create page display
- 29. Repeat steps 5-27
- 30. click on close. Verify that user is returned  to the initial data model page
- 31. Verify that new Data class has been created     
''')
@Ignore
class CuratorCanCreateANewDataClass extends Specification {
}
