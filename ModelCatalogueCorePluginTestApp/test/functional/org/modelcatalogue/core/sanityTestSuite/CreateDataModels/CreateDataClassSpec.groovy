package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.modalSuccessButton





@Stepwise
class CreateDataClassSpec extends AbstractModelCatalogueGebSpec{
    private static final String metadataStep ="button#step-metadata"
    private static final String finishButton ="button#step-finish"
    private static final String parentStep ="button#step-parents"
    private static final String formSection='ul.nav-pills>li:nth-child(1)>a'
    private static final String section_title="textarea#section-title"
    private static final String label="textarea#section-label"
    private static final String instruction="textarea#section-instructions"
    private static final String page_number='input#form-page-number'
    private static final String occurrence ='ul.nav-pills>li:nth-child(3)>a'
    private static final String  appearance='ul.nav-pills>li:nth-child(4)>a'
    private static final String elementStep="button#step-elements"
    private static final String  dataElement="input#data-element"
    private static final String plusButton = "span.input-group-btn>button"
    private static final String  raw ="ul.nav-pills>li:nth-child(4)>a"
    private static final String name ="input#local-name"

    def"login and navigate to the model "(){

        when:
             loginAdmin()
             select 'Test 6'
             selectInTree 'Data Classes'

        then:
            check rightSideTitle contains 'Active Data Classes'
    }
    def"navigate to create data classes page"(){
        when:
             click create
         then:
             check  modalHeader contains "Data Class Wizard"
    }
    def"create data class"(){
        when: ' fill data class step'

             fill nameLabel with "NEW_TESTING_MODEL ${System.currentTimeMillis()}"
             fill modelCatalogueId with "${UUID.randomUUID()}"
             fill description with 'THIS IS MY DATA CLASS'

        then:
            check metadataStep enabled

        when:'fill metadata step'
             click metadataStep
             fillMetadata foo:'one',bar:'two', baz:'three',fubor:'four'

        and:'click on parent button'
           click parentStep

        then:
            check finishButton displayed

        when:'fill parent step'
            click formSection
            fill label with 'TEST_LABEL'
            fill section_title with 'MY_TITLE'
            fill instruction with 'this is my instruction'
            fill page_number with '1'

        and:'click on occurrence '
             click occurrence

        then:
              check finishButton displayed
        when:
             fillMetadata 'Min Occurs': '1', 'Max Occurs': '10'
              // click on appearance
              click appearance
               fill name  with ' this is my name'
        then:
             check elementStep displayed

        when:'fill Element'
             click elementStep
             fill dataElement with 'TEST_ELEMENT'
             click plusButton
             click raw

        and:
            click modalSuccessButton
             fillMetadata foo:'five'

        and:'click green button'
            click finishButton

        then:
             noExceptionThrown()
        //check wizardSummary is "Data Class ${NEW_TESTING_MODEL} created"

}

}
