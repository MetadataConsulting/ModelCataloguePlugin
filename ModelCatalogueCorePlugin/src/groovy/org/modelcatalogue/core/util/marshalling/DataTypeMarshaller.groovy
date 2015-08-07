package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.Mapping
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
        ret.rule = element.rule
        ret.mappings = [count: element.outgoingMappings?.size() ?: 0, itemType: Mapping.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/mapping"]
        ret.dataElements =[count: element.countRelatedDataElements(), itemType: DataElement.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/dataElement"]
        return ret
    }
}




