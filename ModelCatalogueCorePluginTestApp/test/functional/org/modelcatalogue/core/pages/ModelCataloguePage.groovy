package org.modelcatalogue.core.pages

import geb.Page

abstract class ModelCataloguePage extends Page {

    static content = {

        viewTitle           { $("h2") }
        subviewTitle        { $("h3:not(.ng-hide)") }

        showLoginButton     { $("i.glyphicon.glyphicon-log-in") }
        showLogoutButton    { $("i.glyphicon.glyphicon-log-out") }


        loginDialog         { $("div.login-modal-prompt") }

        username            { loginDialog.find("#username") }
        password            { loginDialog.find("#password") }
        loginButton         { loginDialog.find("button.btn-success") }

    }


    def loginAdmin() { loginUser("admin", "admin") }
    def loginViewer() { loginUser("viewer", "viewer") }
    def loginCurator() { loginUser("curator", "curator") }

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



}
