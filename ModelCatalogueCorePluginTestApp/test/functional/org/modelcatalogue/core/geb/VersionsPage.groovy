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
    }
}
