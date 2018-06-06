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
        dataElements { $("td.col-md-4>span>span>a",text:it) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        firstRowLink { $('tbody.ng-scope>tr:nth-child(1)>td:nth-child(1)>span>span>a') }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDown { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
        rows { $('div.inf-table-body tbody tr') }
        elementByName(wait: true) { $('a', text: contains(it)) }
    }

    void createDataElement() {
        createDateElementLink.click()
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDisable() {
        deleteBttn.attr("class") == "disabled"
    }

    void dataElementDropDown() {
        dataElementDropDown.click()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
            return false
        }
        true
    }
    boolean iscreateDateElementLinkVisible() {
        if ( createDateElementLink.empty ) {
            return false
        }
        true
    }

    void selectRow(int row) {
        anchorElements[row].click()
    }

    void selectdataElements(String value) {
        dataElements(value).click()
    }

    String dataElementCreated() {
        firstRowLink.text()
    }

    void selectDataElement(String value) {
        rows.$('a', text: value).click()
    }

    boolean hasDataElement(String name) {
        elementByName(name).displayed
    }
}
