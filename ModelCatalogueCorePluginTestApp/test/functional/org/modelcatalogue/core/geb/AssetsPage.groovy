package org.modelcatalogue.core.geb

import geb.Page

class AssetsPage extends Page {

    static url = '/#'

    static at = { title == 'Assets' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/assets/all" : ''
    }

    static content = {
        addItemIcon(required: false) { $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle") }
    }

    boolean isAddItemIconVisible() {
        if ( addItemIcon.empty ) {
            return false
        }
        true
    }
}
