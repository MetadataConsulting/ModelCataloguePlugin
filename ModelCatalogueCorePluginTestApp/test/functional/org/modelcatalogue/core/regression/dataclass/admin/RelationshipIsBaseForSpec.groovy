package org.modelcatalogue.core.regression.dataclass.admin

import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.item
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.pick
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
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


    /*
    *
    *
    * NEEDS TO BE REWRITTEN - SHOULD BE BASED ON FOR CLASSES NOT MODELS
    *
    *
    *
    *
    * */

    def "login to model catalogue and select a data model"() {

        when:
        loginAdmin()
        select 'NHIC'
        then:'verify  title of the page '
        check Common.rightSideTitle contains 'NHIC'
        and:
        Thread.sleep(2000l)
    }

    def "Navigate to the top menu and select create relationship"() {

        when:''
        click dataModel
        click createRelationship

        then:'verify that destination is displayed'
        check destination displayed
    }

    def "choose relation and create relationship"() {

        when: 'select relation'
        click isBaseFor
        and: ' select destination'
        click destinationIcon
        fill search with "Cancer Model" and Common.pick first Common.item
        Thread.sleep(2000l)
        click cancel
        Thread.sleep(2000L)
        click Common.modalPrimaryButton

        then:'check that alert message is displayed'
        Thread.sleep(2000l)
        check alert displayed
    }
}
