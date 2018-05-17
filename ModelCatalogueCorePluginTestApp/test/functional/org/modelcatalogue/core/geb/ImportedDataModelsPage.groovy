package org.modelcatalogue.core.geb

import geb.Page

class ImportedDataModelsPage extends Page {

    static at = { title.startsWith 'Imports of' }

    static content = {
        footerGreenPlusButton(required: false) { $ ('tfoot span.fa-plus-circle', 0) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    boolean areCreateButtonsVisible() {
        if ( footerGreenPlusButton.empty ) {
            return false
        }
        true
    }
}
