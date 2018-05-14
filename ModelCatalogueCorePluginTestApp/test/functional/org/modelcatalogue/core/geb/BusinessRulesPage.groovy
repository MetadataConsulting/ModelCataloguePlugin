package org.modelcatalogue.core.geb

import geb.Page

class BusinessRulesPage extends Page {

    static url = '/#'

    static at = { title == 'Business Rules' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/validationRule/all" : ''
    }

    static content = {
        addItemIcon(required: false) {
            $("div.inf-table-body>table>tfoot>tr>td>table>tfoot>tr>td.text-center>span.fa-plus-circle")
        }
        expandLink { $('a.inf-cell-expand') }
        dataElementDropDown { $('button#role_item_catalogue-elementBtn') }
        deleteBttn { $('a#deleteBtn') }
    }

    void expandLinkClick() {
        expandLink.click()
    }

    Boolean isDeleteBttnDisable() {
        if (deleteBttn.attr("class") == "disabled")
            return true
        else
            false
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
}
