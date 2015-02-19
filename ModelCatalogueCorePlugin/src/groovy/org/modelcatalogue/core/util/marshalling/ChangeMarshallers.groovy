package org.modelcatalogue.core.util.marshalling

import grails.converters.JSON
import org.modelcatalogue.core.audit.Change

class ChangeMarshallers extends AbstractMarshallers {

    ChangeMarshallers() {
        super(Change)
    }

    protected Map<String, Object> prepareJsonMap(change) {
        if (!change) return [:]
        [
                changed:        CatalogueElementMarshallers.minimalCatalogueElementJSON(change.changed),
                latestVersion:  CatalogueElementMarshallers.minimalCatalogueElementJSON(change.latestVersion),
                type:           change.type.toString(),
                author:         CatalogueElementMarshallers.minimalCatalogueElementJSON(change.author),
                dateCreated:    change.dateCreated,
                property:       change.property,
                oldValue:       change.oldValue != null ? JSON.parse(change.oldValue as String) : null,
                newValue:       change.newValue != null ? JSON.parse(change.newValue as String) : null,
        ]
    }

}
