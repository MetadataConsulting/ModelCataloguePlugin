package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ValueDomain

class DataTypeMarshaller extends CatalogueElementMarshaller {

    DataTypeMarshaller() {
        super(DataType)
    }

    DataTypeMarshaller(Class<? extends DataType> cls) {
        super(cls)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll valueDomains: [count: element.countRelatedValueDomains(), itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/valueDomain"]
        return ret
    }
}




