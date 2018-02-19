package org.modelcatalogue.core.regression.dataelement.admin

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.Common
import spock.lang.Stepwise
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class MET1447_DataElementWizardSpec extends AbstractModelCatalogueGebSpec {

    static final String detailSectionFormItemContent = ".metadata-form-item-content"

    def "login and select Data Element"() {
        login Common.admin
        select 'Test 1' open 'Data Elements' select 'No tags'

        expect:
        check Common.rightSideTitle is 'Active Data Elements'
    }

    def "Add new data element"() {
        when: 'I click the add model button'
        click Common.create

        then: 'the data element dialog opens'
        check Common.wizard displayed

        when:
        fill Common.nameLabel with "NewDE1"
        fill Common.description with "NT1 Description"

        and: 'save button clicked'
        click Common.save

        then: 'the data element is saved and displayed at the top of the table'
        check { infTableCell(1, 1) } contains "NewDE1"
    }

    def "Check the data element shows up with own details"() {
        expect:
        check Common.backdrop gone

        when: 'Data Element is located'
        check { infTableCell(1, 1) } contains "NewDE1"

        then: 'Click the element'
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }
        check Common.rightSideTitle contains 'NewDE1'
    }

    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check Common.detailSectionFormMetadata present Common.once
        check detailSectionFormItemContent gone

        when: "Click the title"
        click Common.detailSectionFormMetadata.find('.title .btn')

        then: "Content is displayed"
        check detailSectionFormItemContent displayed
    }

    /*
    * NEED TO ADD CLONING FROM MET1447
    *
    * */
}
