package org.modelcatalogue.core.geb

import geb.Page

class DataModelPermissionListPage extends Page {

    static url = '/dataModelPermission/index'

    static at = { title == "Data Model Permissions" }

    static content = {
        modalList { $('table tbody a', text: contains(it)) }
    }

    void selectDataModal(String dataModalName) {
        modalList(dataModalName).click()
    }

}
