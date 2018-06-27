package org.modelcatalogue.core.geb

import geb.Page

class ReindexCataloguePage extends Page {

    static url = '/reindexCatalogue/index'

    static at = { title == 'Reindex Catalogue' }

    static content = {
        nav { $('#topmenu', 0).module(NavModule) }
    }
}
