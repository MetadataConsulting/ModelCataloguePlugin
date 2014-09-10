package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Classification

/**
 * Created by ladin on 14.02.14.
 */
abstract class PublishedElementMarshallers extends ExtendibleElementMarshallers {


    PublishedElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll(
                classifications: el.classifications.collect(CatalogueElementMarshallers.&minimalCatalogueElementJSON),
                versionNumber: el.versionNumber,
                status: el.status.toString(),
                versionCreated: el.versionCreated,
                history: [count: el.countVersions(), itemType: type.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/history"]
        )
        ret
    }

    @Override
    protected void addXmlAttributes(el, XML xml) {
        super.addXmlAttributes(el, xml)
        addXmlAttribute(el.classifications, "classifications", xml)
        addXmlAttribute(el.versionNumber, "versionNumber", xml)
        addXmlAttribute(el.status, "status", xml)
        addXmlAttribute(el.versionCreated, "versionCreated", xml)
    }
}
