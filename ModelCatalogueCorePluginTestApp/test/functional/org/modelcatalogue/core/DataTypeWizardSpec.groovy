package org.modelcatalogue.core

import static org.modelcatalogue.core.geb.Common.*
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.core.geb.Common
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise
import org.openqa.selenium.Keys
import spock.lang.IgnoreIf

@Stepwise
@IgnoreIf({ !System.getProperty('geb.env') })
class DataTypeWizardSpec extends AbstractModelCatalogueGebSpec {

    public static final String expandTableHeader = '.inf-table thead .inf-cell-expand'
    public static final CatalogueContent nameFilter = CatalogueContent.create('input.form-control', placeholder: 'Filter Name')
    public static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    public static final CatalogueContent removeEnumerationOne = CatalogueContent.create('.soe-remove-row', 'data-for-property': '01')
    public static final CatalogueContent enumerationsDetail = CatalogueContent.create('data-view-name':"Enumerations")
    public static final String tableRows = '.inf-table tbody .inf-table-item-row'
    public static final String pickReferenceType = '#pickReferenceType'
    public static final String pickPrimitiveType = '#pickPrimitiveType'
    public static final String pickEnumeratedType = '#pickEnumeratedType'
    public static final String pickSubset = '#pickSubsetType'
    public static final String baseEnumeration = '#baseEnumeration'
    public static final String updateMetadataButton = '.tab-pane button.btn-primary.update-object'
    public static final String addMetadataButton = '.tab-pane .btn.add-metadata'
    public static final String removeMetadataRow = '[data-view-content-name="Custom Metadata"] a.soe-remove-row'
    public static final CatalogueAction createMapping = CatalogueAction.runFirst('item', 'catalogue-element', 'create-new-mapping')
    public static final CatalogueAction createRelationship = CatalogueAction.runLast('item', 'catalogue-element', 'create-new-relationship')
    public static final CatalogueAction convert = CatalogueAction.runLast('item', 'catalogue-element', 'convert')
    public static final CatalogueAction removeRelationship = CatalogueAction.runLast('item', 'remove-relationship')
    public static final CatalogueAction removeMapping = CatalogueAction.runLast('item', 'remove-mapping')
    public static final String dataType = 'dataType'
    public static final String mapping = 'mapping'
    public static final String modalPrimaryButton = 'div.modal button.btn-primary, div.modal a.btn-primary'
    public static final String value = 'value'
    public static final GString modalResponse = "$modalDialog pre"
    public static final CatalogueAction editMapping = CatalogueAction.runLast('item', 'edit-mapping')
    public static final String expandMetadata = '.expand-metadata'
    public static final String metadataHelpBlock = '.metadata-help-block'
    static final CatalogueContent detailSectionFormItem = CatalogueContent.create('data-view-name': 'Form (Item)')
    static final String detailSectionFormItemContent = ".metadata-form-item-content"
    static final String detailSectionCustomMetadataContent = '[data-view-content-name="Custom Metadata"]'
    static final CatalogueAction changeType = CatalogueAction.runFirst('item-detail', 'change-type')
    public static final String primitiveTypeIcon = 'h3.ce-name small.fa-cog'
    public static final String dataTypeSelector = '#option'
    public static final String primitiveTypeValue = 'string:primitiveType'
    public static final String metadataTable = 'table.soe-table'
    public static final String removeRelationshipButton = '#role_item_remove-relationshipBtn'
    public static final String primitiveContent = '[data-view-name="Measurement Unit"]'

    def "go to login"() {
        login admin

        when:
        select 'NHIC' select 'Data Types'

        then:
        check rightSideTitle is 'Active Data Types'
    }

    def "create reference"() {
        select 'Test 1'

        addDataModelImport 'XMLSchema', 'NHIC'

        selectInTree 'Data Types'

        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'New Reference Type'


        click pickReferenceType

        fill 'dataClass' with 'DEMOGRAPHICS' and prefer first existing item
        fill description with "Test Reference Unit"

        click save

        then:
        check modalDialog gone
        check { infTableCell(1, 1) } contains "New Reference Type"
        check { infTableCell(1, 2, text: 'DEMOGRAPHICS') } displayed
    }

