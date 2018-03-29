package org.modelcatalogue.core.geb

import geb.Page

class DataImportPage extends Page implements InputUtils{

    static at = { $("#type").displayed }

    static content = {
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        search(wait: true) { $("input#element", 0) }
        searchMoreLink(wait: true) { $("a.show-more-cep-item", 0) }
        submitButton(wait: true) { $("div.modal-footer>button.btn-primary") }
    }

    void addItem() {
        addItemIcon.click()
    }

    void fillSearchBox(String searchText) {
        fillInput(search, searchText)
    }

    void searchMore() {
        searchMoreLink.click()
    }

    void finish() {
        submitButton.click()
    }
}
