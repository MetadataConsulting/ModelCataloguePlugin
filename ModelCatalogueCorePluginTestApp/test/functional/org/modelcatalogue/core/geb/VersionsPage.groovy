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
        versionSelect(wait: true) { $('a.preserve-new-lines.ng-binding', text: it).siblings('a') }
        expandLink(wait: true) { $('a.inf-cell-expand') }
        dataElementDropDown(wait: true) { $('button#role_item_catalogue-elementBtn') }
        deleteBttn(wait: true) { $('a#deleteBtn') }
        deleteConfirmationBttn(wait: true) { $('button.btn.btn-primary') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        showMoreButton { $('span.fa-plus-square-o') }
        editButton(required: false) { $('#role_item-detail_edit-catalogue-elementBtn') }
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

    void showMore() {
        showMoreButton.click()
    }

    boolean editButtonVisible() {
        if (editButton.empty) {
            return false
        }
        true
    }

    void selectModelByVersion(String version) {
        rows.$('td a', text: version).click()
    }
}
