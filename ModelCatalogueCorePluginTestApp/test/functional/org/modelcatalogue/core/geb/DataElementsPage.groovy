package org.modelcatalogue.core.geb

import geb.Page

class DataElementsPage extends Page {

    static url = '/#'

    static at = { title == 'Data Elements' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataElement/all" : ''
    }

    static content = {
        anchorElements { $("td.col-md-4>span>span>a") }
    }

    void selectRow(int row) {
        anchorElements[row].click()
    }
}
