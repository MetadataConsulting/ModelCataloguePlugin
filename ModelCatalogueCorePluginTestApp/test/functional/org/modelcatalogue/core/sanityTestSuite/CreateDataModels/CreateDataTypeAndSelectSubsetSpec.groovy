package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.getSave


class CreateDataTypeAndSelectSubsetSpec extends AbstractModelCatalogueGebSpec {
    private static final String icon="div.content-row>div>div:nth-child(2)>div:nth-child(1)>div>div:nth-child(2)>div>div:nth-child(4)>div>div>div>div:nth-child(11)>div:nth-child(1)>div:nth-child(1)>h3>a>span:nth-child(1)"
    private static final String header="h3.ce-name"
    private static final String role ="a#role_item_catalogue-element-menu-item-link>span:nth-child(1)"
    private static final String dataType = "a#catalogue-element-create-dataType-menu-item-link>span:nth-child(3)"
    private static final String name="input#name"
    private static final String catalogueId ="input#modelCatalogueId"
    private static final String description="textarea#description"
    private static final String  subset= "input#pickSubsetType"
    private static final String enumeratedTypeBase ="form.ng-dirty>div:nth-child(13)>div>span>span"
    private static final String addImport="div.search-lg>p>span>a"
    private static final String  search ="input#elements"
    private static final String OK = "div.messages-modal-prompt>div>div>div:nth-child(3)>button:nth-child(1)"
    private static final String clickX ="div.input-group-addon"




    def" create data type and select subset"(){


        when:
              // login to model catalogue
               loginCurator()
        then:
             noExceptionThrown()
        when:
             // click on the icon
             click icon
        then:
             noExceptionThrown()
        when:
             // CLICK ON DATA MODEL
               click role
        then:
              noExceptionThrown()
        when:
              // click on new data type
               click dataType
        then:
              // verify  rude is displayed
              check("div.modal-body>form>div:nth-child(4)>label")isDisplayed()
        when:
              // type name
             fill(name)with("my data type ${System.currentTimeMillis()}")
              // type catalogue id
              fill(catalogueId)with("456${System.currentTimeMillis()}")
              // type description
               fill(description)with("my description of data type${System.currentTimeMillis()}")
        then:
             noExceptionThrown()
        when:
             // click  on reference
              click subset
              // click on measurement unit
              click enumeratedTypeBase
        then:
             noExceptionThrown()
        when:
              // click on add import
               click addImport
              // type model to import in the advance search for finalize element
               fill(search)with("Cancer Model 0.0.1")
               // select the model
               fill search with("Cancer Model 0.0.1") and pick first item
              // click on ok
                click OK
               // click on X  next to search for measurement unit
                click clickX
               // click on save
                 click save
        then:
             noExceptionThrown()


    }
}
