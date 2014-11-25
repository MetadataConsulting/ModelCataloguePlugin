package org.modelcatalogue.core.pages

import geb.Page
import geb.navigator.Navigator

abstract class ModelCataloguePage extends Page {

    static content = {

        viewTitle           { $("h2") }
        subviewTitle        { $("h3:not(.ng-hide)") }
        subviewStatus       { $("h3 small span.label") }

        showLoginButton     { $(".navbar-form i.glyphicon.glyphicon-log-in") }
        showLogoutButton    { $(".navbar-form i.glyphicon.glyphicon-log-out") }


        loginDialog         { $("div.login-modal-prompt") }

        username            { loginDialog.find("#username") }
        password            { loginDialog.find("#password") }
        loginButton         { loginDialog.find("button.btn-success") }

        confirmDialog       { $('.modal.messages-modal-confirm') }
        confirmOk           { $('.modal.messages-modal-confirm .btn-primary') }

    }


    def loginAdmin() { loginUser("admin", "A6m1n2014") }
    def loginViewer() { loginUser("viewer", "v13w3r") }
    def loginCurator() { loginUser("curator", "c2r4t0r") }

    def loginUser(String user, String pwd) {
        if (!showLoginButton.displayed) {
            showLogoutButton.click()
        }

        showLoginButton.click()

        waitFor {
            loginDialog.displayed
        }

        username = user
        password = pwd
        loginButton.click()
    }

    /**
     * Selects the first item from catalogue element picker if any element is found.
     * returns true if the element was selected, false otherwise
     */
    boolean selectCepItemIfExists() {
        try {
            waitFor(3) {
                $('.cep-item').displayed
            }
            $('.cep-item').click()
            return true
        } catch (ignored) {
            return false
        }

    }

    Navigator actionButton(String id, String role = "item") {
        $('#role_' + role + '_' + id + 'Btn')
    }

}
