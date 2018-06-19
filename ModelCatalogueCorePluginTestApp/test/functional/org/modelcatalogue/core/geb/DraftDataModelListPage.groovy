package org.modelcatalogue.core.geb

import geb.Page

class DraftDataModelListPage extends Page {

    static url = '/#'

    static at = { title == 'Data Models' }

    static content = {
        treeView { $('div.data-model-treeview-pane', 0).module(DataModelTreeViewModule) }
    }

}
