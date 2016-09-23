package org.modelcatalogue.core

import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class DataElementWizardSpec extends AbstractModelCatalogueGebSpec {

    static final CatalogueContent detailSectionFormItem = CatalogueContent.create('data-view-name': 'Form (Item)')
    static final String detailSectionFormItemContent = ".metadata-form-item-content"

    def "login and select Data Element"() {
        login admin
        select 'Test 1' select 'Data Elements'

        expect:
        check rightSideTitle is 'Active Data Elements'
    }

    def "Add new data element"() {
        when: 'I click the add model button'
        click create

        then: 'the data element dialog opens'
        check wizard displayed

        when:
        fill nameLabel with "NewDE1"
        fill description with "NT1 Description"

        and: 'save button clicked'
        click save

        then: 'the data element is saved and displayed at the top of the table'
        check { infTableCell(1, 1) } contains "NewDE1"
    }

    def "Check the data element shows up with own details"() {
        expect:
        check backdrop gone

        when: 'Data Element is located'
        check { infTableCell(1, 1) } contains "NewDE1"

        then: 'Click the element'
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }
        check rightSideTitle contains 'NewDE1'
    }

    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check detailSectionFormItem present once
        check detailSectionFormItemContent gone

        when: "Click the title"
        click detailSectionFormItem.find(".title")

        then: "Content is displayed"
        check detailSectionFormItemContent displayed
    }
}
