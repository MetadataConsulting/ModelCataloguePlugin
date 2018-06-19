package org.modelcatalogue.core.geb

import geb.Page
import geb.module.Checkbox

class CreateMeasurementUnitsPage extends Page implements InputUtils {

    static url = '/dataModel/create'

    static at = { $('.modal-dialog').text().contains('Create Measurement Unit') }

    static content = {
        name(wait: true, required: false) { $('input#name') }
        symbol(wait: true, required: false) { $('input#symbol') }
        catalogueId(wait: true, required: false) { $('input#modelCatalogueId') }
        description(wait: true, required: false) { $('textarea#description') }
        measurementUnitButton(wait: true, required: false) { $('a#role_modal_modal-save-elementBtn') }
    }


    void setSymbol(String value) {
        fillInput(symbol, value)
    }

    void setName(String value) {
        fillInput(name, value)
    }

    void setCatalogueId(String value) {
        fillInput(catalogueId, value)
    }

    void setDescription(String value) {
        fillInput(description, value)
    }

    void submit() {
        measurementUnitButton.click()
        sleep(2000)
    }

}

