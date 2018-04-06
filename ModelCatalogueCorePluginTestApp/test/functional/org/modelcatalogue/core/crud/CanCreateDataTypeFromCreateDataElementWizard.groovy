package org.modelcatalogue.core.crud

import geb.spock.GebSpec
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1623')
@Title('Check that a user can create data type from data element')
@Narrative('''
- Login to model catalogue as curator/curator
- Create data model - Fill the form with random Data Model Name
- Select Data Elements in the tree and Create a new Data Element. 
- During the creation of the Data Element (on the bottom input field), write a non existing data type name and click create new.
- Create Data Type Pop-up  Wizard appears. Fields are populated with name of data type. Click save-
- Data Type is created and populated in the data element form.  
- Click save in the data element form
- Verify the data model has a data element and a data type
- Verify that Data Element is related to the Data Type (visiting the data element detail page shows a link to the data type)
''')
class CanCreateDataTypeFromCreateDataElementWizard extends GebSpec {
}
