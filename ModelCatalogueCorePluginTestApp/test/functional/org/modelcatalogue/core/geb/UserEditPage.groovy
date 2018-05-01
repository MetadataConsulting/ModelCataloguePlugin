package org.modelcatalogue.core.geb

import geb.Page
import org.modelcatalogue.core.security.MetadataRoles

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

    private void grantMetadataCuratorRole() {
        roleMetadataCurator.click()
    }

    private void grantSupervisorRole() {
        roleSupervisor.click()
    }

    private void grantUserRole() {
        roleUser.click()
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

    void grant(String role) {
        if ( role == MetadataRoles.ROLE_SUPERVISOR ) {
            grantSupervisorRole()

        } else if ( role == MetadataRoles.ROLE_CURATOR ) {
            grantMetadataCuratorRole()

        } else if ( role == MetadataRoles.ROLE_USER ) {
            grantUserRole()
        }
    }

    boolean userRoleGranted() {
        roleUser.@('class').contains("jquery-safari-checkbox-checked")
    }

    boolean metadatacuratorRoleGranted() {
        roleMetadataCurator.@('class').contains("jquery-safari-checkbox-checked")
    }

    boolean supervisorRoleGranted() {
        roleSupervisor.@('class').contains("jquery-safari-checkbox-checked")
    }
}
