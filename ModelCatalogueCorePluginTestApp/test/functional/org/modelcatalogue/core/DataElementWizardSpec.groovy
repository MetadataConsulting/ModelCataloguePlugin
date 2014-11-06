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
        waitFor(60) {
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
            name        = "New"
            description = "NT1 Description"
            valueDomain = "VD4Dent1"

            and: 'save button clicked'
            saveButton.click()

            then: 'the data element is saved'

            waitFor {
                $('td.inf-table-item-cell.ng-scope.col-md-3', text: "New").displayed
            }

    }


    def "Check the value domain exists"(){



    }

    def "Add an enumerated datatype to the value domain"(){

    }

}
