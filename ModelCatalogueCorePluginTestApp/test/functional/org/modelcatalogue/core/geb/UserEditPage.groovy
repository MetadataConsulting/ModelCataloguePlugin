package org.modelcatalogue.core.geb

import geb.Page

class UserEditPage extends Page {

    static url = '/userAdmin/edit'

    static at = { title == 'Edit User' }

    static content = {
        userDetails { $('div#tabs ul li a', text: "User Details") }
        Roles { $('div#tabs ul li a', text: "Roles") }
        updateButton { $('a#update') }
        deleteButton { $('a#deleteButton') }
        roleUser { $('span.jquery-safari-checkbox', 4) }
        roleSupervisor { $('span.jquery-safari-checkbox', 6) }
        roleMetadataCurator(wait: true) { $('span.jquery-safari-checkbox', 5) }
        logoutLink { $('div#loginLinkContainer a') }
    }

    void clickRoles() {
        Roles.click()
    }

    void assignMetadataCuratorRole() {
        roleMetadataCurator.click()
    }

    void update() {
        updateButton.click()
    }

    void updateUser() {
        updateButton.click()
    }

    void logout() {
        logoutLink.click()
    }

}
