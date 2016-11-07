package org.modelcatalogue.core.ddl

import org.modelcatalogue.core.CatalogueElement

class UpdateDefinition {
    final DataDefinitionLanguage ddl
    final String property

    UpdateDefinition(DataDefinitionLanguage ddl, String property) {
        this.ddl = ddl
        this.property = property
    }

    UpdateAction of(String catalogueElementName) {
        CatalogueElement element = ddl.find CatalogueElement, catalogueElementName
        if (!element) {
            throw new IllegalArgumentException("Catalogue element $catalogueElementName not found!")
        }
        return new UpdateAction(ddl, property, element)
    }
}