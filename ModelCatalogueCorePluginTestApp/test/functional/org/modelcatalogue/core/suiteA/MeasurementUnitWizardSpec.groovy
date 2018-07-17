package org.modelcatalogue.core.suiteA

import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.MeasurementUnitsPage
import spock.lang.Ignore
import static org.modelcatalogue.core.geb.Common.*
import spock.lang.IgnoreIf
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class MeasurementUnitWizardSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Test 2')
        dashboardPage.select('Test 2')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Measurement Units')

        then:
        at MeasurementUnitsPage

        and:
        check Common.rightSideTitle is 'Active Measurement Units'
    }

    def "create new unit"() {
        when:
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill 'name' with 'Foos'
        fill 'symbol' with 'Foo'

        click Common.save

        then:
        check { infTableCell(1, 2, text: 'Foos') } displayed
    }

    def "check the unit shows up with own detail page"() {
        remove Common.messages
        click { infTableCell(1, 2).find('a') }

        expect:
        check Common.rightSideTitle contains 'Foos'
    }

    def "going to metadata tab changes the url"() {
        check { $('li', 'data-tab-name': 'relatedTo') } displayed

        when:
        click { $('li', 'data-tab-name': 'relatedTo').find('a') }

        then:
        waitFor {
            currentUrl.toString().endsWith('/relatedTo')
        }
    }

}
