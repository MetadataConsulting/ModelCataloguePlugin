package org.modelcatalogue.core.generalTestSuit

import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueAction
import org.modelcatalogue.core.gebUtils.CatalogueContent
import org.modelcatalogue.core.gebUtils.Common
import spock.lang.Stepwise

@Stepwise
class DataClassWizardSpec extends AbstractModelCatalogueGebSpec {

    private static final String stepMetadata = "button#step-metadata"
    private static final String stepChildren = "#step-children"
    private static final String stepElements = "#step-elements"
    private static final String stepFinish = "#step-finish"
    private static final String exitButton = "#exit-wizard"
    private static final String wizardSummary = '.wizard-summary'
    private static final String xmlEditorStylesheet = '#xml-editor-stylesheet'
    private static final String xmlEditorSource = '#xml-editor-source'
    private static final String xmlEditorResult = '#xml-editor-result'
    private static final CatalogueContent detailSectionDataElement = CatalogueContent.create('data-view-name': 'Children')
    private static final CatalogueAction exportXml = CatalogueAction.runLast('item', 'export', 'edit-XML')
    private static final CatalogueContent resultContentLines = CatalogueContent.create(xmlEditorResult).find('.ace_content .ace_line')
    private static final String NEW_DATA_CLASS_NAME = "New ${UUID.randomUUID().toString()}"



    def "go to login"() {
        login Common.admin

        select 'Test 2'

        addDataModelImport 'nhic'

        selectInTree "Data Classes"

        expect:
        check '#jserrors' gone
        check Common.rightSideTitle contains "Active Data Classes"
    }


    def "Add new data class"() {
        click Common.create
        expect: 'the model dialog opens'
        check Common.modalDialog displayed

        when: 'the model details are filled in'
        fill Common.nameLabel with NEW_DATA_CLASS_NAME
        fill Common.modelCatalogueId with "http://www.example.com/${UUID.randomUUID().toString()}"
        fill Common.description with "Description"

        then: 'metadataStep step is not disabled'
        check stepMetadata enabled

        when: 'metadataStep step is clicked'
        click stepMetadata


        then:
        check stepMetadata has 'btn-primary'

        when: 'metadataStep are filled in'
        fillMetadata foo: 'bar', one: 'two'

        and: 'children step is clicked'
        click stepChildren

        then:
        check stepChildren has 'btn-primary'

        when: 'child metadataStep are filled in'
        fillMetadata 'Min Occurs': '1', 'Max Occurs': '10'

        and: 'the child is selected'
        click '.search-for-more-icon'
        fill '.modal-body .input-group-lg input' with 'patient'

        click '.list-group-item.item-found'

        and: 'create child from scratch and leave it filled in'
        fill 'child-data-class' with 'This should create new child data class'

        and: 'elements step is clicked'
        click stepElements

        then:
        check stepElements has 'btn-primary'

        when: 'element metadataStep are filled in'
        fillMetadata 'Min Occurs': '2', 'Max Occurs': '25'

        and: 'the element is selected'
        fill 'data-element' with 'nhs'
        selectCepItemIfExists()
        Thread.sleep(3000)

        and: 'finish is clicked'
        click stepFinish
        Thread.sleep(5000)

        then: 'the data class is saved'
        check wizardSummary is "Data Class ${NEW_DATA_CLASS_NAME} created"

        when:
        Thread.sleep(3000)
        //add timeout
        click exitButton

        and:
        selectInTree 'Data Classes', true

        then:
        check CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith(NEW_DATA_CLASS_NAME)) displayed

        check Common.modalDialog gone
    }

    def "Add another data class"() {
        click Common.create

        expect: 'the data class dialog opens'
        check Common.modalDialog displayed
        //check'div.modal-header>h4'isDisplayed()

        when: 'the data class details are filled in'
        fill Common.nameLabel with "Another New"

        and: 'finish is clicked'
        click stepFinish
        Thread.sleep(3000)

        then: 'the data class is saved'
        check wizardSummary is "Data Class Another New created"

        when:
        click exitButton

        then:
        check CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith("Another New")) displayed

        when: "click the footer action"
        click CatalogueContent.create('span.catalogue-element-treeview-name', text: startsWith("Another New"))
        click detailSectionDataElement.find(Common.detailSectionHeaderAddAction)

        then: "modal is shown"
        check Common.modalDialog displayed

        when:
        fill 'type' with 'parent of'
        fill 'element' with 'demographics'
        selectCepItemIfExists()

        click Common.modalPrimaryButton

        then: 'the number of children of Another New must be 1'

           check 'td.col-md-5' contains'DEMOGRAPHICS'
//        check {
//            $('span.catalogue-element-treeview-name', text: startsWith("Another New")).parent().parent().find('.badge')
//        } is '1'

    }

    def "edit child data class"() {
        click Common.inlineEdit

        expect:
        check "input[name='name']" displayed

        when:
        fill Common.nameLabel with 'Changed Name'
        Thread.sleep(3000)
        click Common.inlineEditSubmit
        Thread.sleep(3000)
        remove Common.messages

        then: "same number of children are still shown"
        remove Common.messages
        check 'td.col-md-5' contains'DEMOGRAPHICS'

//        check {
//         $('span.catalogue-element-treeview-name', text: startsWith("Changed Name")).parent().parent().find('.badge')
//         } contains  '1'


    }

    def "xml editor"() {
        when:
        click exportXml

        then: "three editor present"
        check xmlEditorStylesheet displayed
        check xmlEditorSource displayed
        check xmlEditorResult displayed

        and: "result has some xsl generated"
        check resultContentLines test 10, { it.size() > 1 }
    }
}
