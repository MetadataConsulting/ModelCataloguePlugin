package org.modelcatalogue.core

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.IgnoreIf

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
class ChangeLogForEligibilitySpec extends AbstractModelCatalogueGebSpec {

    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')
    public static
    final CatalogueContent changeLogForRDEligibilityXSLX =
        CatalogueContent.create('.menu-item-link', text: 'Change Log for RD Eligibility (Excel)')

    @Ignore
    def "go to login"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DataModelListPage

        when:
        DataModelListPage dataModelListPage = browser.page DataModelListPage
        dataModelListPage.dashboard()

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select("Rare Disease Conditions")

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.titleContains 'Rare Disease Conditions'
    }

    @Ignore
    def "download the change log as MS Excel spreadsheet"() {

        when:
            click exportAction
            click changeLogForRDEligibilityXSLX

            // tracking the window open does not work very well but the asset will appear in the treeview when created
            selectInTree 'Assets', true
            selectInTree 'Rare Disease Conditions and Phenotypes - Eligibility change log (MS Excel Spreadsheet)'

        then:
            check Common.rightSideTitle contains 'Rare Disease Conditions and Phenotypes - Eligibility change log (MS Excel Spreadsheet)'
            check Common.rightSideDescription is 'Your report is ready. Use Download button to download it.'
    }
}
