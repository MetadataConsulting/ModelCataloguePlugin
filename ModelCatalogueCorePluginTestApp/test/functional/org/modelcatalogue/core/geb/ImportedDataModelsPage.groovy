package org.modelcatalogue.core.geb

import geb.Page

class ImportedDataModelsPage extends Page {

    static at = { title.startsWith 'Imports of' }

    static content = {
        footerGreenPlusButton(required: false) { $('tfoot span.fa-plus-circle', 0) }
        treeView(wait: true) { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        dataModelList(wait: true) { $('table.inf-table tr') }
    }

    boolean areCreateButtonsVisible() {
        if (footerGreenPlusButton.empty) {
            return false
        }
        true
    }

    void importDataModel() {
        footerGreenPlusButton.click()
    }

    void selectModelByName(String name) {
        dataModelList.$('td a', text: name).click()
    }
}
