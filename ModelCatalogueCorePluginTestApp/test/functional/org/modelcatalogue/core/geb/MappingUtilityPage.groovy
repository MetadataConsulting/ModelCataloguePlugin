package org.modelcatalogue.core.geb

import geb.Page

class MappingUtilityPage extends Page {
    static url = '/batch/all'

    static at = { title == 'Mapping Batches' }

    static content = {
        links { $('.panel-body a') }
        nav { $('#topmenu', 0).module(NavModule) }
    }

}
