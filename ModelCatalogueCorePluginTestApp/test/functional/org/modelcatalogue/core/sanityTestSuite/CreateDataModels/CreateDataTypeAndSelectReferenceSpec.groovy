package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import spock.lang.Unroll

import static org.modelcatalogue.core.geb.Common.getCreate
import static org.modelcatalogue.core.geb.Common.getDescription
import static org.modelcatalogue.core.geb.Common.getModalHeader
import static org.modelcatalogue.core.geb.Common.getModelCatalogueId
import static org.modelcatalogue.core.geb.Common.getNameLabel
import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import static org.modelcatalogue.core.geb.Common.getSave
import static org.modelcatalogue.core.geb.Common.messages
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Ignore
@Stepwise
class CreateDataTypeAndSelectReferenceSpec extends AbstractModelCatalogueGebSpec {
    private static final String reference = "input#pickReferenceType"
    private static final String dataClass = "form.ng-dirty>div:nth-child(11)>div>span>span"
    private static final String addImport = "div.search-lg>p>span>a"
    private static final String search = "input#elements"
    private static final String OK = "div.messages-modal-prompt>div>div>div:nth-child(3)>button:nth-child(1)"
    private static final String clickX = "div.input-group-addon"
    private static final String table = "tr.inf-table-item-row>td:nth-child(1)"
    private static final String referenceType = "a#role_item_catalogue-element-menu-item-link"
    private static final String deleteButton = "a#delete-menu-item-link>span:nth-child(3)"
    private static final String dataType = "tr.inf-table-item-row>td:nth-child(1)>span>span>a"

    def "login to Model Catalogue and select Model"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('admin', 'admin')

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

    def "Navigate to data type page"() {
        when:
        click create
        then:
        check modalHeader contains 'Create Data Type'
    }

    def "fill the create data type form"() {
        when:
        fill nameLabel with "TESTING_DATA_TYPE"

        fill modelCatalogueId with "MET-333"

        fill description with "my description of data type${System.currentTimeMillis()}"

        and: 'select references button and save'
        click reference
        click dataClass

        and: 'import a data'
        click addImport
        fill search with("clinical Tags 0.0.1")
        remove messages

        and:
        click OK
        click clickX

        and:
        click save

        then:
        check table contains 'TESTING_DATA_TYPE'
    }

    @Ignore
    @Unroll
    def "delete the created data type"() {
        when: 'click on the created data type'
        click dataType

        and: 'navigate to the top menu and click on the reference type button'
        click referenceType

        and:
        click deleteButton

        and: 'confirmation'
        click modalPrimaryButton

        then:
        check table gone
    }
}
