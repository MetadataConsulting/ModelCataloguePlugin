package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getPick
@Ignore
//functionality has been removed so ignore for the moment
class CompareTwoDataModelSpec extends AbstractModelCatalogueGebSpec{
    private static final String Icon ="div.content-row>div>div:nth-child(2)>div:nth-child(1)>div>div:nth-child(2)>div>div:nth-child(4)>div>div>div>div:nth-child(9)>div:nth-child(1)>div:nth-child(1)>h3>a>span:nth-child(1)"
    private static final String dataModel="a#role_item_catalogue-element-menu-item-link>span:nth-child(1)"
    private static final String compare ="a#compare-catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String leftIcon="span.input-group-addon"
    private static final String search_box ="input#value"
    private static final String clickOK="button.btn-primary"
     private static String text ="cancer Model"

    def "compare two models"() {
        when:
             // login into model catalogue
             loginCurator()

        then:
             noExceptionThrown()
        when:
              // click on the icon next to model
              click Icon
        then:
              // verify that text is displayed
               noExceptionThrown()
        when:
             // click data model on the menu tap
               click dataModel
              // click on compare
               click compare
        then:
             noExceptionThrown()
        when:
             // click icon on the left for advanced search
             click leftIcon
        then:
              noExceptionThrown()
        when:
             // selectRelation a model
             fill search_box with text and pick first item
             // click ok
             click clickOK

        then:
            noExceptionThrown()
    }
}
