package org.modelcatalogue.core.geb

import geb.Page

class RegisterPage extends Page {

    static at = {
        title == 'Register'
    }

    static url = '/register/'

    static content = {
        newUsernameInput { $("input#username-new", 0) }
        newEmailInput { $("input#email-new", 0) }
        passwordInput { $("input#password", 0) }
        password2Input { $("input#password2", 0) }
        createButton { $("button.btn", 0) }
        alertDiv(required: false) { $("div.alert", 0) }
    }

    boolean isAlertDisplayed() {
        newEmailInput.@('disabled')
    }

    boolean isEmailDisabled() {
        alertDiv.isDisplayed()
    }

    void register(String username, String email, String password, String password2) {
        fillInput(newUsernameInput, username)
        fillInput(newEmailInput, email)
        fillInput(passwordInput, password)
        fillInput(password2Input, password2)
        createButton.click()
    }

    void fillInput(def input, String value) {
        for ( char c : value.toCharArray() ) {
            input << "${c}".toString()
        }
    }
}
