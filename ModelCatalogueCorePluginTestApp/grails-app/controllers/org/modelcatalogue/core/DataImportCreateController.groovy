package org.modelcatalogue.core

import groovy.transform.CompileStatic

@CompileStatic
class DataImportCreateController {

    static allowedMethods = [
            importExcel: 'GET',
            importObo: 'GET',
            importModelCatalogueDSL: 'GET',
            importXml: 'GET',
    ]

    def importExcel() {
        render view: '/dataImport/importExcel'
    }

    def importObo() {
        render view: '/dataImport/importObo'
    }

    def importModelCatalogueDSL() {
        render view: '/dataImport/importModelCatalogueDSL'
    }

    def importXml() {
        render view: '/dataImport/importXml'
    }
}
