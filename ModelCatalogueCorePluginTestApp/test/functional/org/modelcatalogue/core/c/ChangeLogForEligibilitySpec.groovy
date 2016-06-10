package org.modelcatalogue.core.c

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.getAdmin
import static org.modelcatalogue.core.geb.Common.getModalDialog
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getRightSideTitle

@Stepwise
class ChangeLogForEligibilitySpec extends AbstractModelCatalogueGebSpec {

    public static final String exportButton = '#role_item_export-menu-item'
    public static final String ChangeLogforRDEligibilityXSLX = '#catalogue-element-export-specific-reports_13-menu-item-link'

    def "go to login"() {
        login admin

        expect:
        waitFor(120) { browser.title == 'Data Models' }

        when:
//        select('Rare Disease Conditions') / 'Rare Disease Conditions' / 'Data Classes'
        selectLatestVersion '22535','Rare Disease Conditions' //Rare Disease Conditions
        descendTree 'Data Classes','Rare Disease Conditions and Phenotypes'

        then:

//        check rightSideTitle contains 'Rare Disease Conditions and Phenotypes Rare Disease Conditions 0.0.1'
        check rightSideTitle contains 'Rare Disease Conditions and Phenotypes Rare Disease Conditions'
    }

    def "download the changeLog Ms excel document"() {

        when:
        click exportButton

        then:

        check ChangeLogforRDEligibilityXSLX displayed

        withNewWindow({ $(ChangeLogforRDEligibilityXSLX).click() }, wait: true, close: false) {
            waitFor(30) {
                $(rightSideTitle).text() == 'Rare Disease Conditions and Phenotypes - Eligibility change log (MS Excel Spreadsheet) Rare Disease Conditions 0.0.2'
            }
            waitFor(60) {
                $(rightSideTitle).text() == 'Your report is ready. Use Download button to download it.'
            }


        }
    }
}
