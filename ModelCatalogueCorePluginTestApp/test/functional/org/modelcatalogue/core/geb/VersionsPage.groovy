package org.modelcatalogue.core.geb

import geb.Page

class VersionsPage extends Page {

    static url = '/#'

    static at = { title.startsWith('History of') }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/versions/all" : ''
    }

    static content = {
        rows { $('#history-tab tbody tr') }
        versionSelect (wait:true){ $('a.preserve-new-lines.ng-binding.ng-scope', text: it).siblings('a') }
        expandLink (wait:true){ $('a.inf-cell-expand') }
        dataElementDropDown (wait:true){ $('button#role_item_catalogue-elementBtn') }
        deleteBttn(wait:true) { $('a#deleteBtn') }
        deleteConfirmationBttn(wait:true) { $('button.btn.btn-primary') }
    }

    boolean rowsContainText(String text) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows[i].text().contains(text)) {
                return true
            }
        }
        false
    }

    void expandLinkClick() {
        expandLink.click()
    }

    void selectVersion(String version) {
        versionSelect(version).click()
    }

    void dataElementDropDownClick() {
        dataElementDropDown.click()
    }

    void deleteBttnClick() {
        deleteBttn.click()
    }

    void deleteConfirmationBttnClick() {
        deleteConfirmationBttn.click()
    }

}
