package org.modelcatalogue.core.geb

import geb.Page

class CloneIntoDataModulePage extends Page {
    static at = { $("div.modal-header>h4").text().contains('Clone into') }

    static content = {
        searchMore(wait: true) { $('span.ng-isolate-scope.fa.fa-fw.fa-file-o') }
        cloneModalButton(wait: true) { $('button.btn.btn-primary', text: 'OK') }
    }

    void cloneModal() {
        cloneModalButton.click()
    }

    void listAllDataModels() {
        searchMore.click()
    }
}