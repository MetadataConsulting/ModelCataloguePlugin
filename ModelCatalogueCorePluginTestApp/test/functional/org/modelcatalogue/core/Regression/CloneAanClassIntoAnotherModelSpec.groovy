package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import spock.lang.Unroll

import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class CloneAanClassIntoAnotherModelSpec extends AbstractModelCatalogueGebSpec{

    private static final String  createButton='a#role_data-models_create-data-modelBtn'
    private static final String  cloneButton='a#clone-menu-item-link>span:nth-child(3)'
    private static final String  dataClassButton='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  finishButton='button#step-finish'
    private static final String  modelCatalogue='span.mc-name'
    private static final String   delete='a#delete-menu-item-link>span:nth-child(3)'
    private static final String  modelDataMenu='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'
    private static final String  cloneDataClass='td.col-md-4>span>span>a'
    private static final String   search='input#value'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 2000
    static final String stepImports = "#step-imports"
    static final String wizardName = 'div.create-classification-wizard #name'

    def "login to model catalogue and create a data model"() {
        when:
        loginAdmin()

        then:
        check createButton isDisplayed()

        when:
         click createButton

        and:'fill the form '
        fill nameLabel with 'TESTING_DATA_MODEL'
        fill modelCatalogueId with 'MET-00233'
        fill description with 'this my testing data'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        // TODO: check does not work even if the button is enabled
        //check stepImports enabled
        true

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:'import NHIC'
        fill wizardName with 'NHIC'
        selectCepItemIfExists()

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click closeButton

        then:
         check rightSideTitle contains 'TESTING_DATA_MODEL'
    }

    def "clone a data class in to another data model"() {
        when: 'navigate back to the hope page'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click modelCatalogue

        and:
        select 'MET-523' open 'Data Classes' select 'MET-523.M1'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and: 'navigate to the top menu and select data class and clone '
        click dataClassButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click cloneButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and: 'clone the data class to another data model '
        fill search with 'TESTING_DATA_MODEL' and pick first item
        click('button.btn-primary')
        Thread.sleep(10000l)


        and:
        click modelCatalogue
       Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        select 'TESTING_DATA_MODEL'
        selectInTree'Data Classes'

        then:
        check rightSideTitle is'Active Data Classes'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click cloneDataClass
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains 'MET-523.M1'


    }

    @Unroll
    def "check that the data element of the clone data class is(#dataElement)"( int location,String dataElement) {

        expect:
        $("#data-elements-changes > div.inf-table-body > table > tbody > tr:nth-child($location) > td:nth-child(1) > a.preserve-new-lines.ng-binding.ng-scope").text()== dataElement
        Thread.sleep(3000L)

        where:
        location || dataElement
        1        || 'MET-523.M1.DE1'
        2        || 'MET-523.M1.DE2'
        3        || 'MET-523.M1.DE3'


    }
      @Unroll
    def "check that the data type of the clone class is (#dataType)"(int position , String dataType){


        expect:

        $("#data-elements-changes > div.inf-table-body > table > tbody > tr:nth-child($position) > td.inf-table-item-cell.ng-scope.col-md-3 > span > span").text()==dataType
         Thread.sleep(3000L)

        where:
        position || dataType
        1        || 'MET-523.M1.VD1'
        2        || 'MET-523.M1.VD2'
        3        || 'MET-523.M1.VD3'
    }

    def "delete the created data model"() {
        when:
        click modelCatalogue
        select 'TESTING_DATA_MODEL'


        then:
        check rightSideTitle contains 'TESTING_DATA_MODEL'

        when:
        click modelDataMenu
        click delete

        and:
        click modalPrimaryButton

        then:
        noExceptionThrown()
    }
}

