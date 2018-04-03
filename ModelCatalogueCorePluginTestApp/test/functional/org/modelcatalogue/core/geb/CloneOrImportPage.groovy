package org.modelcatalogue.core.geb

import geb.Page

class CloneOrImportPage extends Page {
    static at = { $("div.modal-header>h4")*.text().contains('Import or Clone') }

    static content = {
        cloneButton { $("div.modal-footer>form>button:nth-child(1)") }
        importButton { $("div.modal-footer>form>button:nth-child(2)") }
        cancelButton { $("div.modal-footer>form>button.btn.btn-warning") }
    }

    void allowClone() {
        cloneButton.click()
    }

    void allowImport() {
        importButton.click()
    }

    void cancel() {
        cancelButton.click()
    }
}