package org.modelcatalogue.core.geb

import geb.Page
import geb.module.Checkbox

class CreateAssetsPage extends Page implements InputUtils {

    static url = '/dataModel/create'

    static at = { $('.modal-dialog').text().contains('Create Asset') }

    static content = {
        name(wait: true, required: false) { $('input#name') }
        inputFile { $('input', type: 'file') }
        description(wait: true, required: false) { $('textarea#description') }
        submitButton(wait: true, required: false) { $('a#role_modal_modal-save-elementBtn') }

    }

    void setName(String value) {
        fillInput(name, value)
    }

    void setDescription(String value) {
        fillInput(description, value)
    }

    void submit() {
        submitButton.click()
        sleep(2000)
    }

    void upload(String absolutePath) {
        inputFile = absolutePath
    }

}

