package org.modelcatalogue.core.geb

import geb.Module

class ModalDialogModule extends Module {

    static content = {
        modalPrimaryButton { $('.btn-primary', 0) }
    }

    void ok() {
        modalPrimaryButton.click()
    }
}
