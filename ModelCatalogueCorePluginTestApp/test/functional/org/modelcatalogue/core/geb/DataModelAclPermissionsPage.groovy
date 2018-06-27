package org.modelcatalogue.core.geb

import geb.Page

class DataModelAclPermissionsPage extends Page {

    static url = '/dataModelPermission/index'

    static at = { title == 'Data Model Permissions' }

    static content = {
        links(wait: true) { $('.panel-body a') }
        link(wait: true) { $('.panel-body a', text: contains(it)) }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    void select(String value) {
        link(value).click()
    }

}
