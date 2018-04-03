package org.modelcatalogue.core.suiteA

import org.modelcatalogue.core.geb.ChangesPage
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Stepwise
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
@Stepwise
class ChangesSpec extends AbstractModelCatalogueGebSpec {

    public static final String FIRST_NEW_ELEMENT_CREATED_CHANGE = "a.change-NEW_ELEMENT_CREATED:first-of-type"

    def "go to login"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select("Test 1")

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage

        when:
        click Common.create

        fill 'name' with "Data Type Change Test"

        click Common.save

        then:
        check 'div.modal' gone
        remove Common.messages

        when:
        to ChangesPage

        then:
        at ChangesPage
    }

    def "check the unit shows up with own detail page"() {
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
        check { infTableCell(1, 1).find('a', text: 'supervisor') } displayed

        when:
        click { infTableCell(1, 1).find('a', text: 'supervisor') }

        then:
        check 'h3' contains 'supervisor'

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
