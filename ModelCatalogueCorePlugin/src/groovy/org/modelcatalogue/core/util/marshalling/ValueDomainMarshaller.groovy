package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.ValueDomain

class ValueDomainMarshaller extends CatalogueElementMarshaller {

    ValueDomainMarshaller() {
        super(ValueDomain)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)

        ret.putAll unitOfMeasure: minimalCatalogueElementJSON(el.unitOfMeasure),
                rule: el.rule,
                dataType: minimalCatalogueElementJSON(el.dataType),
                multiple: el.multiple ?: false,
                mappings: [count: el.outgoingMappings?.size() ?: 0, itemType: Mapping.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/mapping"],
                dataElements: [count: el.countDataElements(), itemType: DataElement.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/dataElement"]
        ret
    }
}




