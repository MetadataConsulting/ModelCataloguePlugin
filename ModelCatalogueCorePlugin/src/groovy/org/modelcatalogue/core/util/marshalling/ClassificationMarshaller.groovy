package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification

class ClassificationMarshaller extends CatalogueElementMarshallers {

    ClassificationMarshaller() {
        super(Classification)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll  namespace: element.namespace
        ret.classifies = [count: element.countClassifies(), itemType: CatalogueElement.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/outgoing/classification"]
        return ret
    }
}




