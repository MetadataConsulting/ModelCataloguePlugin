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
        roleUser(required: false, cache: false) { $('input#ROLE_USER') }
        roleSupervisor(required: true) { $('input#ROLE_SUPERVISOR') }
        roleMetadataCurator(required: true) { $('input#ROLE_METADATA_CURATOR', type: "checkbox") }
        logoutLink { $('div#loginLinkContainer a') }
    }

    void clickRoles() {
        Roles.click()
    }

    void assignMetadataCuratorRole() {
        roleMetadataCurator.click()   //input tag is hidden
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
