package org.modelcatalogue.core.geb

import geb.Page

class DataModelPage extends Page {

    static at = {
        title.startsWith('Activity of')
    }

    static url = '/#'

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataModel/${args[0]}/" : ''
    }

    static content = {
        activity {
            $("#activity-changes>div.inf-table-body>table>tbody>tr:nth-child(1)>td.inf-table-item-cell.ng-scope.col-md-7> span>span>code")
        }
        h3CeName { $('h3.ce-name', 0) }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        rightSideTitleH3 { $("h3:not(.ng-hide):not(.data-model-heading)", 0) }
        dataModelButton(required: false) { $('#role_item_catalogue-element-menu-item-link', 0) }
        deleteButton(required: false) { $('#delete-menu-item-link', 0) }
        modalDialog(required: false) { $('.modal-dialog', 0).module(ModalDialogModule) }
        dropdownLink(required: false) { $('a#role_item_catalogue-element-menu-item-link', 0) }
        dropdown(required: false) { $('li#role_item_catalogue-element-menu-item').module(DataModelNavModule) }
        exportLink(required: false) { $('a#role_item_export-menu-item-link') }
        exportXMLLink(required: false) { $('a#catalogue-element-export-specific-reports_12-menu-item-link') }
    }

    void isExportVisible() {
        !exportLink.empty
    }

    void exportXml() {
        exportXMLLink.click()
    }

    void export() {
        exportLink.click()
    }

    void dropdown() {
        dropdownLink.click()
    }

    void delete() {
        deleteButton.click()
    }

    void dataModel() {
        dataModelButton.click()
    }

    boolean titleContains(String query) {
        String text = h3CeName.text()
        text.contains(query)
    }

    String getRightSideTitle() {
        rightSideTitleH3.text()
    }
}
