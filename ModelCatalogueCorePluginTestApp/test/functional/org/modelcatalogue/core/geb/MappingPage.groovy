package org.modelcatalogue.core.geb

import geb.Page

class MappingPage extends Page {

    static url = '/batch/all'

    static at = { title == 'Mapping Batches' }

    static content = {
        generateMappingButton { $('.page-header a', text: "Generate Mappings") }
        nav { $('#topmenu', 0).module(NavModule) }
        mappingList { $('form tbody tr') }
    }

    void generateMapping() {
        generateMappingButton.click()
    }

    void hasMapping(String value1, String value2) {
        mappingList.$('td a', text: contains(value1)).click()
    }

}
