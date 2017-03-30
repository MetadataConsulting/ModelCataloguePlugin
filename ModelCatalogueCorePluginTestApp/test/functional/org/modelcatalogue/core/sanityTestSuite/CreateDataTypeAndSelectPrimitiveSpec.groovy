package org.modelcatalogue.core.sanityTestSuite

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
class CreateDataTypeAndSelectPrimitiveSpec extends AbstractModelCatalogueGebSpec{

    private static final String primitive= "input#pickPrimitiveType"
    private static final String measurementUnit="form.ng-dirty>div:nth-child(12)>div>span>span"
    private static final String addImport="div.search-lg>p>span>a"
    private static final String  search ="input#elements"
    private static final String OK = "div.messages-modal-prompt>div>div>div:nth-child(3)>button:nth-child(1)"
    private static final String clickX ="div.input-group-addon"

    def"login to Model Catalogue and select model"() {
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

        and:'select primitive button'
             click primitive
             click measurementUnit

        and: ' import data '
               click addImport
               fill search with("test 0.0.1") and pick first item
               click OK
               click clickX
               click save
        then:
             noExceptionThrown()




    }
}
