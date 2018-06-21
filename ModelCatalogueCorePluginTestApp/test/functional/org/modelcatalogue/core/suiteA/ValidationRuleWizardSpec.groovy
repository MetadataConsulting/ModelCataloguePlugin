package org.modelcatalogue.core.suiteA

import org.modelcatalogue.core.geb.BusinessRulesPage
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise


@Stepwise
class ValidationRuleWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Test 3')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Business Rules')

        then:
        at BusinessRulesPage

        and:
        check Common.rightSideTitle is 'Active Business Rules'
    }

    def "create new validation rule"() {
        when:
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill 'name' with 'Test Validation Rule'

        click Common.save

        then:
        check { infTableCell(1, 1) } contains 'Test Validation Rule'
    }

    def "check the unit shows up with own detail page"() {
        check Common.closeGrowlMessage gone
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        expect:
        check Common.rightSideTitle contains 'Test Validation Rule'
    }

}
