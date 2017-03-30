package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.save


class CreateNewDataElementSpec extends AbstractModelCatalogueGebSpec {
    private static final String OK ="button.btn-primary"
    private static final String search="input#elements"
    private static final String  dataType ="span.input-group-addon"
    private static final String  addImport="div.search-lg>p>span>a:nth-child(3)"
   // private static final String  create ="span.text-success"
    private static final String  button ="div.input-group-addon"
    static String myName=" testind data element ${System.currentTimeMillis()}"
    static String myCatalogue="324${System.currentTimeMillis()}"
    static String myDescription=" hello there ${System.currentTimeMillis()}"

    def"login and navigate to Data Model"() {
        when:
             loginAdmin()
             select 'Test 6'
             selectInTree 'Data Elements'
        then:
            check rightSideTitle is 'Active Data Elements'
    }
    def" navigate to data element creation page"() {
        when:
             click create
        then:
            check modalHeader contains 'Create Data Element'
    }
    def " fill the create data element form"(){
        when:
            fill nameLabel with myName

             fill modelCatalogueId with myCatalogue

             fill description with myDescription
        and:
            click dataType
            click addImport
            fill search with 'MET-523'
            remove messages
        and:
            click OK
            click button
            click save
        then:
            noExceptionThrown()



    }
}
