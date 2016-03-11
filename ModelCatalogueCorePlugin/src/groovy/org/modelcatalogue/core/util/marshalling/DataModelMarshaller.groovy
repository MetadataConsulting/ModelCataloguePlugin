package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataModel

class DataModelMarshaller extends CatalogueElementMarshaller {

    DataModelMarshaller() {
        super(DataModel)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll  namespace: element.namespace, semanticVersion: element.semanticVersion, revisionNotes: element.revisionNotes
        ret.content = [count: Integer.MAX_VALUE, itemType: Map.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/content"]
        return ret
    }
}




