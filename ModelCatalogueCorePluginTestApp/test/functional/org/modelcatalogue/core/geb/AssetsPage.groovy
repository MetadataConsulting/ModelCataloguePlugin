package org.modelcatalogue.core.geb

import geb.Page

class AssetsPage extends Page {

    static url = '/#'

    static at = { title == 'Assets' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/assets/all" : ''
    }

    static content = {
    }
}
