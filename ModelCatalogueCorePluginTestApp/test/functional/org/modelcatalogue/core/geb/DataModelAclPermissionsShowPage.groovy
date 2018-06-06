package org.modelcatalogue.core.geb

import geb.Page
import geb.module.Select

class DataModelAclPermissionsShowPage extends Page {

    static at = { title == 'Show Data Model Permissions' }

    static url = '/dataModelPermission/show/'

    String convertToPath(Object[] args) {
        args ? '/' + args[0] : ""
    }

    static content = {
        breadcrumbs { $('ol.breadcrumb').module(BreadcrumbModule) }
        rows { $('.panel-body tbody tr') }
        row { $('.panel-body tr', it as int).module(DataModelAclPermissionRowModule) }
        grantForm { $('form#grantForm', 0) }
        usernameSelect { $('select#username option', value: it) }
        permissionSelect { $('select#permission option', value: it) }
        inputSubmit { $('input', type: 'submit', value: 'Grant', 0) }
        nav { $('#topmenu', 0).module(NavModule) }
    }

    int count() {
        rows.size()
    }

    void setUsername(String value) {
        usernameSelect(value).click()
    }

    void setPermission(String value) {
        permissionSelect(value).click()
    }

    void grant(String username, String permission) {
        setUsername(username)
        setPermission(permission)
        inputSubmit.click()
    }
}
