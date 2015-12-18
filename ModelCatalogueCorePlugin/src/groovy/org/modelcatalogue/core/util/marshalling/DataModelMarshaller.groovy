package org.modelcatalogue.core.util.marshalling

import com.google.common.collect.ImmutableSet
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.DataModelFilter

class DataModelMarshaller extends CatalogueElementMarshaller {

    DataModelMarshaller() {
        super(DataModel)
    }

    protected Map<String, Object> prepareJsonMap(element) {
        if (!element) return [:]
        def ret = super.prepareJsonMap(element)
        ret.putAll  namespace: element.namespace, semanticVersion: element.semanticVersion, revisionNotes: element.revisionNotes
        ret.statistics = dataModelService.getStatistics(DataModelFilter.create(ImmutableSet.<DataModel>of(element as DataModel), ImmutableSet.<DataModel>of()))
        ret.content = [count: 7, itemType: Map.name, link: "/${GrailsNameUtils.getPropertyName(element.getClass())}/$element.id/content"]
        return ret
    }
}




