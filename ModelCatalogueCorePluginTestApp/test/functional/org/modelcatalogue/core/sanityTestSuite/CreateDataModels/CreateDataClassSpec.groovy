package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.modalSuccessButton

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
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
    private static final String wizardSummary = 'td.col-md-4'
    private static final String dataClass = 'td.col-md-4>span>span>a'
    private static final String modelCatalogue= 'span.mc-name'
    private static final String exitButton= 'button#exit-wizard'
    private static final String deleteBtton = 'a#delete-menu-item-link>span:nth-child(3)'
    private static final String dataClassButton = 'a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String   greenButton='div#isBasedOn-changes>div:nth-child(3)>table>tfoot>tr>td>table>tfoot>tr>td:nth-child(1)>span'
    private static final String  destinationTable='div#isBasedOn-changes>div:nth-child(3)>table>tbody>tr>td:nth-child(2)'
    private static final String   type='div#isBasedOn-changes>div:nth-child(3)>table>tbody>tr>td:nth-child(3)'
    private static final String  dataClassCreated='tbody.ng-scope>tr:nth-child(2)>td:nth-child(1)>span>span>a'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000
    private static final String  dataWizard ='div.alert'
    private static final String  delete ='a#delete-menu-item-link>span:nth-child(3)'
    private static final String  isBasedOnTag='ul.nav-tabs>li:nth-child(3)>a>span:nth-child(1)'
    private static final String   search='input#element'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'

    def "login and navigate to the model "() {
        when:
        loginAdmin()
        select 'Test 3'
        selectInTree 'Data Classes'

        then:
        check rightSideTitle contains 'Active Data Classes'
    }

    def "navigate to create data classes page"() {
        when:
        click create

        then:
        check modalHeader contains "Data Class Wizard"
    }

    def "create data class"() {
        when: ' fill data class step'
        fill nameLabel with "NEW_TESTING_MODEL "
        fill modelCatalogueId with "${UUID.randomUUID()}"
        fill description with 'THIS IS MY DATA CLASS'

        then:
        check metadataStep enabled

        when: 'fill metadata step'
        click metadataStep
        fillMetadata foo: 'one', bar: 'two', baz: 'three', fubor: 'four'

        and: 'click on parent button'
        click parentStep

        then:
        check finishButton displayed

        when: 'fill parent step'
        click formSection
        fill label with 'TEST_LABEL'
        fill section_title with 'MY_TITLE'
        fill instruction with 'this is my instruction'
        fill page_number with '1'

        and: 'click on occurrence '
        click occurrence

        then:
        check finishButton displayed

        when:
        fillMetadata 'Min Occurs': '1', 'Max Occurs': '10'
        // click on appearance
        click appearance
        fill name with ' this is my name'

        then:
        check elementStep displayed

        when: 'fill Element'
        click elementStep
        fill dataElement with 'TEST_ELEMENT'
        click plusButton
        click raw

        and:
        click modalSuccessButton
        fillMetadata foo: 'five'

        and: 'click green button'
        click finishButton
        Thread.sleep(2000L)
        click exitButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check wizardSummary contains "NEW_TESTING_MODEL"

        cleanup:
        click exitButton
    }

    def "create a data class and create a relationship is based on"(){
        when:
        selectInTree 'Data Classes'

        then:
        check rightSideTitle is 'Active Data Classes'

        when:
        click create

        then:'check the title of the page'
        check modalHeader is 'Data Class Wizard'

        when: 'fill the data class form'
        fill nameLabel with'TESTING_CLASS'
        fill modelCatalogueId with 'ME-34567'
        fill description with 'THIS IS MY TESTING DATA CLASS'

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check dataWizard contains 'TESTING_CLASS'

        when:
        click exitButton

        and:
        selectInTree 'Data Classes'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        $(dataClassCreated).text().contains('TESTING_CLASS')


        when:'select the created data class and created a relationship based on'
        click dataClassCreated
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click isBasedOnTag

        and:
        click greenButton
        fill search with 'NEW_TESTING_MODEL' and Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS) and pick first item
        click modalPrimaryButton

        then:
        check destinationTable contains 'NEW_TESTING_MODEL'

        and:
        check type is 'Data Class'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
    }

    def "delete the created data class"() {
        when:
        click modelCatalogue

        and:
        select 'Test 3'
        selectInTree 'Data Classes'

        then:
        check rightSideTitle contains 'Active Data Classes'

        when:
        click dataClass

        and:
        Thread.sleep(1000L)
        click dataClassButton

        and:
        click deleteBtton

        and:
        click modalPrimaryButton

        then:
        check wizardSummary gone
    }
}
