package org.modelcatalogue.core.geb

import geb.Page

class DataClassesPage extends Page {

    static url = '/#'

    static at = { title == 'Data Classes' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/dataClasses/all" : ''
    }

    static content = {
    }
}
