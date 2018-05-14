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
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        rows { $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center") }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDown { $('button#role_item_catalogue-elementBtn') }
        deleteBttn(required: false) { $('a#deleteBtn') }
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDissable() {
        if (!deleteBttn || deleteBttn?.attr("class") == "disabled") {
            return true
        }
        false
    }

    void dataElementDropDown() {
        dataElementDropDown.click()
    }

    int count() {
        rows.size()
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
