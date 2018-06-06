package org.modelcatalogue.core.geb

import geb.Module

class ModalFooterModule extends Module {

    static content = {
        saveLink(wait: true) { $('a#role_modal_modal-save-elementBtn', 0) }
        saveAndCreateAnotherLink { $('a#role_modal_modal-save-and-add-anotherBtn', 0) }
        cancelLink { $('a#role_modal_modal-cancelBtn', 0) }
    }

    void cancel() {
        cancelLink.click()
    }

    void saveAndCreateAnother() {
        saveAndCreateAnotherLink.click()
    }

    void save() {
        saveLink.click()
        sleep(2000)
    }
}
