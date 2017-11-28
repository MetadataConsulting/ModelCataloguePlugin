package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import geb.module.Select
import groovy.transform.NotYetImplemented
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
@Stepwise
class CreateRelationshipSpec extends AbstractModelCatalogueGebSpec {

    private static final String dataModel="a#role_item_catalogue-element-menu-item-link>span:nth-child(3"
    private static final String relationship="a#create-new-relationship-menu-item-link>span:nth-child(3)"
    private static final String createRelationship="button.btn-primary"
    private static final String selectRelation ="select#type"
    private static final String search="input#element"
    private static final String undoButton="a#role_item_undo-changeBtn"
    private static final String  table="tbody.ng-scope>tr:nth-child(1)>td:nth-child(4)"
    private static String text="NHIC"
    private static final String cancel="div.messages-modal-confirm>div>div>div:nth-child(3)>form>button:nth-child(3)"
    private static final String plusButton="tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>a>span"

    def "login to model catalogue and navigate to data model"() {
        when:
        loginAdmin()
        select 'Test 1'
        then:
        check rightSideTitle contains 'Test 1'
    }

    def "Navigate to create relationship page"() {
        when:
        click dataModel

        and: 'click on the create relationship'
        click relationship

        then:
        check createRelationship displayed
    }

    def "create relationship"() {
        when:'select relation'

        def select = $(selectRelation).module(Select)
        select.selected = "is based on"

        then:
        select.selectedText == "is based on"

        and:
        Thread.sleep(2000L)

        when:
        fill search with text and pick first item
        Thread.sleep(2000l)

        and:
        click cancel

        and: 'click on the create relationship button'
        click createRelationship

        then:
        noExceptionThrown()
        //check table contains 'is based on'
    }
     @Ignore
    def" remove the created relationship "(){

        when:
        click plusButton
        and:
        Thread.sleep(2000L)
        click undoButton

        and:
        click modalPrimaryButton

        then:
        noExceptionThrown()
    }
}
