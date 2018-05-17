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
        firstRowLink { $('tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a') }
        rows { $('div.inf-table-body tbody tr') }
        showMoreButton { $('span.fa-plus-square-o') }
        editDataElementButton { $('a#role_item-detail_edit-catalogue-elementBtn') }
        elementsList { $('table.inf-table tbody tr') }
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

    String dataElementCreated() {
        firstRowLink.text()
    }

    void showMore() {
        showMoreButton.click()
    }

    boolean editDataElementDisabled() {
        waitFor { editDataElementButton.@('disabled') }
    }

    void selectDataElement(String value) {
        rows.$('a', text: value).click()
    }

    boolean containsElement(String value) {
        elementsList.$('a', text: value).displayed
    }
}
