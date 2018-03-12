package org.modelcatalogue.core.geb

import geb.Page

class DataModelPolicyListPage extends Page {

    static url = '/#/catalogue/dataModelPolicy/allx'

    static at = { title == 'Data Model Policies' }

    static content = {
        createLink { $('#role_list_create-catalogue-element-menu-item-link', 0) }
        dataModelPolicyLinks { $('a', href: contains("#/catalogue/dataModelPolicy/")) }
        nav { $('#topmenu', 0) .module(NavModule) }
    }

    int countDataModelPolicyLinks() {
        dataModelPolicyLinks.size()
    }

    void create() {
        createLink.click()
    }

}
