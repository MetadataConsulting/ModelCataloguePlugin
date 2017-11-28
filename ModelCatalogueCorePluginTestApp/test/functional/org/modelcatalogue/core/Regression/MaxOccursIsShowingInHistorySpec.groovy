package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class MaxOccursIsShowingInHistorySpec extends AbstractModelCatalogueGebSpec{

    static final String stepImports = "#step-imports"
    static final String wizardName = 'div.create-classification-wizard #name'
    private static final String  createButton='a#role_data-models_create-data-modelBtn'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'
    private static final String  finishButton='button#step-finish'
    private static final String  search ='input#element'
    private static final String  dataWizard ='div.alert'
    private static final String  alert ='div.alert>div>span'
    private static final String   deleteButton='a#delete-menu-item-link>span:nth-child(3)'
    private static final String  exitButton='div.modal-footer>button:nth-child(2)'
    private static final String  stepParent='button#step-parents'
    private static final String  occurrence='ul.nav-pills>li:nth-child(3)>a'
    private static final String  modelCatalogue='span.mc-name'
    private static final String  metadataButton='label.expand-metadata'
    private static final String   table='#data-elements-changes > div.inf-table-body > table > tbody > tr > td:nth-child(3) > span > span'
    private static final String  firstRow='tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)'
    private static final String  dataClass='tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a'
    private static final String  createRelationshipButton='a#create-new-relationship-menu-item-link>span:nth-child(3)'
    private static final String parentOf ='#type > option:nth-child(6)'
    private static final String  dataClassButton='a#role_list_create-catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  menuButton='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  minOccurs='input#minOccurs'
    private static final String  maxOccurs='input#maxOccurs'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 3000

    def "login to model catalogue and create a data model"() {
        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
        click createButton

        and:'fill the form '
        fill nameLabel with 'TESTING_DATA_MODEL_MAX'
        fill modelCatalogueId with 'MET-00263'
        fill description with 'this my testing data'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        then:
        check stepImports enabled

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:'import  Clinical Tags'
        fill wizardName with 'Clinical Tags'
        selectCepItemIfExists()

        and:'create the dat class'
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click closeButton

        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL'
    }

    def "create data class and add occurrence"() {
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
        fill modelCatalogueId with 'ME-345'
        fill description with 'THIS IS MY TESTING DATA CLASS'


        and:'select parent and fill the occurrence'
        click stepParent

        and:
        click occurrence

        and:
        fill minOccurs with '1'
        fill maxOccurs with '*'

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check dataWizard contains 'TESTING_CLASS'

        when:
        click exitButton

        and:
        selectInTree 'Data Classes'

        then:
        check 'td.col-md-4' contains 'TESTING_CLASS'
    }

    def "create a data class without occurrence"() {
        when:
        click dataClassButton

        then:'check the title of the page'
        check modalHeader is 'Data Class Wizard'

        when: 'fill the data class form'
        fill nameLabel with'TESTING'
        fill modelCatalogueId with 'ME-322'
        fill description with 'THIS IS MY TESTING DATA CLASS'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check dataWizard contains 'TESTING'

        when:
        click exitButton

        then:
        check firstRow contains 'TESTING'
    }

    def "verify that max occurrence appears into history"() {
        when:
        click dataClass
        click menuButton

        and:'click on the create relationship and select is based on from the drop down list'
        click createRelationshipButton
        click parentOf

        and:'select destination'
        fill search with  'TESTING_CLASS' and pick first item

        and:'click on the metadata and select occurrence'
        click metadataButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click occurrence

        and:
        fill minOccurs with'1'
        fill maxOccurs with '10'

        and:
        click modalPrimaryButton

        then:
        check table contains 'Min Occurs: 1\n' +
            'Max Occurs: 10'
    }

    def "delete data model"() {
        when:
        click modelCatalogue
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        select 'TESTING_DATA_MODEL_MAX'

        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL_MAX'

        when:
        click menuButton

        and:
        click deleteButton
        click modalPrimaryButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        noExceptionThrown()
       // check alert contains 'TESTING_DATA_MODEL_MAX is deleted'
    }
}
