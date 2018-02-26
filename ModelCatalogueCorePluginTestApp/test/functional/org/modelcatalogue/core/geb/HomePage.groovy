package org.modelcatalogue.core.geb

import geb.Page

class HomePage extends Page {

    static at = { title == 'Model Catalogue'}

    static url = '/'

    static content = {
        loginButton { $('button.btn', text: 'Login') }
        signupButton { $('button.btn', text: 'Sign Up') }
        footer { $( 'footer.site-footer', 0).module(DevelopmentSupportModule) }
    }

    void login() {
        loginButton.click()
    }

    void signup() {
        signupButton.click()
    }
}
