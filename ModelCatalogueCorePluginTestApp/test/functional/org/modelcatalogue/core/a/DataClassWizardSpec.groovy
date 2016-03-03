package org.modelcatalogue.core.a

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class DataClassWizardSpec extends AbstractModelCatalogueGebSpec {


    private static final String stepMetadata                = "#step-metadata"
    private static final String stepChildren                = "#step-children"
    private static final String stepElements                = "#step-elements"
    private static final String stepFinish                  = "#step-finish"
    private static final String exitButton                  = "#exit-wizard"
    private static final String wizardSummary               = '.wizard-summary'
    private static final CatalogueAction inlineEdit         = CatalogueAction.runFirst('item-detail', 'inline-edit')
    private static final CatalogueAction inlineEditSubmit   = CatalogueAction.runFirst('item-detail', 'inline-edit-submit')


    def "go to login"() {
        login admin

        select('Test 2') / "Test 2"

        addDataModelImport 'nhic'

        selectInTree "Data Classes"

        expect:
        check '#jserrors' gone
        check rightSideTitle contains "Data Classes"
    }


    def "Add new data class"() {
        click create
        expect: 'the model dialog opens'
        check modalDialog displayed

        when: 'the model details are filled in'
        fill name  with "New"
        fill modelCatalogueId with "http://www.example.com/${UUID.randomUUID().toString()}"
        fill description with "Description"

        then: 'metadata step is not disabled'
        check stepMetadata enabled

        when: 'metadata step is clicked'
        click stepMetadata

        then:
        check stepMetadata has 'btn-primary'

        when: 'metadata are filled in'
        fillMetadata foo: 'bar', one: 'two'


        and: 'children step is clicked'
        click stepChildren

        then:
        check stepChildren has 'btn-primary'

        when: 'child metadata are filled in'
        fillMetadata 'Min Occurs': '1', 'Max Occurs': '10'

        and: 'the child is selected'
        click '.search-for-more-icon'
        fill '.modal-body .input-group-lg input' with 'patient'

        click '.list-group-item.item-found'

        and: 'create child from scratch and leave it filled in'
        fill name with 'This should create new child data class'

        and: 'elements step is clicked'
        click stepElements

        then:
        check stepElements has 'btn-primary'

        when: 'element metadata are filled in'
        fillMetadata 'Min Occurs': '2', 'Max Occurs': '25'

        and: 'the element is selected'
        fill name with 'nhs'
        selectCepItemIfExists()

        and: 'finish is clicked'
        click stepFinish

        then: 'the data class is saved'
        check wizardSummary is "Data Class New created"

        when:
        click exitButton

        then:
        check CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith("New")) displayed

        check modalDialog gone
    }

    def "Add another data class"(){
        click create


        expect: 'the data class dialog opens'
        check modalDialog displayed

        when: 'the data class details are filled in'
        fill name with "Another New"


        and: 'finish is clicked'
        click stepFinish

        then: 'the data class is saved'
        check wizardSummary is "Data Class Another New created"

        when:
        click exitButton

        then:
        check CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith("Another New")) displayed


        when: "click the footer action"
        click CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith("Another New"))

        selectTab('contains')

        click tableFooterAction

        then: "modal is shown"
        check modalDialog displayed

        when:
        fill 'type' with 'parent of'
        fill 'element' with 'demographics'
        selectCepItemIfExists()

        click modalPrimaryButton

        then: 'the number of children of Another New must be 1'
        check {
            $('span.catalogue-element-treeview-name', text: startsWith("Another New")).parent().parent().find('.badge')
        } is '1'

    }

    def "edit child data class"() {
        click inlineEdit

        expect:
        check "input[name='name']" displayed

        when:
        fill 'name' with 'Changed Name'

        click inlineEditSubmit

        then: "same number of children are still shown"
        check closeGrowlMessage gone

        check {
            $('span.catalogue-element-treeview-name', text: startsWith("Changed Name")).parent().parent().find('.badge')
        } is '1'
    }

}
