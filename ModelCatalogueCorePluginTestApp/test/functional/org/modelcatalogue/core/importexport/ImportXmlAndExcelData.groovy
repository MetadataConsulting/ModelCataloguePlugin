package org.modelcatalogue.core.importexport

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1461')
@Title('Import Xml and Excel data')
@Narrative('''
- Login to model catalogue as curator/curator
- Create a new Data Model by selecting the create new data model button (black plus sign) in the top left hand corner | Redirected to Create New Data Model page 
- Populate form with Data Model Name, Catalogue ID and description . Press save button to create data model. | Data Mode is created. User is directed to Data Model page 
- Select Data Types from the tree navigation on the left | Taken to data types page. Active Data Types is displayed as title. 
- Press the green plus button to create new data type | Data Type Wizard pop-up appears
- Populate name, catalogue ID and description. Then select the enumerated option out of the radio button options at the bottom of the form | the form extends to give Value and description options for creation of enumerations
- Populate Value and description for the enumerated data type. Press the plus sign to add more enumerations.  
- Click save | Data type is saved
- Go to main page of data model
- Navigate to the top menu bar, click on the' Export' menu button and select Export to catalogue XML  | Export of XML begins
- XML file is downloading | XML downloaded
- Open the XML file with a suitable text-editor. Make a few change to XML by adding a new enumerated option for the enumerated data type.
- Save the xml file and go back to the data model.
- navigate to Import button (top- left-  next to create button)
- Click on the Import button and Select to import catalogue XML 
- Upload the change file, type the name and save
- Once file has finished uploading  file name should appear in blue in the history tab below loading box.
- Select the white 'show more' button ( with plus sign on it) to the left of the file name 
- Select option to download asset, download and open in text editor 
- Verify that the edit you made to the enumerated type is present in this version of the catalogue xml
- Repeat steps3-14  more than once, each time verifying that edits are present. 
- Check that an error message is not displayed       
''')
class ImportXmlAndExcelData extends Specification {
}
