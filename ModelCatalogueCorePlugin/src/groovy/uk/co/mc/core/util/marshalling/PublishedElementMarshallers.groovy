package uk.co.mc.core.util.marshalling

import grails.converters.XML

/**
 * Created by ladin on 14.02.14.
 */
abstract class PublishedElementMarshallers extends CatalogueElementMarshallers {


    PublishedElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll(
                versionNumber: el.versionNumber,
                status: el.status.toString()
        )
        ret
    }

    @Override
    protected void addXmlAttributes(el, XML xml) {
        super.addXmlAttributes(el, xml)
        addXmlAttribute(el.versionNumber, "versionNumber", xml)
        addXmlAttribute(el.status, "status", xml)
    }
}
