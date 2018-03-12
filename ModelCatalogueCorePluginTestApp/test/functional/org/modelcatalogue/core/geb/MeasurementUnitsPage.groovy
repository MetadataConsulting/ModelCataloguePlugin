package org.modelcatalogue.core.geb

import geb.Page

class MeasurementUnitsPage  extends Page {

    static url = '/#'

    static at = { title == 'Measurement Units' }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/measurementUnits/all" : ''
    }

    static content = {
    }
}
