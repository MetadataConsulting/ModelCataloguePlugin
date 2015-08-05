package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.PrimitiveType

class PrimitiveTypeMarshaller extends DataTypeMarshaller {

    PrimitiveTypeMarshaller() {
        super(PrimitiveType)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.measurementUnit = minimalCatalogueElementJSON(element.measurementUnit)
        ret
    }

}




