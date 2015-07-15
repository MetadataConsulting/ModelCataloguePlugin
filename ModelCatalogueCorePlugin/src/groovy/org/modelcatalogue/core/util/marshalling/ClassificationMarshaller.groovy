package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.DataModel

class ClassificationMarshaller extends CatalogueElementMarshaller {

    ClassificationMarshaller() {
        super(DataModel)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll  namespace: element.namespace
        return ret
    }
}




