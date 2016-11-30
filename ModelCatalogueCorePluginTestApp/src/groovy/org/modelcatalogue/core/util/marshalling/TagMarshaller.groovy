package org.modelcatalogue.core.util.marshalling

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.Tag

class TagMarshaller extends CatalogueElementMarshaller {

    TagMarshaller() {
        super(Tag)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll(
            content: [count: el.countTags(), itemType: Relationship.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/content"]
        )

        ret
    }

}




