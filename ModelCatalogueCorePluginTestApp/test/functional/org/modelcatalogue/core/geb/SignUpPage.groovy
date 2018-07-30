package org.modelcatalogue.core.geb

import geb.Page

class SignUpPage extends Page {

    static url = '/'

    static at = { title == 'Model Catalogue' }

    static content = {
        signUp(wait: true, required: false) { $('.ng-pristine a') }
    }

    def clickSignUp() {
        signUp.click()
    }

}
