package org.modelcatalogue.core.geb

import geb.Module

class NavModule extends Module {

    static content = {
        homeLink(required: false) { $("#home-link", 0) }
        userMenuLink(required: false) { $("#usermenu-link", 0) }
        usernameLink(required: false) { $("#username-link", 0) }
        favouriteLink(required: false) { $("#favourite-link", 0) }
        apiKeyLink(required: false) { $("#apikey-link", 0) }
        logoutLink(required: false) { $("#logout-link", 0) }
        cogMenuLink(required: false) { $("#cogmenu-link", 0) }
        usersLink(required: false) { $("#users-link", 0) }
        codeversionLink(required: false) { $("#codeversion-link", 0) }
        mappingUtilityLink(required: false) { $("#mappingutility-link", 0) }
        activityLink(required: false) { $("#activity-link", 0) }
        reindexCatalogueLink(required: false) { $("#reindexcatalogue-link", 0) }
        relationshipTypesLink(required: false) { $("#relationshiptypes-link", 0) }
        dataModelLink(required: false) { $("#datamodel-link", 0) }
        monitoringLink(required: false) { $("#monitoring-link", 0) }
        logsLink(required: false) { $("#logs-link", 0) }
        feedbacksLink(required: false) { $("#feedbacks-link", 0) }
        importMenuLink(required: false) { $("#importmenu-link", 0) }
        importExcelLink(required: false) { $("#importexcel-link", 0) }
        importOboLink(required: false) { $("#importobo-link", 0) }
        importDslLink(required: false) { $("#importdsl-link", 0) }
        importXmlLink(required: false) { $("#importxml-link", 0) }
        createDataModelLink(required: false) { $("#createdatamodel-link", 0) }
    }

    void home() {
        homeLink.click()
    }

    void userMenu() {
        userMenuLink.click()
    }

    void username() {
        usernameLink.click()
    }

    void favourite() {
        favouriteLink.click()
    }

    void apiKey() {
        apiKeyLink.click()
    }

    void logout() {
        logoutLink.click()
    }

    void cogMenu() {
        cogMenuLink.click()
    }

    void users() {
        usersLink.click()
    }

    void codeversion() {
        codeversionLink.click()
    }

    void mappingUtility() {
        mappingUtilityLink.click()
    }

    void activity() {
        activityLink.click()
    }

    void reindexCatalogue() {
        reindexCatalogueLink.click()
    }

    void relationshipTypes() {
        relationshipTypesLink.click()
    }

    void dataModel() {
        dataModelLink.click()
    }

    void monitoring() {
        monitoringLink.click()
    }

    void logs() {
        logsLink.click()
    }

    void feedbacks() {
        feedbacksLink.click()
    }

    void importMenu() {
        importMenuLink.click()
    }

    void importExcel() {
        importExcelLink.click()
    }

    void importObo() {
        importOboLink.click()
    }

    void importDsl() {
        importDslLink.click()
    }

    void importXml() {
        importXmlLink.click()
    }

    void createDataModel() {
        createDataModelLink.click()
    }
}
