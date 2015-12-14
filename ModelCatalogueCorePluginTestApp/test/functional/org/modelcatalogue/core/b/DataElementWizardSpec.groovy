    package org.modelcatalogue.core.b

    import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
    import org.modelcatalogue.core.geb.CatalogueAction
    import spock.lang.Stepwise

    /**
     * Created by david on 02/11/14.
     */
    @Stepwise
    class DataElementWizardSpec extends AbstractModelCatalogueGebSpec  {

        protected static final CatalogueAction save = CatalogueAction.create('modal', 'modal-save-element')
        protected static final CatalogueAction create = CatalogueAction.create('list', 'create-catalogue-element')
        protected static final CatalogueAction finalize = CatalogueAction.create('item', 'change-element-state', 'finalize')
        protected static final CatalogueAction newVersion = CatalogueAction.create('item', 'change-element-state', 'create-new-version')
        protected static final CatalogueAction archive = CatalogueAction.create('item', 'change-element-state', 'archive')
        protected static final CatalogueAction delete = CatalogueAction.create('item', 'change-element-state', 'delete')

        protected static final String name = 'name'
        protected static final String description = 'description'

        protected static final String status = "h3 small span.label"
        protected static final String rightSideTitle = "h3:not(.ng-hide):not(.data-model-heading)"
        protected static final String dataWizard = 'div.basic-edit-modal-prompt'
        protected static final String subviewStatus = "h3 small span.label"
        protected static final String modalDialog = "div.modal"
        protected static final String backdrop = '.modal-backdrop'
        protected static final String confirm = '.modal.messages-modal-confirm'
        protected static final String OK = '.modal.messages-modal-confirm .btn-primary'
        protected static final String nameInTheFirstRow = 'div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)'
        protected static final String firstRowLink = 'div.inf-table-body tbody tr:nth-child(1) td:nth-child(3) a'

        def "login and select Data Element"() {
            login admin

            dataModel('Test 1') / 'Test 1' / 'Data Elements'

            expect:
            check rightSideTitle is 'Data Element List'
        }


        def "Add new data element"(){
            when: 'I click the add model button'
            click create

            then: 'the data element dialog opens'
            check dataWizard displayed

            when:
            fill name with "NewDE1"
            fill description with "NT1 Description"

            and: 'save button clicked'
            click save

            then: 'the data element is saved and displayed at the top of the table'
            check nameInTheFirstRow, text: "NewDE1" displayed

        }


        def "Check the data element shows up with own details"(){
            expect:
            check backdrop gone
            
            when: 'Data Element is located'

            check nameInTheFirstRow, text: "NewDE1" displayed

            then: 'Click the element'

            click firstRowLink

            check rightSideTitle is 'NewDE1 DRAFT'

        }

        def "finalize element"() {
            check backdrop gone
            when: "finalize is clicked"
            click finalize

            then: "modal prompt is displayed"
            check confirm displayed

            when: "ok is clicked"
            click OK

            then: "the element is finalized"
            check status is 'FINALIZED'
        }

        def "create new version of the element"() {
            check backdrop gone
            when: "new version is clicked"
            click newVersion

            then: "modal prompt is displayed"
            check confirm displayed

            when: "ok is clicked"
            click OK

            then: "the element new draft version is created"
            check status is 'DRAFT'
        }

        def "deprecate the element"() {
            waitUntilModalClosed()
            when: "depracete action is clicked"
            click archive

            then: "modal prompt is displayed"
            check confirm displayed

            when: "ok is clicked"
            click OK

            then: "the element is now deprecated"
            check subviewStatus is 'DEPRECATED'

        }

        def "hard delete the element"() {
            check backdrop gone
            when: "delete action is clicked"
            click delete

            then: "modal prompt is displayed"
            check confirm displayed

            when: "ok is clicked"
            click OK

            then: "you are redirected to the list page"
            check rightSideTitle is 'Data Element List'

        }
    }
