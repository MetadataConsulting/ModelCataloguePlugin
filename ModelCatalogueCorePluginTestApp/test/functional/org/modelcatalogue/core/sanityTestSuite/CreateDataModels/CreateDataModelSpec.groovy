package org.modelcatalogue.core.sanityTestSuite.CreateDataModels

import org.modelcatalogue.core.geb.*
import spock.lang.IgnoreIf
import spock.lang.Ignore
import static org.modelcatalogue.core.geb.Common.getModalPrimaryButton

@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
class CreateDataModelSpec extends AbstractModelCatalogueGebSpec {
    private static final CatalogueAction create = CatalogueAction.runFirst('data-models', 'create-data-model')
    private static final String deleteButton = "a#delete-menu-item-link>span:nth-child(3)"
    private static final String dataModelButton = "a#role_item_catalogue-element-menu-item-link>span:nth-child(3)"

    def "do create data model"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.with {
            name = "TESTING_DATA_MODEL"
            semanticVersion = "2.1.28"
            modelCatalogueId = "MT-234"
            check('Default Checks')
            check('Cancer Model')
            submit()
        }

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dataModel()
        dataModelPage.delete()
        dataModelPage.modalDialog.ok()

        then:
        noExceptionThrown()
    }
}
