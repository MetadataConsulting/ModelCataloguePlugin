package org.modelcatalogue.core.geb

import geb.Page

class ImportModelCatalogueXMLPage extends Page implements InputUtils {

    static at = { title == 'Import Model Catalogue XML File' }

    static url = '/dataImport/xml'

    static content = {
        inputModelName { $('input#modelName') }
        inputFile { $('input', type: 'file') }
        importButton { $('input', type: 'submit') }
    }

    void upload(String name, String absolutePath) {
        fillInput(inputModelName, name)
        upload(absolutePath)
    }

    void upload(String absolutePath) {
        inputFile = absolutePath
        importButton.click()
    }

}
