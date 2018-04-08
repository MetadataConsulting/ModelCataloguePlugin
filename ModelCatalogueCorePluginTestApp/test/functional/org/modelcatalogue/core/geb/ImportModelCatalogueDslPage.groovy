package org.modelcatalogue.core.geb

import geb.Page

class ImportModelCatalogueDslPage extends Page implements InputUtils {

    static at = { title == 'Import Model Catalogue DSL'}

    static url = '/dataImport/dsl'

    static content = {
        inputName { $('input#modelName')}
        inputFile { $('input', type: 'file') }
        inputButton { $('input', type: 'submit') }
    }

    void upload(String name, String absolutePath) {
        fillInput(inputName, name)
        upload(absolutePath)
    }
    void upload(String absolutePath) {
        inputFile = absolutePath
        inputButton.click()
    }

}
