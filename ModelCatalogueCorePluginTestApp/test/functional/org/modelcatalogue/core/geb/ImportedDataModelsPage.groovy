package org.modelcatalogue.core.geb

import geb.Page

class ImportedDataModelsPage extends Page {

    static at = { title.startsWith 'Imports of' }

    static content = {
        footerGreenPlusButton(required: false) { $('tfoot span.fa-plus-circle', 0) }
        searchMoreButton { $('span.input-group-addon.search-for-more-icon') }
        dataModelList(required: false, wait: true) { $('h4.list-group-item-heading') }
        createRelationshipButton(required: false, wait: true) { $('button.btn.btn-primary', type: 'submit') }
        cancelButton(required: true) { $('button.btn.btn-warning') }
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

    boolean areCreateButtonsVisible() {
        if (footerGreenPlusButton.empty) {
            return false
        }
        true
    }

    void importDataModel() {
        footerGreenPlusButton.click()
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void selectDataModel(int index) {
        dataModelList[index].click()
    }

    void createRelationship() {
        createRelationshipButton.click()
    }
}
