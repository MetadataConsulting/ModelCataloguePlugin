    package org.modelcatalogue.core.b

    import static org.modelcatalogue.core.geb.Common.*

    import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
    import spock.lang.Stepwise

    /**
     * Created by david on 02/11/14.
     */
    @Stepwise
    class DataElementWizardSpec extends AbstractModelCatalogueGebSpec  {


        def "login and select Data Element"() {
            login admin

            select('Test 1') / 'Test 1' / 'Data Elements'

            expect:
            check rightSideTitle is 'Data Element List'
        }


        def "Add new data element"(){
            when: 'I click the add model button'
            click create

            then: 'the data element dialog opens'
            check wizard displayed

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
