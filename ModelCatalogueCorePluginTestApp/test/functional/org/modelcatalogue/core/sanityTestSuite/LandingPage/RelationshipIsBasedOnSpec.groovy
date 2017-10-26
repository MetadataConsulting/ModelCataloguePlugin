package org.modelcatalogue.core.sanityTestSuite.LandingPage


import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise
import static org.modelcatalogue.core.gebUtils.Common.item
import static org.modelcatalogue.core.gebUtils.Common.getRightSideTitle
import static org.modelcatalogue.core.gebUtils.Common.modalPrimaryButton
import static org.modelcatalogue.core.gebUtils.Common.pick


@Stepwise
class RelationshipIsBasedOnSpec extends AbstractModelCatalogueGebSpec{

    private static final String dataModel ="a#role_item_catalogue-element-menu-item-link"
    private static final String createRelationship ="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String  destination ="h3.panel-title"
    private static final String  destinationIcon="span.input-group-addon"
    private static final String   isBasedOn ="#type > option:nth-child(2)"
    private static final String   cancel = "div.messages-modal-confirm>div>div>div:nth-child(3)>form>button:nth-child(3)"
    private static final String  search ="input#element"
    private static final String  alert ="div.alert"
    private static final String  primaryButton ="button.btn-primary"



    def"login to model catalogue and select data model"(){

        when:
        loginAdmin()
        select 'SACT'
        then:
        check rightSideTitle contains 'SACT'
    }

    def"navigate to the top menu and select create relationship "(){

        when: 'navigate to the top menu and click on the data model button'
        click dataModel

        and: 'select create relationship'
        click createRelationship

        then:
        check destination displayed

    }

    def"select based on,destination and create relationship"(){

        when: 'select relation'
        click isBasedOn

        and: ' select destination'

        click destinationIcon
        Thread.sleep(2000L)
        fill search with "Clinical Tags" and pick first item
        //selectInSearch(2)

        and: 'click on the cancel button'
        click cancel
        Thread.sleep(2000l)

        and: 'click on the create relationship'
        click 'button.btn-primary'

        then: 'verify that alert is displayed'
        check alert displayed


    }
}
