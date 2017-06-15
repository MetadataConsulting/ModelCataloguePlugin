package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

import spock.lang.See
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
@See("https://metadata.atlassian.net/browse/MET-1398")
class TwoModelsCanHaveTheSameMeasurementUnitSpec extends  AbstractModelCatalogueGebSpec{


    private static final String createdModel ='a#role_data-models_create-data-modelBtn'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 2000
    static final String stepImports = "#step-imports"
    static final String wizardName = 'div.create-classification-wizard #name'
    private static final String  finishButton='button#step-finish'
    private static final String symbol ='input#symbol'
    private static final String firstRow ='td.col-md-2'
    private static final String dataModelMenu='a#role_item_catalogue-element-menu-item-link>span:nth-child(3)'
    private static final String  delete ='a#delete-menu-item-link>span:nth-child(3)'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'
    private static final String  measurementUnits='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(4)>div>span>span'
    private static final String  modelCatalogueLink='span.mc-name'




    def"login to model catalogue and create a data model"(){

        when:
        loginAdmin()

        and:' FILL THE FORM'

        click createdModel

        fill nameLabel with 'DATA_MODEL_1'

        fill modelCatalogueId with 'MET-6783'

        fill description with 'this is my testing data'

        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check stepImports enabled

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
        check rightSideTitle contains 'DATA_MODEL_1'

    }

    def" create a measurement units "(){

        when:
        click measurementUnits

        then:
        check rightSideTitle is 'Active Measurement Units'

        when:
        click create

        then:
        check modalHeader is 'Create Measurement Unit'


        when:
        fill nameLabel with 'kilogram'
        fill symbol with 'KG'
        fill modelCatalogueId with 'MET-78'

        and:
        click save

        then:
        check firstRow is'KG'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
    }

    def " navigate to home page and create a second model"(){

        when:
        click  modelCatalogueLink
        click createdModel

        and:
        fill nameLabel with 'DATA_MODEL_2'
        fill modelCatalogueId with 'MET-7777'
        fill description with 'this my testing data'
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        and:
        click finishButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
        click closeButton

        then:
        check rightSideTitle contains 'DATA_MODEL_2'

    }

    def"create the same measurement unit and verify no error is displayed"(){


        when:
        click measurementUnits

        then:
        check rightSideTitle is 'Active Measurement Units'

        when:
        click create

        then:
        check modalHeader is 'Create Measurement Unit'


        when:
        fill nameLabel with 'kilogram'
        fill symbol with 'KG'
        fill modelCatalogueId with 'MET-78'

        and:
        click save

        then:
        check firstRow is'KG'

    }

    def"delete the created data models"(){

        when:
        click modelCatalogueLink
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        select'DATA_MODEL_1'

        then:
        check rightSideTitle contains 'DATA_MODEL_1 MET-6783@0.0.1'

        when:
        click dataModelMenu
        click delete

         then:
         check modalHeader is 'Do you really want to delete Data Model DATA_MODEL_1?'

        when:
        click modalPrimaryButton


        then:
        noExceptionThrown()
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)


        when:
        select'DATA_MODEL_2'

        then:
        check rightSideTitle contains 'DATA_MODEL_2 MET-7777@0.0.1'

        when:
        click dataModelMenu
        click delete

        then:
        check modalHeader is 'Do you really want to delete Data Model DATA_MODEL_2?'

        when:
        click modalPrimaryButton


        then:
        noExceptionThrown()

    }



}
