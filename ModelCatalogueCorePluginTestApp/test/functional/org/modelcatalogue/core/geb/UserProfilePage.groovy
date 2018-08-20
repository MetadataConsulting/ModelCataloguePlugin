package org.modelcatalogue.core.geb

import geb.Page

class UserProfilePage extends Page {

    static at = {
        title.startsWith('Properties of')
    }

    static url = '/#/catalogue/user'

    static content = {
        disableOrEnableUserButton(wait: true) { $('#role_item-detail_update-userBtn') }
        nav { $('div.navbar-collapse', 0).module(NavModuleAdmin) }
    }

    void disableOrEnableUser() {
        disableOrEnableUserButton.click()
    }
}
