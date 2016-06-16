package org.modelcatalogue.core.c

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getAdmin
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getRightSideDescription

@Stepwise
class ChangeLogForEligibilitySpec extends AbstractModelCatalogueGebSpec {

    public static final String exportButton = '#role_item_export-menu-item'
    public static final String ChangeLogforRDEligibilityXSLX = '#catalogue-element-export-specific-reports_13-menu-item-link'

    def "go to login"() {
        login admin

        expect:
        waitFor(120) { browser.title == 'Data Models' }

        when:
        select 'Rare Disease Conditions', true
        selectInTree 'Data Classes'
        selectInTree 'Rare Disease Conditions and Phenotypes'

        then:
        check rightSideTitle contains 'Rare Disease Conditions and Phenotypes Rare Disease Conditions'
    }

    def "download the change log as MS Excel spreadsheet"() {

        when:
        click exportButton

        then:

        check ChangeLogforRDEligibilityXSLX displayed

        withNewWindow({ $(ChangeLogforRDEligibilityXSLX).click() }, wait: true, close: false) {
            waitFor(30) {
                $(rightSideTitle).text().startsWith('Rare Disease Conditions and Phenotypes - Eligibility change log (MS Excel Spreadsheet) Rare Disease Conditions')
            }
            waitFor(60) {
                $(rightSideDescription).text() == 'Your report is ready. Use Download button to download it.'
            }
        }
    }
}
