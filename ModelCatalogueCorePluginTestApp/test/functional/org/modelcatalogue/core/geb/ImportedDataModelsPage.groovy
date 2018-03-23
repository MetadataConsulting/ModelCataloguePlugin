package org.modelcatalogue.core.geb

import geb.Page

class ImportedDataModelsPage extends Page {

    static content = {
        footerGreenPlusButton(required: false) { $ ('tfoot span.fa-plus-circle', 0) }
    }

    boolean areCreateButtonsVisible() {
        if ( footerGreenPlusButton.empty ) {
            return false
        }
        true
    }

}
