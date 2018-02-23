package org.modelcatalogue.core.sanityTestSuite.LandingPage

import org.modelcatalogue.core.geb.DataElementPage
import org.modelcatalogue.core.geb.DataElementsPage
import org.modelcatalogue.core.geb.DataModelListPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Ignore
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteA')  })
@Stepwise
class EditDataElementSpec extends AbstractModelCatalogueGebSpec {

    private static final String Element="td.col-md-4>span>span>a"
    private static final String editButton="a#role_item-detail_inline-editBtn"
    private static final String  description="#metadataCurator > div.container-fluid.container-main > div > div > div.ng-scope > div > div.split-view-right.data-model-detail-pane > ui-view > ui-view > div > div > div > div > form > div:nth-child(4) > div > ng-include > div > div > span > div > textarea"
    private static final String dataType="input#dataType"
    private static final String submit="button#role_item-detail_inline-edit-submitBtn"
    private static final String change="#history-changes > div.inf-table-body > table > tbody > tr:nth-child(1) > td.inf-table-item-cell.ng-scope.col-md-8 > span > span > code"

    def "login to model catalogue and select a draft model"() {
        when:
        to LoginPage

        then:
        at LoginPage

        when:
        LoginPage loginPage = browser.page LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DataModelListPage

        when:
        DataModelListPage dataModelListPage = browser.page DataModelListPage
        dataModelListPage.search('Test 3')
        dataModelListPage.select('Test 3')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Elements')

        then:
        at DataElementsPage
    }

    def "select a data element"() {

        when:
        DataElementsPage dataElementsPage = browser.page DataElementsPage
        dataElementsPage.selectRow(0)

        then:
        at DataElementPage

        when:
        DataElementPage dataElementPage = browser.page DataElementPage

        then:
        dataElementPage.editButton.isDisplayed()
    }

    @Ignore
    def "edit description ,data type and save"() {
        when:
        DataElementPage dataElementPage = browser.page DataElementPage
        dataElementPage.edit()
        dataElementPage.description = '.i am describe my action'
        fill dataType with "var 1234"
        dataElementPage.submit()

        then:
        $('span.unit-name').text() == "var 1234 "
    }
}
