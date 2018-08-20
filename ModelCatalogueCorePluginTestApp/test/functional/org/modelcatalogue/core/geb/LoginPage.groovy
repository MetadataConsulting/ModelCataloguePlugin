package org.modelcatalogue.core.geb

import geb.Page

class LoginPage extends Page {

    static at = { title == 'Login' }

    static url = '/login/auth'

    static content = {
        inputUsername { $('input#username', 0) }
        inputPassword { $('input#password', 0) }
        submitButton { $('button', text: contains('Login')) }
        rememberMeCheckBox { $('.checkbox input') }
        disabledMessage { $('div.alert-danger', text: 'Sorry, your account is disabled.') }
    }

    void login(String username, String password) {
        fillUsername(username)
        fillPassword(password)
        submitButton.click()
        sleep(3_000)

    }

    void fillUsername(String username) {
        for (char c : username.toCharArray()) {
            inputUsername << "${c}".toString()
        }
    }

    void fillPassword(String password) {
        for (char c : password.toCharArray()) {
            inputPassword << "${c}".toString()
        }
    }

    boolean isAccountDisabled() {
        disabledMessage.displayed
    }

    boolean clickRememberMeCheckBox() {
        rememberMeCheckBox.click()
    }
}
