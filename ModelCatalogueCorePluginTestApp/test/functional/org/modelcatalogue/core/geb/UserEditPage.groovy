package org.modelcatalogue.core.geb

import geb.Page

class UserEditPage extends Page {

    static url = '/userAdmin/edit'

    static at = { title == 'Edit User' }

    static content = {
        userDetails { $('div#tabs ul li a', text: "User Details") }
        rolesTabLink { $('div#tabs ul li a', text: "Roles") }
        updateButton { $('a#update') }
        deleteButton { $('a#deleteButton') }
        roleUser { $('div a', text: "ROLE_USER").siblings("span") }
        roleSupervisor { $('div a', text: "ROLE_SUPERVISOR").siblings("span") }
        roleMetadataCurator(wait: true) { $('div a', text: "ROLE_METADATA_CURATOR").siblings("span") }
        logoutLink { $('div#loginLinkContainer a') }
    }

    void clickRoles() {
        rolesTabLink.click()
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
