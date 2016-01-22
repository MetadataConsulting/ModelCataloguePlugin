package org.modelcatalogue.core.c

import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.*

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Stepwise

@Stepwise
class DataTypeWizardSpec extends AbstractModelCatalogueGebSpec {


    public static final String expandTableHeader = '.inf-table thead .inf-cell-expand'
    public static final CatalogueContent nameFilter = CatalogueContent.create('input.form-control', placeholder: 'Filter Name')
    public static final CatalogueContent enumerationsTableEditor = CatalogueContent.create('table', title: 'Enumerations')
    public static final String tableRows = '.inf-table tbody .inf-table-item-row'
    public static final String pickReferenceType = '#pickReferenceType'
    public static final String pickPrimitiveType = '#pickPrimitiveType'
    public static final String pickEnumeratedType = '#pickEnumeratedType'
    public static final String updateMetadataButton = '.tab-pane button.btn-primary.update-object'
    public static final String addMetadataButton = '.tab-pane .btn.add-metadata'
    public static final String removeMetadataRow = '.tab-pane a.soe-remove-row'
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

    def "go to login"() {
        login admin

        when:
        select('NHIC') / 'NHIC' / 'Data Types'

        then:
        check rightSideTitle is 'Data Type List'
    }


    def "filter by name in header"() {
        check backdrop gone

        when: "the header is expanded"
        click expandTableHeader

        then: "the name filter is displayed"
        check nameFilter

        when: "we filter by anatomical side"
        fill nameFilter with 'anatomical side'

        then: "only one row will be shown"
        check tableRows test { it.size() == 1 }

        when: "the filter is reset"
        fill nameFilter with ''

        then: "we see many rows again"
        check tableRows test { it.size() >= 1 }
    }



    def "create reference"() {
        select('Test 1') / 'Test 1'

        addDataModelImport 'SI', 'XMLSchema', 'NHIC'

        selectInTree 'Data Types'

        when:
        check closeGrowlMessage gone
        click create

        then:
        check modalDialog displayed

        when:
        fill name with 'New Reference Type'

        click pickReferenceType

        fill 'input#dataClass' with 'DEMOGRAPHICS' and prefer first existing item

        click save

        then:
        check { infTableCell(1, 1, text: 'New Reference Type') } displayed
        check { infTableCell(1, 2, text: 'DEMOGRAPHICS') } displayed
    }

    def "create primitive"() {
        when:
        check closeGrowlMessage gone
        click create

        then:
        check modalDialog displayed

        when:
        fill name with 'New Primitive Type'

        click pickPrimitiveType


        fill 'input#measurementUnit' with 'new unit'

        click save

        then:
        check { infTableCell(1, 1, text: 'New Primitive Type') } displayed
        check { infTableCell(1, 2, text: 'new unit') } displayed
    }

    def "create enum"() {
        when:
        check closeGrowlMessage gone
        click create

        then:
        check modalDialog displayed

        when:
        fill name with 'New Enum Type'

        click pickEnumeratedType


        check enumerationsTableEditor displayed

        fillMetadata '01': 'one', '02': 'two'

        click save

        then:
        check backdrop gone
        check { infTableCell(1, 1, text: 'New Enum Type') } displayed
    }

    def "create standard"() {
        when:
        check closeGrowlMessage gone
        click create

        then:
        check modalDialog displayed

        when:
        fill name with 'New Data Type'

        click save

        then:
        check backdrop gone
        check { infTableCell(1, 1, text: 'New Data Type')} displayed
    }

    def "check it shows up with own detail page"(){
        when:
        click { infTableCell(1, 1).find('a:not(.inf-cell-expand)') }
        then:

        check rightSideTitle contains 'New Data Type Test 1'
    }



    def "create new mapping"() {
        check closeGrowlMessage gone
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

    def "edit mapping"() {
        check closeGrowlMessage gone

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


    def "create relationship"() {
        check backdrop gone
        check closeGrowlMessage gone

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

        check closeGrowlMessage gone

        click modalPrimaryButton

        then:
        check backdrop gone
        check tabTotal('relatedTo') is '1'

    }

    def "create relationship from footer action"() {
        check backdrop gone
        check closeGrowlMessage gone

        when: "related to tab selected"
        selectTab('relatedTo')


        then: "related to tab is active"
        waitFor { tabActive('relatedTo') }

        when: "click the footer action"
        click tableFooterAction

        then: "modal is shown"
        check modalDialog displayed

        when:
        fill 'element' with 'xs:string' and pick first item

        check closeGrowlMessage gone

        click modalPrimaryButton

        then:
        check backdrop gone
        check tabTotal('relatedTo') is '2'
    }

    def "remove relationship"() {
        check backdrop gone
        check closeGrowlMessage gone
        when:
        toggleInfTableRow(1)

        click removeRelationship

        then:
        check confirm displayed

        when:
        click OK

        then:
        check tabTotal('relatedTo') is '1'
    }

    def "remove mapping"() {
        check backdrop gone
        check closeGrowlMessage gone
        when:
        selectTab('mappings')
        toggleInfTableRow(1)
        click removeMapping

        then:
        check confirm displayed

        when:
        click OK
        check closeGrowlMessage gone

        then:
        waitFor {
            totalOf('mappings') == 0
        }
    }

    def "update metadata"() {
        check backdrop gone

        when:
        selectTab 'ext'

        then:
        check updateMetadataButton disabled
        check closeGrowlMessage gone

        when:
        click addMetadataButton

        then:
        check addMetadataButton gone

        when:
        fillMetadata foo: 'bar', one: 'two', free: 'for'

        then:
        check updateMetadataButton enabled

        when:
        click updateMetadataButton

        then:
        check updateMetadataButton disabled

        when:
        3.times {
            click removeMetadataRow
        }

        then:
        check updateMetadataButton enabled

        when:
        click updateMetadataButton

        then:
        check updateMetadataButton disabled

        when:
        refresh browser

        then:
        check addMetadataButton displayed
    }

}