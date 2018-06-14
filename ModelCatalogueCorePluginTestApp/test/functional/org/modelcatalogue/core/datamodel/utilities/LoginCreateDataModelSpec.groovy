package org.modelcatalogue.core.datamodel.utilities

import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Login and then Create Data Model.
 */
@Stepwise
abstract class LoginCreateDataModelSpec extends LoginSpec {

    @Shared
    String dataModelName = (String) "NEW_TESTING_MODEL_${UUID.randomUUID().toString()}"
    @Shared
    String dataModelDescription = "TESTING_MODEL_DESCRIPTION"

    /**
     * Leads on from LoginSpec (where you login as some user)
     */
    def "create a data model"() {
        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.nav.createDataModel()
        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = browser.page CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.description = dataModelDescription
        createDataModelPage.modelCatalogueIdInput = UUID.randomUUID().toString()
        createDataModelPage.submit()
        then:
        at DataModelPage
    }
}
