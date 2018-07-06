package org.modelcatalogue.core.suiteA

import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage

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
    public static
    final CatalogueContent nameFilter = CatalogueContent.create('input.form-control', placeholder: 'Filter Name')
    public static
    final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    public static
    final CatalogueContent removeEnumerationOne = CatalogueContent.create('.soe-remove-row', 'data-for-property': '01')
    public static final CatalogueContent enumerationsDetail = CatalogueContent.create('data-view-name': "Enumerations")
    public static final String tableRows = '.inf-table tbody .inf-table-item-row'
    public static final String pickReferenceType = '#pickReferenceType'
    public static final String pickPrimitiveType = '#pickPrimitiveType'
    public static final String pickEnumeratedType = '#pickEnumeratedType'
    public static final String pickSubset = '#pickSubsetType'
    public static final String baseEnumeration = '#baseEnumeration'
    public static final String updateMetadataButton = '.tab-pane button.btn-primary.update-object'
    public static final String addMetadataButton = '.tab-pane .btn.add-metadata'
    public static final String removeMetadataRow = '[data-view-content-name="Custom Metadata"] a.soe-remove-row'
    public static
    final CatalogueAction createMapping = CatalogueAction.runFirst('item', 'catalogue-element', 'create-new-mapping')
    public static
    final CatalogueAction createRelationship = CatalogueAction.runLast('item', 'catalogue-element', 'create-new-relationship')
    public static final CatalogueAction convert = CatalogueAction.runLast('item', 'catalogue-element', 'convert')
    public static final CatalogueAction removeRelationship = CatalogueAction.runLast('item', 'remove-relationship')
    public static final CatalogueAction removeMapping = CatalogueAction.runLast('item', 'remove-mapping')
    public static final String dataType = 'dataType'
    public static final String mapping = 'mapping'
    public static final String modalPrimaryButton = 'div.modal button.btn-primary, div.modal a.btn-primary'
    public static final String value = 'value'
    public static final GString modalResponse = "$Common.modalDialog pre"
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

    @Ignore
    def "go to login"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search("NHIC")
        dashboardPage.select('NHIC')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage

        and:
        check Common.rightSideTitle is 'Active Data Types'
    }

    @Ignore
    def "create reference"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.search('Test 1')
        dashboardPage.select('Test 1')

        then:
        at DataModelPage

        when:
        addDataModelImport 'XMLSchema', 'NHIC'

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage

        when:
        remove Common.messages
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'New Reference Type'


        click pickReferenceType

        fill 'dataClass' with 'DEMOGRAPHICS' and Common.prefer first Common.existing item
        fill Common.description with "Test Reference Unit"

        click Common.save

        then:
        check Common.modalDialog gone
        check { infTableCell(1, 1) } contains "New Reference Type"
        check { infTableCell(1, 2, text: 'DEMOGRAPHICS') } displayed
    }

    @Ignore
    def "create primitive"() {
        when:
        remove Common.messages
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'New Primitive Type'
        fill Common.description with "Test Primitive Unit"

        click pickPrimitiveType

        check primitiveContent displayed
        fill 'measurementUnit' with 'new unit'
        check "$primitiveContent .dropdown-menu" displayed
        pressKey("$primitiveContent input", Keys.ESCAPE)

        click Common.save

        then:
        check Common.modalDialog gone
        check { infTableCell(1, 1) } contains "New Primitive Type"
        check { infTableCell(1, 2, text: 'new unit') } displayed
    }

    @Ignore
    def "create enum"() {
        when:
        remove Common.messages
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'Enumeration 1'

        click pickEnumeratedType


        check enumerationsTableEditor displayed

        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'

        click Common.save

        then:
        check Common.modalDialog gone
        check Common.backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 1"
    }

    @Ignore
    def "create subset 2"() {
        when:
        remove Common.messages
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'Enumeration 2'

        click pickSubset

        check baseEnumeration displayed

        fill baseEnumeration with 'Enumeration 1' and Common.pick first Common.item

        click '#subtype-enum-1'
        click '#subtype-enum-2'

        click Common.save

        then:
        check Common.modalDialog gone
        check Common.backdrop gone
        check { infTableCell(1, 1) } contains "Enumeration 2"
    }

    @Ignore
    def "create subset 3"() {
        when:
        remove Common.messages
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'Enumeration 3'

        click pickSubset

        check baseEnumeration displayed

        fill baseEnumeration with 'Enumeration 2' and Common.pick first Common.item

        click '#subtype-enum-1'

        click Common.save

        then:
        check Common.modalDialog gone
        check Common.backdrop gone
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
        3.times { scroll Common.up }
        click Common.inlineEdit
        Thread.sleep(3000)
        click removeEnumerationOne
        Thread.sleep(3000)
        click Common.inlineEditSubmit

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
        remove Common.messages
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'New Data Type'

        click Common.save

        then:
        check Common.modalDialog gone
        check Common.backdrop gone
        check { infTableCell(1, 1) } contains "New Data Type"
    }

    @Ignore
    def "check it shows up with own detail page"() {
        when:
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }

        then:
        check Common.rightSideTitle contains 'New Data Type'
    }

    @Ignore
    def "Check Form (Item) detail section is present and collapsed"() {
        expect:
        check Common.detailSectionFormMetadata present Common.once
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
        click Common.inlineEdit
        click Common.detailSectionMetadata.find('.title .btn')
        scroll metadataTable

        when:
        fillMetadata foo: 'bar'
        scrollTop()
        click Common.inlineEditSubmit

        then:
        check Common.inlineEditSubmit gone
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
        click Common.inlineEdit
        click Common.detailSectionMetadata.find('.title .btn')
        scroll metadataTable

        when:
        click removeMetadataRow
        scrollTop()
        click Common.inlineEditSubmit

        then:
        check Common.inlineEditSubmit gone
        // wait some time in order to have the view rendered
        Thread.sleep(300)
        click Common.detailSectionMetadata.find('.title .btn')
        check detailSectionCustomMetadataContent missing "foo"
        check detailSectionCustomMetadataContent missing "bar"
        click Common.detailSectionMetadata.find('.title .btn')
    }

    @Ignore
    def "create new mapping"() {
        remove Common.messages
        check Common.backdrop gone

        when: "create new mapping action is clicked"
        click createMapping


        then: "crate new mapping dialog opens"
        check Common.modalHeader is 'Create new mapping for New Data Type'

        when: "value domain is selected"
        fill dataType with 'xs:boolean' and Common.pick first Common.item

        and: "new mapping rule is created"
        fill mapping with "number(x).asType(Boolean)"

        and: "the create mapping button is clicked"
        click modalPrimaryButton

        then: "there is exactly one mapping"
        check Common.backdrop gone
        check tabTotal('mappings') is '1'
    }

    @Ignore
    def "convert value"() {
        check Common.backdrop gone

        when: "convert action is clicked"
        click convert

        then: "modal is shown"
        check Common.modalHeader is 'Convert Value from New Data Type'

        when: "truthy value is entered"
        fill value with '10'

        then: "true is shown"
        check modalResponse is 'true'

        when: "falsy value is entered"
        fill value with '0'

        then: "false is shown"
        check modalResponse is 'false'

        when:
        click Common.modalCloseButton

        then:
        check Common.modalDialog gone
    }

    @Ignore
    def "edit mapping"() {
        remove Common.messages

        when: "mappings tab selected"
        selectTab 'mappings'


        then: "mappings tab is active"
        waitFor { tabActive('mappings') }

        when:
        toggleInfTableRow(1)

        click editMapping

        then:
        check Common.modalDialog displayed

        when:
        fill mapping with 'x'
        click modalPrimaryButton

        then:
        check { infTableCell(1, 2) } is 'x'
    }

    @Ignore
    def "create relationship"() {
        check Common.backdrop gone
        remove Common.messages

        when: "create relationship action is clicked"
        click createRelationship

        then: "modal is shown"
        check Common.modalDialog displayed

        when:
        fill 'type' with 'related to'

        fill 'element' with 'xs:boolean' and Common.pick first Common.item

        click expandMetadata

        then:
        check metadataHelpBlock displayed

        when:
        fillMetadata($(Common.modalDialog), foo: 'bar', one: 'two')

        remove Common.messages

        click modalPrimaryButton

        then:
        check Common.backdrop gone
        check tabTotal('relatedTo') is '1'

    }

    @Ignore
    def "create relationship from footer action"() {
        check Common.backdrop gone
        remove Common.messages

        when: "related to tab selected"
        selectTab('relatedTo')


        then: "related to tab is active"
        waitFor { tabActive('relatedTo') }

        when: "click the footer action"
        click CatalogueContent.create('#relatedTo-tab').find(Common.tableFooterAction)

        then: "modal is shown"
        check Common.modalDialog displayed

        when:
        fill 'element' with 'xs:string' and Common.pick first Common.item

        remove Common.messages

        click modalPrimaryButton

        then:
        check Common.backdrop gone
        check tabTotal('relatedTo') is '2'
    }

    @Ignore
    def "remove relationship"() {
        check Common.backdrop gone
        remove Common.messages
        when:
        toggleInfTableRow(1)
        scroll removeRelationshipButton

        click removeRelationship

        then:
        check Common.confirm displayed

        when:
        click Common.OK

        then:
        check tabTotal('relatedTo') is '1'
    }

    @Ignore
    def "remove mapping"() {
        check Common.backdrop gone
        remove Common.messages
        when:
        selectTab('mappings')
        toggleInfTableRow(1)
        click removeMapping

        then:
        check Common.confirm displayed

        when:
        click Common.OK
        remove Common.messages

        then:
        waitFor {
            totalOf('mappings') == 0
        }
    }

    @Ignore
    def "change type"() {
        given:
        scroll Common.up
        expect:
        check Common.backdrop gone
        remove Common.messages
        when:
        click changeType
        then:
        check Common.modalDialog displayed
        when:
        fill dataTypeSelector with primitiveTypeValue
        click modalPrimaryButton
        then:
        check Common.backdrop gone
        check primitiveTypeIcon displayed
    }
}
