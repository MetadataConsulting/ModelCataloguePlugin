package org.modelcatalogue.core.geb

import geb.Page

class DataImportPage extends Page implements InputUtils {
    static at = { $("h4").text().contains("Import") }

    static content = {
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        search(wait: true) { $("input#elements", 0) }
        searchMoreLink(wait: true) { $("span.search-for-more-icon", 0) }
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
        sleep(2000)
        submitButton.click()
        sleep(2000)
    }
}

