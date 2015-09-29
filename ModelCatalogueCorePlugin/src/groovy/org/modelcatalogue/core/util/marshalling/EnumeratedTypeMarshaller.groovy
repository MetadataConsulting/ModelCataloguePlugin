package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.OrderedMap

class EnumeratedTypeMarshaller extends CatalogueElementMarshaller {

    EnumeratedTypeMarshaller() {
        super(EnumeratedType)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll valueDomains: [count: element.relatedValueDomains?.size() ?: 0, itemType: ValueDomain.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/valueDomain"]
        ret.putAll enumerations: OrderedMap.toJsonMap(element.enumerations)
        ret
    }

}




