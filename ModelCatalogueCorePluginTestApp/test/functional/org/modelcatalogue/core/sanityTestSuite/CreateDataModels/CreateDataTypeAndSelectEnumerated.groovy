package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModalCloseButton
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.save

@Stepwise
class CreateDataTypeAndSelectEnumerated extends AbstractModelCatalogueGebSpec{
    private static final String enumerated ="input#pickEnumeratedType"
    private static final String  paste="button.btn-default"

    def"login and navigate to the model"(){
        when:
              loginCurator()
              select 'Test 6'
             selectInTree 'Data Types'

        then:
             check rightSideTitle contains 'Active Data Types'
    }

    def"navigate to data type creation page "(){
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

        and:'click on enumerated type'
            click enumerated
            fillMetadata Mycopy:'this is my description'
        and:
             click paste

       then:
            check "button.btn-primary" displayed

        when:
             click save

        then:
           noExceptionThrown()
    }
}
