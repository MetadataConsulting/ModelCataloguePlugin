package org.modelcatalogue.core.pages

import geb.Page

abstract class ModelCataloguePage extends Page {

    static content = {

        viewTitle           { $("h2") }
        subviewTitle        { $("h3:not(.ng-hide)") }

        showLoginButton     { $(".navbar-form i.glyphicon.glyphicon-log-in") }
        showLogoutButton    { $(".navbar-form i.glyphicon.glyphicon-log-out") }


        loginDialog         { $("div.login-modal-prompt") }

        username            { loginDialog.find("#username") }
        password            { loginDialog.find("#password") }
        loginButton         { loginDialog.find("button.btn-success") }

    }


    def loginAdmin() { loginUser("admin", "A!6m1n2014") }
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



}
