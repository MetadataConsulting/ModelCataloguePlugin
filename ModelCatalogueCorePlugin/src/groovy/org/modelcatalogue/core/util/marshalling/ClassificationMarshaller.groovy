package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.util.ClassificationFilter

class ClassificationMarshaller extends CatalogueElementMarshaller {

    ClassificationMarshaller() {
        super(Classification)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll  namespace: element.namespace, activity: [count: auditService.getGlobalChanges([:], ClassificationFilter.includes(element)).total, itemType: Change, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/activity"]
        return ret
    }
}




