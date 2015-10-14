package org.modelcatalogue.core.pages

class MeasurementUnitListPage extends ModelCataloguePage {

    static url = "#/catalogue/measurementUnit/all"

    static at = {
        url == "#/catalogue/measurementUnit/all"
    }

    static content = {
        viewTitle(required: false)          { $("h2") }
        dataWizard          { $('div.basic-edit-modal-prompt') }
        classifications     { dataWizard.find('input[id=dataModel]') }
    }
}
