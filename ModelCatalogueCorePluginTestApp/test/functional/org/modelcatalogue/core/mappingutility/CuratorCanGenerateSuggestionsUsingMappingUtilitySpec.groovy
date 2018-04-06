package org.modelcatalogue.core.mappingutility

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1662')
@Title('Examine that user can generate Suggestion using Mapping Utility')
@Narrative('''
- Login to Model Catalogue as curator
- On the top menu, click on the Settings menu button from the top left hand menu | Drop-down menu appears
- Select Mapping Utility form the drop dwon menu | Mapping batches page is open
- Select button to the right of the page called 'Generate Mappings' | Generate Suggestsion page is opened
- Populate form buy selecting name of Data Models from list from Data Mdoel 1 and Data Model 2 drop down. Select option for type of optimization and click button called "Generate" | Taken back to Mapping batches page, popup states that mapping suggestions are being generated. 
- Refresh the page to see Mapping suggestions | Mapping suggestions appear
- Verify that suggestion are created once page is refreshed 
''')
class CuratorCanGenerateSuggestionsUsingMappingUtilitySpec extends Specification {
}
