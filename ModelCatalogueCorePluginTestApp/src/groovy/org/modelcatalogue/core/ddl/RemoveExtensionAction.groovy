package org.modelcatalogue.core.ddl

import org.modelcatalogue.core.CatalogueElement

class RemoveExtensionAction {
    final DataDefinitionLanguage ddl
    final String property

    RemoveExtensionAction(DataDefinitionLanguage ddl, String property) {
        this.ddl = ddl
        this.property = property
    }

    void of(String catalogueElementName) {
        CatalogueElement element = ddl.find CatalogueElement, catalogueElementName
        if (!element) {
            throw new IllegalArgumentException("Catalogue element $catalogueElementName not found!")
        }
        element.ext.remove(property)
    }
}
