package org.modelcatalogue.core

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
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
