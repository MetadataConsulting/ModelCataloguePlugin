package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueAction
import org.modelcatalogue.core.gebUtils.Common
import spock.lang.IgnoreIf
import spock.lang.Stepwise



@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class ChangesSpec extends AbstractModelCatalogueGebSpec {


    public static final String FIRST_NEW_ELEMENT_CREATED_CHANGE = "a.change-NEW_ELEMENT_CREATED:first-of-type"

    def "go to login"() {
        loginAdmin()

        select 'Test 1' select 'Data Types'

        click Common.create

        fill 'name' with "Data Type Change Test"

        click Common.save

        expect:
        check 'div.modal' gone
        remove Common.messages

        when:
        go "#/catalogue/change/all"

        then:
        check 'h3' is 'Changes'
    }

    def "check the unit shows up with own detail page"(){
        when:

        click FIRST_NEW_ELEMENT_CREATED_CHANGE

        then:
        check "li[data-tab-name='changes']" displayed
        check ".pp-table-property-element-value", 'data-value-for': 'Undone' is 'false'

        when:
        click CatalogueAction.runLast('item', 'undo-change')

        then:
        check Common.modalDialog displayed

        when:
        click Common.modalPrimaryButton

        then:
        check ".pp-table-property-element-value", 'data-value-for': 'Undone' is 'true'
    }

    def "users have activity feed"() {
        go "#/catalogue/user/all"

        expect:
        check 'h3' is 'Users'
        check { infTableCell(1, 1).find('a', text: 'admin') } displayed

        when:
        click { infTableCell(1, 1).find('a', text: 'admin') }

        then:
        check 'h3' contains 'admin'

        check { tab('activity') } displayed
        check { tab('history') } displayed
    }

    def "classifications have activity feed"() {
        when:
        select 'XMLSchema'

        then:
        check { tab('activity') }
    }

}
