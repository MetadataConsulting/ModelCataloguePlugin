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
        createDateElementLink(required: false) { $('a#role_list_create-catalogue-element-menu-item-link', 0) }
        anchorElements { $("td.col-md-4>span>span>a") }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        elementByName(required: false, wait: true) { $('a', text: it) }
    }

    void createDataElement() {
        createDateElementLink.click()
    }

    boolean isAddItemIconVisible() {
        if ( addItemIcon.empty ) {
            return false
        }
        true
    }

    void selectRow(int row) {
        anchorElements[row].click()
    }

    boolean hasDataElement(String name) {
        if (elementByName(name)) {
            return true
        }
        false
    }
}
