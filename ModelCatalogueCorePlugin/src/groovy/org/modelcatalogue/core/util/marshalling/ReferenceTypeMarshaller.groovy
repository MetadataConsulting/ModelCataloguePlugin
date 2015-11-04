package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.ReferenceType

class ReferenceTypeMarshaller extends DataTypeMarshaller {

    ReferenceTypeMarshaller() {
        super(ReferenceType)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.dataClass = minimalCatalogueElementJSON(el.dataClass)
        if (el.dataClass) {
            ret.content = [count: 1, itemType: DataClass.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        } else {
            ret.content = [count: 0, itemType: DataClass.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        }
        ret
    }

}




