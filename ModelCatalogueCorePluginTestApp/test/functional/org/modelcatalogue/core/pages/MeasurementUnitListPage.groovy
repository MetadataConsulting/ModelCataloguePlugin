package org.modelcatalogue.core.pages

/**
 * Created by david on 04/11/14.
 */
class MeasurementUnitListPage extends ModelCataloguePage {

    static url = "#/catalogue/measurementUnit/all"

    static at = {
        url == "#/catalogue/measurementUnit/all"
    }

    static content = {
        viewTitle(required: false)          { $("h2") }
    }
}
