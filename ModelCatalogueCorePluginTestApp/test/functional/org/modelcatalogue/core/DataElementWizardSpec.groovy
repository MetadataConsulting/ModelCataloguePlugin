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
        login Common.admin
        select 'Test 1' select 'Data Elements'

        expect:
        check Common.rightSideTitle is 'Active Data Elements'
    }

    def "Add new data element"() {
        when: 'I click the add model button'
        click Common.create

        then: 'the data element dialog opens'
        check Common.wizard displayed

        when:
        fill Common.name with "NewDE1"
        fill Common.description with "NT1 Description"

        and: 'save button clicked'
        click Common.save

        then: 'the data element is saved and displayed at the top of the table'
        check Common.nameInTheFirstRow, text: "NewDE1" displayed
    }

    def "Check the data element shows up with own details"() {
        expect:
        check Common.backdrop gone

        when: 'Data Element is located'
        check Common.nameInTheFirstRow, text: "NewDE1" displayed

        then: 'Click the element'
        click Common.firstRowLink
        check Common.rightSideTitle contains 'NewDE1 Test 1'
    }

    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check detailSectionFormItem present Common.once
        check detailSectionFormItemContent gone

        when: "Click the title"
        click detailSectionFormItem.find(".title")

        then: "Content is displayed"
        check detailSectionFormItemContent displayed
    }
}
