package org.modelcatalogue.core.regressionTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.openqa.selenium.interactions.Actions
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import static org.modelcatalogue.core.gebUtils.Common.create
import static org.modelcatalogue.core.gebUtils.Common.getDescription
import static org.modelcatalogue.core.gebUtils.Common.getModelCatalogueId
import static org.modelcatalogue.core.gebUtils.Common.getNameLabel
import static org.modelcatalogue.core.gebUtils.Common.item
import static org.modelcatalogue.core.gebUtils.Common.modalHeader
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.pick
import static org.modelcatalogue.core.gebUtils.Common.rightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class CreateNewVersionFromFinalisedToDraftSpec extends AbstractModelCatalogueGebSpec {

    static final String stepImports = "#step-imports"
    static final String wizardName = 'div.create-classification-wizard #name'
    private static final String  createButton='a#role_data-models_create-data-modelBtn'
    private static final String  versionsTreeViews='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(10)>div>span>span'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'
    private static final String  finishButton='button#step-finish'
    private static final String   dataClasses='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(1)>div>span>span'
    private static final String   createdVersion='tr.warning>td:nth-child(2)>a'
    private static final String   createdDataClass='td.col-md-4>span>span>a'
    private static final String   finalize='a#finalize-menu-item-link'
    private static final String   createNewVersion='a#role_modal_modal-create-new-versionBtn'
    private static final String   semanticVersion='input#semanticVersion'
    private static final String   newVersion='a#create-new-version-menu-item-link'
    private static final String   table='tbody.ng-scope>tr:nth-child(1)>td:nth-child(4)'
    private static final String   versionNote='textarea#revisionNotes'
    private static final String   finalizeButton='a#role_modal_modal-finalize-data-modalBtn'
    private static final String    modelCatalogue ='span.mc-name'
    private static final String   dataModelMenuButton='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  dataWizard ='div.alert'
    private static final String exitButton= 'button#exit-wizard'
    private static final String modelInTree = 'ul.catalogue-element-treeview-list-root>li>div>span>span'
    private static final String elementStep="button#step-elements"
    private static final String plusButton = "span.input-group-btn>button"
    private static final String  dataElement="input#data-element"

    private static  Actions action = null

    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000


    def" login to model catalogue and create data model"(){


        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
        click createButton

        and:'fill the form '
        fill nameLabel with 'TESTING_DATA_MODEL_NEW_VERSION'
        fill modelCatalogueId with 'MET-00263'
        fill description with 'this my testing data'


        then:
        check stepImports enabled

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:'import  MET-523'
        fill wizardName with 'MET-523'
        selectCepItemIfExists()

        and:'create the data model'
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click closeButton

        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL_NEW_VERSION'
    }


    def"create data class and add data element from imported data model"() {

        when: 'refresh the page and select data model'

        click modelCatalogue

        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        select 'TESTING_DATA_MODEL_NEW_VERSION'

        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL_NEW_VERSION'


        when:
        selectInTree 'Data Classes'

        then:
        check rightSideTitle is 'Active Data Classes'

        when:
        click create

        then: 'check the title of the page'
        check modalHeader is 'Data Class Wizard'

        when: 'fill the data class form'
        fill nameLabel with 'TESTING_CLASS'
        fill modelCatalogueId with 'ME-34567'
        fill description with 'THIS IS MY TESTING DATA CLASS'
        click elementStep
        fill dataElement with 'TEST_ELEMENT'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click plusButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        fill dataElement with 'MET-523.M1.DE1' and pick first item
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        fill dataElement with 'MET-523.M2.DE1' and pick first item

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check dataWizard contains 'TESTING_CLASS'

        expect: 'navigate to the top menu and select finalized'
        click exitButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

    }

    def"finalized the data model and create new version"() {

        when:
        click modelInTree
        click dataModelMenuButton
        click finalize

        and:
        fill versionNote with 'THIS IS THE VERSION NOTE'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click finalizeButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click modalPrimaryButton

        then:
        check table contains 'TESTING_DATA_MODEL_NEW_VERSION (0.0.1) finalized'


        when: 'navigate to the top menu and click on the new version'
        click dataModelMenuButton
        click newVersion

        and: 'fill the version form'

        fill semanticVersion with '0.0.2'
        click createNewVersion
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click modalPrimaryButton

        and: 'navigate to the tree view and select versions'
        click versionsTreeViews
        click createdVersion

        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL_NEW_VERSION'

    }

    def"verify that data elements are createed"(int location,String dataElement){

        click dataClasses
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click createdDataClass
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        expect:
        $("#data-elements-changes > div.inf-table-body > table > tbody > tr:nth-child($location) > td:nth-child(1) > a.preserve-new-lines.ng-binding.ng-scope").text().contains( dataElement)
        where:
        location || dataElement
        1        || 'TEST_ELEMENT'
        2        || 'MET-523.M1.DE1'
        3        || 'MET-523.M2.DE1'


    }

    def"finalized the draft data model and create a new version"(){

        when:
        selectInTree 'TESTING_DATA_MODEL_NEW_VERSION'

        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL_NEW_VERSION MET-00263@0.0.2'

        when:
        click dataModelMenuButton
        click finalize
        then:
        check modalHeader is 'Finalize Data Model'

        when:
        fill versionNote with 'TESTING VERSION'
        click finalizeButton

        and:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click modalPrimaryButton

        then:
        check table contains 'TESTING_DATA_MODEL_NEW_VERSION (0.0.2) finalized'

        when:
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click dataModelMenuButton
        click newVersion


        then:
        check modalHeader is 'New Version of Data Model'

        when:
        fill semanticVersion with '0.0.3'
        click createNewVersion
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click modalPrimaryButton
        Thread.sleep(2000L)

        and: 'navigate to the tree view and select versions'

        Thread.sleep(4000l)
        selectInTree 'Versions'
        Thread.sleep(2000l)


        then:
        check { infTableCell(1, 1) } is '0.0.3'
        check { infTableCell(2, 1) } is '0.0.2'
        check { infTableCell(3, 1) } is '0.0.1'


    }



}
