package org.modelcatalogue.core.geb

import geb.Page

class DataModelPolicyListPage extends Page {

    static url = '/#/catalogue/dataModelPolicy/allx'

    static at = { title == 'Data Model Policies' }

    static content = {
        createLink { $('#role_list_create-catalogue-element-menu-item-link', 0) }
        dataModelPolicyLinks(wait: true) { String item -> $('a', href: contains("#/catalogue/dataModelPolicy/"), text: item) }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    int countDataModelPolicyLinks() {
        dataModelPolicyLinks.size()
    }

    void create() {
        createLink.click()
    }

    void selectEnumeratedTypePolicy() {
        dataModelPolicyLinks("Enumeration Checks").click()
    }

}
