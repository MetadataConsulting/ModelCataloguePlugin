package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.module.Select
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec

import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*


@Stepwise
class CreateRelationshipSpec extends AbstractModelCatalogueGebSpec {

    private static final String dataModel="a#role_item_catalogue-element-menu-item-link>span:nth-child(3"
    private static final String relationship="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String createRelationship="button.btn-primary"
    private static final String selectRelation ="select#type"
    private static final String   icon="span.input-group-addon"
    private static final String search="input#value"
    private static String text="cancer Model"
    private static final String clone="div.messages-modal-confirm>div>div>div:nth-child(3)>form>button:nth-child(1)"


    def "login to model catalogue and navigate to data model"(){
        when:
             loginCurator()
            select'Test 6'
        then:
            noExceptionThrown()
    }
    def "Navigate to create relationship page"(){
        when:
              click dataModel
             click relationship

        then:
             check createRelationship displayed
    }
    def"create relationship"(){
     when:

                 // select relation
     def select = $(selectRelation).module(Select)
      select.selected="is based on"

        then:
        select.selectedText == "is based on"
        noExceptionThrown()

       when:
                click icon
                fill search with text and pick first item
                click clone
        then:
            check createRelationship displayed

        when:
             click createRelationship
        then:
             noExceptionThrown()


    }
}
