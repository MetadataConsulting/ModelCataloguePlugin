package org.modelcatalogue.core.geb

import geb.Page

class DataModelPermissionGrantPage extends Page {

    static url = 'dataModelPermission/show'

    static at = { title == "Data Model Permissions" }

    static content = {
        username { $('select#username option', value: it) }
        permission { $('select#permission option', value: it) }
        grantPermissionButton { $('input', name: "_action_Grant") }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    void selectUsername(String value) {
        username(value).click()
    }

    void selectPermission(String value) {
        permission(value).click()
    }

    void grantPermission() {
        grantPermissionButton.click()
    }

}
