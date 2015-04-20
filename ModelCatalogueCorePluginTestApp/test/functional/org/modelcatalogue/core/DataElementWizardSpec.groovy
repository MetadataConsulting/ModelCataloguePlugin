    package org.modelcatalogue.core

    import org.modelcatalogue.core.pages.DataViewPage
    import spock.lang.Stepwise

    /**
     * Created by david on 02/11/14.
     */
    @Stepwise
    class DataElementWizardSpec extends AbstractModelCatalogueGebSpec  {

        def "login and select Data Element"() {
            go "#/"
            loginAdmin()

            when:
            go "#/catalogue/dataElement/all"

            then:
            at DataViewPage
            waitFor(120) {
                viewTitle.displayed
            }
            waitFor(120) {
                viewTitle.text().trim() == 'Data Element List'
            }

            waitFor {
                addNewDataElementButton.displayed
            }
        }


        def "Add new data element"(){
            waitUntilModalClosed()
            when: 'I click the add model button'
            addNewDataElementButton.click()


            then: 'the data element dialog opens'
            waitFor {
                dataWizard.displayed
            }

            when: 'the data element details are filled in'

            classifications = "NT1"
            selectCepItemIfExists()

            name        = "NewDE1"
            description = "NT1 Description"

            valueDomain = "VD4Dent1"
            selectCepItemIfExists()

            and: 'save button clicked'
            saveButton.click()
            then: 'the data element is saved and displayed at the top of the table'
            waitFor {
                $('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)', text: "NewDE1").displayed
            }

        }


        def "Check the data element shows up with own details"(){
            waitUntilModalClosed()
            when: 'Data Element is located'

            waitFor {
                $('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)', text: "NewDE1").displayed
            }
            then: 'Click the element'

            $('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3) a').click()

            waitFor(60) {
                pageTitle.displayed
            }

            pageTitle.text().trim()     == 'NewDE1 DRAFT'

        }

        def "Edit the value domain"() {
            waitUntilModalClosed()
            when: "edit action is clicked"
            actionButton('edit-catalogue-element').click()

            then: "edit dialog is shown"
            waitFor {
                dataWizard.displayed
            }

            when: "new value domain is changed"
            valueDomain = "VD4Dent2"
            selectCepItemIfExists()

            and: 'save button clicked'
            saveButton.click()

            and: "properties tab is shown"
            selectTab('properties')

            then: 'the data element is saved and and different value domain is shown'
            waitFor(120) {
                $('td', 'data-value-for': "Value Domain").text().contains('VD4Dent2')
            }
        }

        def "Remove the value domain"() {
            waitUntilModalClosed()
            when: "edit action is clicked"
            actionButton('edit-catalogue-element').click()

            then: "edit dialog is shown"
            waitFor {
                dataWizard.displayed
            }

            when: "new value domain is changed"
            valueDomain = ""

            and: 'save button clicked'
            saveButton.click()

            then: 'the data element is saved and and no value domain is shown'
            waitFor(120) {
                $('td', 'data-value-for': "Value Domain").text().trim() == ''
            }
        }

        def "finalize element"() {
            waitUntilModalClosed()
            when: "finalize is clicked"
            actionButton('change-element-state').click()
            actionButton('finalize').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "the element is finalized"
            waitFor(120) {
                subviewStatus.text() == 'FINALIZED'
            }

        }

        def "create new version of the element"() {
            waitUntilModalClosed()
            when: "new version is clicked"
            actionButton('change-element-state').click()
            actionButton('create-new-version').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "the element new draft version is created"
            waitFor(120) {
                subviewStatus.text() == 'DRAFT'
            }

        }

        def "deprecate the element"() {
            waitUntilModalClosed()
            when: "depracete action is clicked"
            actionButton('change-element-state').click()
            actionButton('archive').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "the element is now deprecated"
            waitFor {
                subviewStatus.text() == 'DEPRECATED'
            }

        }

        def "hard delete the element"() {
            waitUntilModalClosed()
            when: "delete action is clicked"
            actionButton('change-element-state').click()
            actionButton('delete').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "you are redirected to the list page"
            waitFor {
                url == '#/catalogue/dataElement/all'
            }

        }
    }
