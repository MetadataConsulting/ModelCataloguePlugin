package org.modelcatalogue.core.geb

import geb.Page

class LoginPage extends Page {

    static at = { title == 'Login' }

    static url = '/login/auth'

    static content = {
        inputUsername { $('input#username', 0) }
        inputPassword { $('input#password', 0) }
        submitButton { $('button', text: contains('Login')) }
    }

    void login(String username, String password) {
        fillUsername(username)
        fillPassword(password)
        submitButton.click()

    }

    void fillUsername(String username) {
        for ( char c : username.toCharArray() ) {
            inputUsername << "${c}".toString()
        }
    }

    void fillPassword(String password) {
        for ( char c : password.toCharArray() ) {
            inputPassword << "${c}".toString()
        }
    }
}
