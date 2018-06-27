package org.modelcatalogue.core.geb

import geb.Module

class NavModule extends Module {

    static content = {
        homeLink(required: false, wait: true) { $("#home-link", 0) }
        userMenuLink(required: false, wait: true) { $("#usermenu-link", 0) }
        dataModelPermissionLink(required: false, wait: true) { $("#dataModelPermission-link", 0) }
        usernameLink(required: false, wait: true) { $("#username-link", 0) }
        favouriteLink(required: false, wait: true) { $("#favourite-link", 0) }
        apiKeyLink(required: false, wait: true) { $("#apikey-link", 0) }
        logoutLink(required: false, wait: true) { $("#logout-link", 0) }
        cogMenuLink(required: false, wait: true) { $("#cogmenu-link", 0) }
        usersLink(required: false, wait: true) { $("#users-link", 0) }
        codeversionLink(required: false, wait: true) { $("#codeversion-link", 0) }
        mappingUtilityLink(required: false, wait: true) { $("#mappingutility-link", 0) }
        activityLink(required: false, wait: true) { $("#activity-link", 0) }
        reindexCatalogueLink(required: false, wait: true) { $("#reindexcatalogue-link", 0) }
        relationshipTypesLink(required: false, wait: true) { $("#relationshiptypes-link", 0) }
        dataModelPolicyLink(required: false, wait: true) { $("#datamodelpolicy-link", 0) }
        monitoringLink(required: false, wait: true) { $("#monitoring-menu-item-link", 0) }
        logsLink(required: false, wait: true) { $("#logs-archive-menu-item", 0) }
        feedbacksLink(required: false, wait: true) { $("#feedbacks-link", 0) }
        importMenuLink(required: false, wait: true) { $("#importmenu-link", 0) }
        importExcelLink(required: false, wait: true) { $("#importexcel-link", 0) }
        importOboLink(required: false, wait: true) { $("#importobo-link", 0) }
        importDslLink(required: false, wait: true) { $("#importdsl-link", 0) }
        importXmlLink(required: false, wait: true) { $("#importxml-link", 0) }
        createDataModelLink(required: false, wait: true) { $("#createdatamodel-link", 0) }
        settingDropDownTag(required: false, wait: true) { $("#role_navigation-right_admin-menu-menu-item-link", 0) }
    }

    void dataModelPermission() {
        dataModelPermissionLink.click()
    }

    void settingDropDown() {
        settingDropDownTag.click()
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

    void dataModelPolicies() {
        dataModelPolicyLink.click()
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

    Integer userdropdownLength() {
        cogMenuLink.siblings('ul').$('li').size()
    }

    boolean codeVersionIsVisible() {
        codeversionLink.displayed
    }

    boolean relationshiptypeIsVisible() {
        relationshipTypesLink.displayed
    }

    boolean datamodelpolicyIsVisible() {
        dataModelPolicyLink.displayed
    }

    boolean feedbacksIsVisible() {
        feedbacksLink.displayed
    }
}
