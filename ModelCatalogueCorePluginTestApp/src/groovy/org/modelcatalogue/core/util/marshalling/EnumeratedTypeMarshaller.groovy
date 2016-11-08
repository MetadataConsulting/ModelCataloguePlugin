package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.util.OrderedMap

class EnumeratedTypeMarshaller extends DataTypeMarshaller {

    EnumeratedTypeMarshaller() {
        super(EnumeratedType)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.enumerations = element.enumerationsObject.toJsonMap()
        ret.content = [count: element.enumerations?.size() ?: 0, itemType: "${EnumeratedType.name}.EnumeratedValue", link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/content"]
        ret
    }

}




