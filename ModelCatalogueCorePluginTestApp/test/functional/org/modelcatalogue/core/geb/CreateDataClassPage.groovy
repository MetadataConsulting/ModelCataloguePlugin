package org.modelcatalogue.core.geb

import geb.Page

class CreateDataClassPage extends Page implements InputUtils {
    static at = { $('.modal-dialog').text().contains('Data Class Wizard') }

    static content = {
        nameInput { $('#name', 0) }
        descriptionInput { $('#description', 0) }
        modelCatalogueIdInput { $('#modelCatalogueId', 0) }
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void setModelCatalogueId(String value) {
        fillInput(modelCatalogueIdInput, value)
    }
    void setDescription(String value) {
        fillInput(descriptionInput, value)
    }
}
