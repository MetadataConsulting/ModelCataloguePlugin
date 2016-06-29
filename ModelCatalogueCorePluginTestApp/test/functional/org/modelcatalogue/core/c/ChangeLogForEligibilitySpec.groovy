package org.modelcatalogue.core.c

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getAdmin
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getRightSideDescription

@Stepwise
class ChangeLogForEligibilitySpec extends AbstractModelCatalogueGebSpec {

    public static final CatalogueAction exportAction = CatalogueAction.runFirst('item', 'export')
    public static
    final CatalogueContent changeLogForRDEligibilityXSLX = CatalogueContent.create('.menu-item-link', text: 'Change Log for RD Eligibility (Excel)')

    def "go to login"() {
        login admin

        expect:
            waitFor(120) { browser.title == 'Data Models' }

        when:
            select 'Rare Disease Conditions' open 'Data Classes' select 'Rare Disease Conditions and Phenotypes'

        then:
            check rightSideTitle contains 'Rare Disease Conditions and Phenotypes Rare Disease Conditions'
    }

    def "download the change log as MS Excel spreadsheet"() {

        when:
            click exportAction
            click changeLogForRDEligibilityXSLX

            // tracking the window open does not work very well but the asset will appear in the treeview when created
            selectInTree 'Assets', true
            selectInTree 'Rare Disease Conditions and Phenotypes - Eligibility change log (MS Excel Spreadsheet)'

        then:
            check rightSideTitle contains 'Rare Disease Conditions and Phenotypes - Eligibility change log (MS Excel Spreadsheet) Rare Disease Conditions'
            check rightSideDescription is 'Your report is ready. Use Download button to download it.'
    }
}
