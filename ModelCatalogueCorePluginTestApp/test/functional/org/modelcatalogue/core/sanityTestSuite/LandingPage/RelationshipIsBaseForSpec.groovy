package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton


import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.pick


@Stepwise
class RelationshipIsBaseForSpec extends AbstractModelCatalogueGebSpec {
    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  destinationIcon="span.input-group-addon"
    private static final String   isBaseFor ="#type > option:nth-child(3)"
    private static final String   cancel = "div.messages-modal-confirm>div>div>div:nth-child(3)>form>button:nth-child(3)"
    private static final String  search ="input#value"
    private static final String  alert ="div.alert"

    def "login to model catalogue and select a data model"() {

        when:
        loginAdmin()
        select 'TEST 7'
        then:'verify  title of the page '
        check rightSideTitle contains 'TEST 7'

    }
    def"Navigate to the top menu and select create relationship"(){

        when:''
        click dataModel
        click createRelationship

        then:'verify that destination is displayed'
        check destination displayed
    }
    def "choose relation and create relationship"(){

        when: 'select relation'
        click isBaseFor
        and: ' select destination'
        click destinationIcon
        fill search with "Test 1" and pick first item
        click cancel
        click modalPrimaryButton


        then:'check that alert message is displayed'
        check alert displayed


    }
}
