package org.modelcatalogue.core.geb

import geb.Page

class AssetPage extends Page {

    static at = { title.startsWith('History of Import for') }

    @Override
    String convertToPath(Object[] args) {
        args?.size() >= 2 ? "/#/${args[0]}/asset/${args[1]}" : ''
    }

    static content = {
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
        showMoreButton { $('a.inf-cell-expand') }
        downloadAssetLink(required: false, wait: true) { $('a', text: "Download Asset") }
    }

    void showMore() {
        showMoreButton.click()
    }

    void downloadAsset() {
        downloadAssetLink.click()
    }
}
