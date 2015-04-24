package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.DataElement

class DataElementMarshaller extends CatalogueElementMarshaller {

    DataElementMarshaller() {
        super(DataElement)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll valueDomain: minimumValueDomain(el.valueDomain)
        ret
    }

}




