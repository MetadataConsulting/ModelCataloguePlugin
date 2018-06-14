package org.modelcatalogue.core.datamodel

import geb.Browser
import geb.Page
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.FinalizeDataModelPage
import org.modelcatalogue.core.geb.FinalizedDataModelPage

trait FinalizeDataModel {

    public <T extends Page> void throwExceptionIfNotAt(Browser browser, Class<T> pageClass) {
        if (!browser.at(pageClass)) {
            throw new Exception("Browser not at page ${pageClass}")
        }
    }

    void finalizeDataModel(Browser browser, String dataModelVersion) {
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.dropdown()
//        then:
        throwExceptionIfNotAt(browser, DataModelPage)

//        when:
        dataModelPage = browser.page DataModelPage
        dataModelPage.finalizedDataModel()
//        then:
        throwExceptionIfNotAt(browser, FinalizeDataModelPage)

//        when:
        FinalizeDataModelPage finalizeDataModelPage = browser.page FinalizeDataModelPage
        finalizeDataModelPage.version = dataModelVersion
        finalizeDataModelPage.versionNote = "Version ${dataModelVersion}"
        finalizeDataModelPage.submit()
//        then:
        throwExceptionIfNotAt(browser, FinalizedDataModelPage)

//        when:
        FinalizedDataModelPage finalizedDataModelPage = browser.page FinalizedDataModelPage
        finalizedDataModelPage.hideConfirmation()
    }

}
