package org.modelcatalogue.core

import groovy.transform.CompileStatic

@CompileStatic
class ModelCatalogueVersionController {
    static allowedMethods = [index: 'GET']

    def index() {}
}