    def "create primitive"() {
        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'New Primitive Type'
        fill description with "Test Primitive Unit"

        click pickPrimitiveType

        check primitiveContent displayed
        fill 'measurementUnit' with 'new unit'
        check "$primitiveContent .dropdown-menu" displayed
        pressKey("$primitiveContent input", Keys.ESCAPE)

        click save

        then:
        check modalDialog gone
        check { infTableCell(1, 1) } contains "New Primitive Type"
        check { infTableCell(1, 2, text: 'new unit') } displayed
    }

    def "create enum"() {
        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Enumeration 1'

        click pickEnumeratedType


        check enumerationsTableEditor displayed

        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 1"
    }

    def "create subset 2"() {
        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Enumeration 2'

        click pickSubset

        check baseEnumeration displayed

        fill baseEnumeration with 'Enumeration 1' and pick first item

        click '#subtype-enum-1'
        click '#subtype-enum-2'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 2"
    }

    def "create subset 3"() {
        when:
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'Enumeration 3'

        click pickSubset

        check baseEnumeration displayed

        fill baseEnumeration with 'Enumeration 2' and pick first item

        click '#subtype-enum-1'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 3"
        check { infTableCell(1, 2) } contains '01: one'
    }

    @Ignore
    def "updating parent propagates to child and grandchild"() {
        when:
        refresh browser
        Thread.sleep(3000)
        select 'Test 1' open 'Data Types' select 'Enumeration 1'

        Thread.sleep(3000)
        3.times { scroll up }
        click inlineEdit
        Thread.sleep(3000)
        click removeEnumerationOne
        Thread.sleep(3000)
        click inlineEditSubmit

        refresh browser
        Thread.sleep(3000)
        select 'Test 1' open 'Data Types' select 'Enumeration 3'
        then:

        check enumerationsDetail missing 'one'
    }

    @Ignore
    def "create standard"() {
        when:
        select 'Test 1' select 'Data Types'
        remove messages
        click create

        then:
        check modalDialog displayed

        when:
        fill nameLabel with 'New Data Type'

        click save

        then:
        check modalDialog gone
        check backdrop gone
        check { infTableCell(1, 1) } contains "New Data Type"
    }

    @Ignore
    def "check it shows up with own detail page"() {
        when:
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        then:
        check rightSideTitle contains 'New Data Type'
    }

    @Ignore
    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check Common.detailSectionFormMetadata present once
        check detailSectionFormItemContent gone

        when: "Click the title"
        click Common.detailSectionFormMetadata.find('.title .btn')

        then: "Content is displayed"
        check detailSectionFormItemContent displayed

