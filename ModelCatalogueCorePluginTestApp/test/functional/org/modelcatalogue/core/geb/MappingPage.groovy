package org.modelcatalogue.core.geb

import geb.Page

class MappingPage extends Page {

    static url = '/batch/all'

    static at = { title == 'Mapping Batches' }

    static content = {
        generateMappingButton { $('.page-header a', text: "Generate Mappings") }
        nav { $('#topmenu', 0).module(NavModule) }
        mappingList(wait: true, required: false) { $('tr td a', text: contains(it)) }
        mappingSuccessMesg(required: false, wait: true) { $('.alert.alert-info p') }
    }

    void generateMapping() {
        generateMappingButton.click()
    }

    void hasMapping(String value1,String value2) {
        mappingList(value2)?.click()
    }
}
