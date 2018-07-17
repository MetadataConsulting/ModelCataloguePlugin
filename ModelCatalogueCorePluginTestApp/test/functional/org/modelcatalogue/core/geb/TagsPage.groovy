package org.modelcatalogue.core.geb

import geb.Page

class TagsPage extends Page {

    static url = '/#'

    static at = { title == 'Tags' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/tags/all" : ''
    }

    static content = {
        createTagLink(required: false) { $('a#role_list_create-catalogue-element-menu-item-link', 0) }
        addItemIcon(required: false) {
            $("#role_list_create-catalogue-element-menu-item-link")
        }
        infiniteTableFooterRows { $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center") }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDownTag { $('button#role_item_catalogue-elementBtn') }
        deleteBttn(required: false) { $('a#deleteBtn') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDissable() {
        if (!deleteBttn) {
            return true
        }
        return deleteBttn?.attr("class") == "disabled"
    }

    void dataElementDropDown() {
        dataElementDropDownTag.click()
    }

    int countInfiniteTableFooterRows() {
        infiniteTableFooterRows.size()
    }

    void createTag() {
        createTagLink.click()
    }

    boolean isAddItemIconVisible() {
        if (addItemIcon.empty) {
            return false
        }
        true
    }
}