        cleanup:
        click Common.detailSectionFormMetadata.find('.title .btn')
    }

    @Ignore
    def "add metadata"() {
        setup:
        click inlineEdit
        click Common.detailSectionMetadata.find('.title .btn')
        scroll metadataTable

        when:
        fillMetadata foo: 'bar'
        scrollTop()
        click inlineEditSubmit

        then:
        check inlineEditSubmit gone
        // wait some time in order to have the view rendered
        Thread.sleep(300)
        click Common.detailSectionMetadata.find('.title .btn')
        check detailSectionCustomMetadataContent contains "foo"
        check detailSectionCustomMetadataContent contains "bar"
        click Common.detailSectionMetadata.find('.title .btn')
    }

    @Ignore
    def "remove metadata"() {
        setup:
        click inlineEdit
        click Common.detailSectionMetadata.find('.title .btn')
        scroll metadataTable

        when:
        click removeMetadataRow
        scrollTop()
        click inlineEditSubmit

        then:
        check inlineEditSubmit gone
        // wait some time in order to have the view rendered
        Thread.sleep(300)
        click Common.detailSectionMetadata.find('.title .btn')
        check detailSectionCustomMetadataContent missing "foo"
        check detailSectionCustomMetadataContent missing "bar"
        click Common.detailSectionMetadata.find('.title .btn')
    }

    @Ignore
    def "create new mapping"() {
        remove messages
        check backdrop gone

        when: "create new mapping action is clicked"
        click createMapping


        then: "crate new mapping dialog opens"
        check modalHeader is 'Create new mapping for New Data Type'

        when: "value domain is selected"
        fill dataType with 'xs:boolean' and pick first item

        and: "new mapping rule is created"
        fill mapping with "number(x).asType(Boolean)"

        and: "the create mapping button is clicked"
        click modalPrimaryButton

        then: "there is exactly one mapping"
        check backdrop gone
        check tabTotal('mappings') is '1'
    }

    @Ignore
    def "convert value"() {
        check backdrop gone

        when: "convert action is clicked"
        click convert

        then: "modal is shown"
        check modalHeader is 'Convert Value from New Data Type'

        when: "truthy value is entered"
        fill value with '10'

        then: "true is shown"
        check modalResponse is 'true'

        when: "falsy value is entered"
        fill value with '0'

        then: "false is shown"
        check modalResponse is 'false'

        when:
        click modalCloseButton

        then:
        check modalDialog gone
    }

    @Ignore
    def "edit mapping"() {
        remove messages

        when: "mappings tab selected"
        selectTab 'mappings'


        then: "mappings tab is active"
        waitFor { tabActive('mappings') }

        when:
        toggleInfTableRow(1)

        click editMapping

        then:
        check modalDialog displayed

        when:
        fill mapping with 'x'
        click modalPrimaryButton

        then:
        check { infTableCell(1, 2) } is 'x'
    }

    @Ignore
    def "create relationship"() {
        check backdrop gone
        remove messages

        when: "create relationship action is clicked"
        click createRelationship

        then: "modal is shown"
        check modalDialog displayed

        when:
        fill 'type' with 'related to'

        fill 'element' with 'xs:boolean' and pick first item

        click expandMetadata

        then:
        check metadataHelpBlock displayed

        when:
        fillMetadata($(modalDialog), foo: 'bar', one: 'two')

        remove messages

        click modalPrimaryButton

        then:
        check backdrop gone
        check tabTotal('relatedTo') is '1'

    }

    @Ignore
    def "create relationship from footer action"() {
        check backdrop gone
        remove messages

        when: "related to tab selected"
        selectTab('relatedTo')


        then: "related to tab is active"
        waitFor { tabActive('relatedTo') }

        when: "click the footer action"
        click CatalogueContent.create('#relatedTo-tab').find(tableFooterAction)

        then: "modal is shown"
        check modalDialog displayed

        when:
        fill 'element' with 'xs:string' and pick first item

        remove messages

        click modalPrimaryButton

        then:
        check backdrop gone
        check tabTotal('relatedTo') is '2'
    }

    @Ignore
    def "remove relationship"() {
        check backdrop gone
        remove messages
        when:
        toggleInfTableRow(1)
        scroll removeRelationshipButton

        click removeRelationship

        then:
        check confirm displayed

        when:
        click OK

        then:
        check tabTotal('relatedTo') is '1'
    }

    @Ignore
    def "remove mapping"() {
        check backdrop gone
        remove messages
        when:
        selectTab('mappings')
        toggleInfTableRow(1)
        click removeMapping

        then:
        check confirm displayed

        when:
        click OK
        remove messages

        then:
        waitFor {
            totalOf('mappings') == 0
        }
    }

    @Ignore
    def "change type"() {
        given:
            scroll up
        expect:
            check backdrop gone
            remove messages
        when:
            click changeType
        then:
            check modalDialog displayed
        when:
            fill dataTypeSelector with primitiveTypeValue
            click modalPrimaryButton
        then:
            check backdrop gone
            check primitiveTypeIcon displayed
    }
}
