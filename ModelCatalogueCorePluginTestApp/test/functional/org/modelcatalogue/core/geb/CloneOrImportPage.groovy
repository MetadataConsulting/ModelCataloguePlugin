package org.modelcatalogue.core.geb

import geb.Page

class CloneOrImportPage extends Page {
    static at = { $("div.modal-header>h4")*.text().contains('Import or Clone') }

    static content = {
        cloneButton(wait: true) { $("div.modal-footer>form>button:nth-child(1)") }
        importButton(wait: true) { $("div.modal-footer>form>button:nth-child(2)") }
        cancelButton(wait: true) { $("div.modal-footer>form>button.btn.btn-warning") }
    }

    void allowClone() {
        cloneButton.click()
        sleep(2_000)
    }

    void allowImport() {
        importButton.click()
    }

    void cancel() {
        cancelButton.click()
    }
}