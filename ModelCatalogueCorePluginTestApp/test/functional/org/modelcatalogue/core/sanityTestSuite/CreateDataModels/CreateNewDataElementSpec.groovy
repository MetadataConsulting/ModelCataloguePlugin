package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec


class CreateNewDataElementSpec extends AbstractModelCatalogueGebSpec {
   private static final String create ="a#role_data-models_create-data-modelBtn"
    private static final String search_box="input#data-model-search-box"
    private static final String MODEL="span.text-muted"
    private static final String dataModel="a#role_item_catalogue-element-menu-item-link>span:nth-child(1)"
    private static final String dataElement="a#catalogue-element-create-dataElement-menu-item-link>span:nth-child(3)"
    private static final String createDataElement="div.modal-header>h4"
    private static final String  name="input#name"
    private static final String  catelogueId="input#modelCatalogueId"
    private static final String  description="textarea#description"
    private static final String  save="a#role_modal_modal-save-elementBtn>span:nth-child(2)"
    private static final String  button ="div.content-row>div>div:nth-child(2)>div:nth-child(1)>div>div:nth-child(2)>div>div:nth-child(4)>div>div>div>div:nth-child(11)>div:nth-child(1)>div:nth-child(1)>h3>a>span:nth-child(1)"
    static String myName=" testind data element ${System.currentTimeMillis()}"
    static String myCatalogue="324${System.currentTimeMillis()}"
    static String myDescription=" hello there ${System.currentTimeMillis()}"

    void createDataElement(){

        when:
             // login as curator
              loginCurator()
        then:
             // verify create button present
              $(create).text()=="Create"

        when:
             // search for your model
             click button
        then:
              noExceptionThrown()

        when:
              // click on data model
              click dataModel
             // click on data element
             click dataElement
        then:
             noExceptionThrown()
        when:
            // type name
            fill(name)with(myName)
            // type catalogue id
             fill(catelogueId)with(myCatalogue)
             // type description
             fill(description)with(myDescription)
             // click on save
             click save
        then:
            noExceptionThrown()



    }
}
