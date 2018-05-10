package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1604')
@Title('Data Model is created with selected policies')
@Narrative('''
 - Login to Metadata Exchange As curator | Login successful
 - Click on the 'Create Data Model' button (plus sign) from top-right hand menu | Redirected to 'Create Data Model' page
 - Fill the form with Name, Catalogue ID, Description and select from multiple choice list of data policies either Unique of Kind or Default Checks. Click Save button | Data Model is created. Redirected to new data model main page
 - Examine that in the Display panel on the right side, that underneath the Data Model name, in the Policies section, the data model policy that was chosen is present. | Confirm that the Data Model has been created with the Data Model Policies
''')

class CheckDataModelPoliciesSpec extends GebSpec {
}
