package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.Common
import spock.lang.Stepwise
import spock.lang.IgnoreIf

@IgnoreIf({ !System.getProperty('geb.env') })
>>>>>>> origin/Jenkins:ModelCatalogueCorePluginTestApp/test/functional/org/modelcatalogue/core/DataElementWizardSpec.groovy
@Stepwise
class DataElementWizardSpec extends AbstractModelCatalogueGebSpec {

    static final String detailSectionFormItemContent = ".metadataStep-form-item-content"

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


}
