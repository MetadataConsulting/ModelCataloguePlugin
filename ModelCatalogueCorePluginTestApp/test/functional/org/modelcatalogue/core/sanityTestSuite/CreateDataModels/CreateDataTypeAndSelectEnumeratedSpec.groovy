package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage

import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.save
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
@Ignore
class CreateDataTypeAndSelectEnumeratedSpec extends AbstractModelCatalogueGebSpec {

    private static final String enumerated = "input#pickEnumeratedType"
    private static final String paste = "button.btn-default"
    private static final String deleteButton = "a#delete-menu-item-link>span:nth-child(3)"
    private static final String enumeratedType = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"
    private static final String dataType = "tr.inf-table-item-row>td:nth-child(1)>span>span>a"
    private static final String first_row = "tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)"

    //@NotYetImplemented
    def "login and navigate to the model"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.search('Test 3')
        dashboardPage.select('Test 3')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')

        then:
        at DataTypesPage

        and:
        check rightSideTitle contains 'Active Data Types'
    }

    def "navigate to data type creation page "() {
        when:
        click create

        then:
        check modalHeader contains 'Create Data Type'
    }

    def "fill the create data type form"() {
        when:
        fill nameLabel with "TESTING_DATA_TYPE "

        fill modelCatalogueId with "MET-345"

        fill description with "my description of data type"

        and: 'click on enumerated type'
        click enumerated
        fillMetadata '01': 'one', '02': 'two', '03': 'three', '04': 'four', '05': 'five'

        and:
        click paste

        then:
        check "button.btn-primary" displayed

        when:
        click save

        then:
        check first_row contains 'TESTING_DATA_TYPE'
    }

    def "remove the created data type "() {
        when:
        click dataType

        and:
        click enumeratedType

        and:
        click deleteButton

        and:
        click modalPrimaryButton

        then:
        check first_row isGone()
    }
}
