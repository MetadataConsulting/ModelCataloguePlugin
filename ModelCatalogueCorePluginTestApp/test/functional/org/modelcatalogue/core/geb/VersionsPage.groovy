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
