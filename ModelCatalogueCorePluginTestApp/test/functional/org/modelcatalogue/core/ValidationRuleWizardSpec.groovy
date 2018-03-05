package org.modelcatalogue.core

import org.modelcatalogue.core.geb.BusinessRulesPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
@Ignore
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
        check rightSideTitle is 'Active Business Rules'
    }

    def "create new validation rule"() {
        when:
        click create

        then:
        check modalDialog displayed

        when:
        fill 'name' with 'Test Validation Rule'

        click save

        then:
        check { infTableCell(1, 1) } contains 'Test Validation Rule'
    }

    def "check the unit shows up with own detail page"() {
        check closeGrowlMessage gone
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        expect:
        check rightSideTitle contains 'Test Validation Rule'
    }

}
