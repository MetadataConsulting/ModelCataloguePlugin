package org.modelcatalogue.core.geb

import geb.Page

class UserProfilePage extends Page {

    static at = {
        title.startsWith('Properties of')
    }

    static url = '/#/catalogue/user'

    static content = {
        disableUserButton { $('#role_item-detail_update-userBtn') }
    }

    void disableUser() {
        disableUserButton.click()
    }

}
