package org.modelcatalogue.core.geb

import geb.Page

class LoginModalPage extends Page {

    static at = { $("a.btn-block").text() == "Login with Google" }

    static content = {
        cancelButton { $("button.btn-warning", 0) }
        usernameInput { $("input#username", 0) }
        passwordInput { $("input#password", 0) }
    }

    void setUsername(String value) {
        fillInput(usernameInput, value)
    }

    void setPassword(String value) {
        fillInput(passwordInput, value)

    }

    void cancel() {
        cancelButton.click()
    }

    void fillInput(def input, String value) {
        for ( char c : value.toCharArray() ) {
            input << "${c}".toString()
        }
    }
}
