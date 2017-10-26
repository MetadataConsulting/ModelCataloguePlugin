package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueAction
import org.modelcatalogue.core.gebUtils.CatalogueContent
import org.modelcatalogue.core.gebUtils.Common
import spock.lang.Ignore
import spock.lang.Stepwise

@Stepwise
@Ignore
class ChangeLogForEligibilitySpec extends AbstractModelCatalogueGebSpec {

    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')
    public static
    final CatalogueContent changeLogForRDEligibilityXSLX =
        CatalogueContent.create('.menu-item-link', text: 'Change Log for RD Eligibility (Excel)')

    def "go to login"() {
        login Common.admin

        expect:
            waitFor(120) { browser.title == 'Data Models' }

        when:
            select 'Rare Disease Conditions'

        then:
            check Common.rightSideTitle contains 'Rare Disease Conditions'
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
