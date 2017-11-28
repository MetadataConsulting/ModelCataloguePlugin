package org.modelcatalogue.core.Regression

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import spock.lang.Unroll

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
    private static final String   createdVersion='tr.warning>td:nth-child(1)>a:nth-child(2)'
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
    private static final String  dataClassCreated='tbody.ng-scope>tr:nth-child(2)>td:nth-child(1)>span>span>a'
    private static final String modelInTree = 'ul.catalogue-element-treeview-list-root>li>div>span>span'
    private static final String elementStep="button#step-elements"
    private static final String plusButton = "span.input-group-btn>button"
    private static final String  dataElement="input#data-element"
    private static final String  raw ="ul.nav-pills>li:nth-child(4)>a"

    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000

    def "login to model catalogue and create data model"() {
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

    def "finalized the data model and create new version"() {

        when:'refresh the page and select data model'

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

        then:'check the title of the page'
        check modalHeader is 'Data Class Wizard'

        when: 'fill the data class form'
        fill nameLabel with'TESTING_CLASS'
        fill modelCatalogueId with 'ME-34567'
        fill description with 'THIS IS MY TESTING DATA CLASS'
        click elementStep
        fill dataElement with 'TEST_ELEMENT'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click plusButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check dataWizard contains 'TESTING_CLASS'

        when:'navigate to the top menu and select finalized'
        click exitButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
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


        when:'navigate to the top menu and click on the new version'
        click dataModelMenuButton
        click newVersion

        and:'fill the version form'

        fill semanticVersion with '0.0.2'
        click createNewVersion
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click modalPrimaryButton

        and:'navigate to the tree view and select versions'
        click versionsTreeViews
        click createdVersion

        then:
        check rightSideTitle  contains 'TESTING_DATA_MODEL_NEW_VERSION'


        when:
        click dataClasses
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click createdDataClass
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains'TESTING_CLASS'
    }

    @Unroll
    def "verify that data are not duplicated"(int location,String dataElement) {
        //add a refresh
        expect:

        $("#data-elements-changes > div.inf-table-body > table > tbody > tr:nth-child($location) > td:nth-child(1) > a.preserve-new-lines.ng-binding.ng-scope").text()== dataElement
          Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        where:
        location || dataElement
        1        || 'TEST_ELEMENT'
    }
}
