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


    // keep the passwords simply stupid, they are only for dev/test or very first setup
    // sauce labs connector for some reason fails with the six in the input
    def loginAdmin() { loginUser("admin", "admin") }
    def loginViewer() { loginUser("viewer", "viewer") }
    def loginCurator() { loginUser("curator", "creator") }

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

    /**
     * @param row number of row starting 1
     * @param column number of column starting 1
     * @return given cell
     */
    Navigator infTableCell(Map attrs = [:], int row, int column) {
        $(attrs, 'div.inf-table-body tbody tr:nth-child(' + row +') td:nth-child(' + column + ')')
    }

}
