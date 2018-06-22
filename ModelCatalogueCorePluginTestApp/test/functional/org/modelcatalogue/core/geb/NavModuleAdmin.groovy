package org.modelcatalogue.core.geb

import geb.Module

class NavModuleAdmin extends Module {

    static content = {
        newRealtionshipButton { $('#role_list_create-catalogue-element-menu-item-link') }
        searchButton { $('#role_navigation-right_search-menu-menu-item-link') }
        createDataModelButton { $('#role_navigation-right_create-data-model-menu-item-link') }
        curatorMenuDropdownLink { $('#role_navigation-right_curator-menu-menu-item-link') }
        importExcel { $('#import-excel-menu-item-link') }
        importOBO { $('#import-obo-menu-item-link') }
        importDSL { $('#import-dsl-menu-item-link') }
        importXML { $('#import-xml-menu-item-link') }
        adminMenuDropdownLink(wait: true) { $('#role_navigation-right_admin-menu-menu-item-link') }
        usersLink { $('#user-super-admin-menu-item-link') }
        dataModelPermissionLink(required: false, wait: true) { $('#datamodelpermission-admin-menu-item-link') }
        codeVersionLink { $('#code-version-menu-item-link') }
        mappingUtilityLink { $('#action-batches-menu-item-link') }
        activityLink { $('#user-last-seen-menu-item-link') }
        reindexCatalogueLink { $('#reindex-catalogue-menu-item-link') }
        relationshipTypesLink { $('#relationship-types-menu-item-link') }
        dataModelPolicyLink { $('#data-model-policies-menu-item-link') }
        monitoringLink { $('#monitoring-menu-item-link') }
        logsLink { $('#logs-archive-menu-item-link') }
        feedbackLink { $('#feedbacks-menu-item') }
        userMenuDropdownLink { $('#role_navigation-right_user-menu-menu-item-link') }
        userInfoLink { $('#user-info-menu-item-link') }
        favouritesLink { $('#user-favorites-menu-item') }
        apiKeyLink { $('#user-api-key-menu-item-link') }
        logoutLink { $('#user-login-right-menu-item-link') }
    }


    void adminMenu() {
        adminMenuDropdownLink.click()
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
        feedbackLink.click()
    }

    void dataModelAcl() {
        dataModelPermissionLink.click()
    }

    void userMenu() {
        userMenuDropdownLink.click()
        sleep(1000)
    }

    void logout() {
        logoutLink.click()
        sleep(2000)
    }
}
