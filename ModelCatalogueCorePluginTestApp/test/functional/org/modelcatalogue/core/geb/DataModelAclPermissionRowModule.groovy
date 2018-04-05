package org.modelcatalogue.core.geb

import geb.Module

class DataModelAclPermissionRowModule extends Module {

    static content = {
        usernameCell { $('td', 0) }
        permissionCell { $('td', 0) }
        deletButton { $('input', value: 'Delete') }
    }

    String getUsername() {
        usernameCell.text()
    }

    void delete() {
        deletButton.click()
    }

    String getPermission() {
        permissionCell.text()
    }
}
