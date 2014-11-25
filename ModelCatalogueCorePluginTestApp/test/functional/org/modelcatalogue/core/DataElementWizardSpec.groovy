package org.modelcatalogue.core

/**
 * Created by david on 02/11/14.
 */

import geb.spock.GebReportingSpec
import geb.spock.GebSpec
import org.modelcatalogue.core.pages.DataViewPage
import org.modelcatalogue.core.pages.ModalTreeViewPage
import spock.lang.Stepwise

@Stepwise
class DataElementWizardSpec extends GebReportingSpec  {

    def "login and select Data Element"() {
        when:
        go "#/catalogue/dataElement/all"

        then:
        at DataViewPage
        waitFor(120) {
            viewTitle.displayed
        }
        viewTitle.text().trim()     == 'Data Element List'
        //subviewTitle.text().trim()  == 'NHIC Datasets'

        when:
        loginAdmin()

        then:
        waitFor {
            addNewDataElementButton.displayed
        }
    }


    def "Add new data element"(){

            when: 'I click the add model button'
            addNewDataElementButton.click()


            then: 'the data element dialog opens'
            waitFor {
                dataWizard.displayed
            }

            when: 'the data element details are filled in'

            classifications = "NT1"
            name        = "NewDE1"
            description = "NT1 Description"
            valueDomain = "VD4Dent1"

            and: 'save button clicked'
            saveButton.click()
            then: 'the data element is saved and displayed at the top of the table'
             waitFor {
                    $('div.inf-table-body tbody tr:nth-child(1) td:nth-child(3)', text: "NewDE1").displayed
             }

    }


    def "Check the data element shows up with own details"(){


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
        at DataViewPage

        when: "edit action is clicked"
        $('#role_item_edit-catalogue-elementBtn').click()

        then: "edit dialog is shown"
        waitFor {
            dataWizard.displayed
        }

        when: "new value domain is changed"
        valueDomain = "VD4Dent2"

        and: 'save button clicked'
        saveButton.click()

        then: 'the data element is saved and and different value domain is shown'
        waitFor {
            $('td', 'data-value-for': "Value Domain").text().contains('VD4Dent2')
        }
    }

    def "Remove the value domain"() {
        at DataViewPage

        when: "edit action is clicked"
        $('#role_item_edit-catalogue-elementBtn').click()

        then: "edit dialog is shown"
        waitFor {
            dataWizard.displayed
        }

        when: "new value domain is changed"
        valueDomain = ""

        and: 'save button clicked'
        saveButton.click()

        then: 'the data element is saved and and different value domain is shown'
        waitFor {
            $('td', 'data-value-for': "Value Domain").text().trim() == ''
        }
    }



}
