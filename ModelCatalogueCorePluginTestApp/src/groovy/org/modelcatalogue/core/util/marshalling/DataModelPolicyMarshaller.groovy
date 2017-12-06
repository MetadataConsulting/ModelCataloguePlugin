package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataModelPolicy

class DataModelPolicyMarshaller extends AbstractMarshaller {

    DataModelPolicyMarshaller() {
        super(DataModelPolicy)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        [
                id: el.id,
                name: el.name,
                version: el.version,
                policyText: el.policyText,
                elementType: el.class.name,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id"
        ]
    }
}




