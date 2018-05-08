package org.modelcatalogue.core.datamodel

import geb.spock.GebSpec
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1766')
@Title('Check Model Policy - enumeratedType property')
@Narrative($/
 - Login to Metadata Exchange as supervisor or curator | Login successful
 - Navigate to the top right menu and click on the Settings menu button | Settings menu drop-down appears
 - Select Data Model Policies from Settings menu drop-down | Redirected to Data Model Policies page is displayed. 'Data Model Policies' is the title
 - From list of Data Model Policies, select 'Enumeration Checks' | Redirected to 'Enumeration Checks' policy main page. 'Enumeration Checks' is the title.
 - Check that the Enumeration Policy Text is correct . | Enumeration Checks Policy Text is the same as shown below:
//key-value should be lowercase and underscore separated and no special characters
check enumeratedType property 'enumAsString' apply negativeRegex: /.*"key"\s*:\s*(?!"[a-z0-9]+").*/
/$)

class CheckDataModelPolicyEnumeratedTypeSpec extends GebSpec {
}
