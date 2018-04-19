package org.modelcatalogue.core.geb

import geb.Page

class ImportOrCloneModalPage extends Page implements InputUtils {
    static at = { $('.modal-dialog', 0).text().contains('Import or Clone') }

    static content = {
        cloneButton(required: true, wait: true) {
            $('button.btn.ng-binding.ng-scope.btn-primary', text: contains("Clone"))
        }
        importButton(required: true, wait: true) {
            $('button.btn.ng-binding.ng-scope.btn-primary', text: contains("Import"))
        }
        cancelButton(required: true, wait: true) { $('button.btn.btn-warning', text: contains(" Cancel")) }
    }

    void cloneElement() {
        cloneButton.click()
    }

    void importElement() {
        importButton.click()
    }

    void cancel() {
        cancelButton.click()
    }

}
