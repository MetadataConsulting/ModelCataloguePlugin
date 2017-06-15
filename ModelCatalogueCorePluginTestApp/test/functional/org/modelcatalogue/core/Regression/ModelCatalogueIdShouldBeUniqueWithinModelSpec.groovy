package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.See
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.pick
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise @See("https://metadata.atlassian.net/browse/MET-1412")
class ModelCatalogueIdShouldBeUniqueWithinModelSpec extends AbstractModelCatalogueGebSpec {

    private static final String createdModel ='a#role_data-models_create-data-modelBtn'
    private static final int TIME_TO_REFRESH_SEARCH_RESULTS = 2000
    static final String stepImports = "#step-imports"
    static final String wizardName = 'div.create-classification-wizard #name'
    private static final String  finishButton='button#step-finish'
    private static final String  closeButton='div.modal-footer>button:nth-child(2)'
    private static final String  summary='div#summary>h4'
    private static final String  dataClasses='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(1)>div>span>span'
    private static final String   dataElements='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(2)>div>span:nth-child(2)>span'
    private static final String  stepFinish='button#step-finish'
    private static final String  exitWizard='button#exit-wizard'
    private static final String   search='input#dataTypes'
    private static final String cloneButton ='form.ng-pristine>button:nth-child(1)'
    private static final String   cloneDataType ='tr.inf-table-item-row>td:nth-child(1)>span>span>a'
    private static final String dataTypes ='ul.catalogue-element-treeview-list-root>li>ul>li:nth-child(3)>div>span:nth-child(2)>span'






    def"login to model catalogue and create a data model"(){

        when:
        loginAdmin()

        and:' FILL THE FORM'

        click createdModel

        fill nameLabel with 'DATA_MODEL'

        fill modelCatalogueId with 'MET-02'

        fill description with 'TESTING DESCRIPTION'

        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check stepImports enabled

        when:
        click stepImports

        then:
        check stepImports has 'btn-primary'

        when:'import SI'
        fill wizardName with 'SI'
        selectCepItemIfExists()

        and:
        click finishButton

        then:
        check summary is 'Data Model DATA_MODEL created'

        when:
        click closeButton
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check rightSideTitle contains 'DATA_MODEL MET-02@0.0.1'

         and:
         Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)
         check 'small.ce-description>div>div' contains 'TESTING DESCRIPTION'

    }

    def"create two data classes with the same catalogue id"(){

        when:
        click dataClasses

        then:
        check rightSideTitle is 'Active Data Classes'


        when:
        click create

        then:
        check modalHeader is 'Data Class Wizard'


        when:
        fill nameLabel with'DATA_CLASS_1'
        fill modelCatalogueId with 'MET-11'
        fill description with 'TESTING CATALOGUE ID'

        and:
        click stepFinish
        Thread.sleep(TIME_TO_REFRESH_SEARCH_RESULTS)

        then:
        check 'div.alert' contains 'Data Class DATA_CLASS_1 created'

        when:
        click exitWizard

        then:
        check rightSideTitle is 'Draft Data Classes'

        and:
        check 'td.col-md-4' is 'DATA_CLASS_1 MET-11'


        when:
        click create

        then:
        check modalHeader  contains 'Data Class Wizard'

        when:
        fill nameLabel with 'DATA_CLASS_2'
        fill modelCatalogueId with 'MET-11'
        fill description with 'TESTING DESCRIPTION'

        and:
        click stepFinish

        then:
        check 'div.alert' isDisplayed()

        and:
        check 'div.alert' contains 'Property modelCatalogueId must be unique for every DATA_CLASS_2 [null@0.0.1] (DRAFT:DataClass:null) within DATA_MODEL'

    }

    def"create two data elements with the same catalogue id"(){

        when:
        click dataElements
        then:
        check rightSideTitle contains 'Active Data Elements'

        when:
        click create

        then:
        check modalHeader contains 'Create Data Element'

        when:
        fill nameLabel with 'TEST_DATA_ELEMENT_1'
        fill modelCatalogueId  with 'MET-12'
        fill  description with 'TESTING CATALOGUE ID'
        fill search with 'Boolean' and pick first item

        then:
        check modalHeader is 'Import or Clone'

        when:
        click cloneButton
        click save

        then:
        check 'td.col-md-4' contains 'TEST_DATA_ELEMENT_1'

        when:
        click dataTypes

        then:
        check cloneDataType is 'Boolean'



    }



}
