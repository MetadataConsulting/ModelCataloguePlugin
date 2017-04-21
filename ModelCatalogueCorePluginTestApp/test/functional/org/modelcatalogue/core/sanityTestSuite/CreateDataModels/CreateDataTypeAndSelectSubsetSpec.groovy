package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getMessages
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave

@Stepwise
class CreateDataTypeAndSelectSubsetSpec extends AbstractModelCatalogueGebSpec {

    private static final String  subset= "input#pickSubsetType"
    private static final String enumeratedTypeBase ="form.ng-dirty>div:nth-child(13)>div>span>span"
    private static final String addImport="div.search-lg>p>span>a"
    private static final String  search ="input#elements"
    private static final String clickX ="div.input-group-addon"
    private static final String OK = "div.messages-modal-prompt>div>div>div:nth-child(3)>button:nth-child(1)"




    def" login and navigate to Data model"(){

        when:
             loginCurator()
             select 'Test 6'
             selectInTree 'Data Types'

        then:
             check rightSideTitle contains 'Active Data Types'
    }
    def"Navigate to data type page"() {
        when:
            click create
        then:
            check modalHeader contains 'Create Data Type'
    }
    def " fill the create data type form"(){
        when:
             fill nameLabel with "my data type ${System.currentTimeMillis()}"

             fill modelCatalogueId with "${UUID.randomUUID().toString()}"

              fill description with "my description of data type${System.currentTimeMillis()}"

        and:'select references button and save'
            click subset
            click enumeratedTypeBase

        and:'import a data'
             click addImport
             fill search with "Cancer Model 0.0.1"
             remove messages

              click OK

               click clickX

               click save
        then:
            noExceptionThrown()


    }
}
