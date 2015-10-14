package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.ReferenceType

class ReferenceTypeMarshaller extends DataTypeMarshaller {

    ReferenceTypeMarshaller() {
        super(ReferenceType)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.dataClass = minimalCatalogueElementJSON(element.dataClass)
        ret
    }

}




