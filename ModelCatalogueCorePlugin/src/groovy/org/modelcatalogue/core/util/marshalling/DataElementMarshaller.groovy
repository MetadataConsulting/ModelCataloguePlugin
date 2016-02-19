package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType

class DataElementMarshaller extends CatalogueElementMarshaller {

    DataElementMarshaller() {
        super(DataElement)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll dataType: minimalCatalogueElementJSON(el.dataType)

        if (el.dataType) {
            ret.content = [count: 1, itemType: DataType.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        } else {
            ret.content = [count: 0, itemType: DataType.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        }

        ret
    }

}



