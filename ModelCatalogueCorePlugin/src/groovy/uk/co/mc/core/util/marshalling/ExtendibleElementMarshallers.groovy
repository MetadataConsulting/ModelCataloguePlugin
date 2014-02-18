package uk.co.mc.core.util.marshalling

import grails.converters.XML

/**
 * Created by ladin on 14.02.14.
 */
class ExtendibleElementMarshallers extends PublishedElementMarshallers {

    ExtendibleElementMarshallers(Class type) {
        super(type)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        def ret = super.prepareJsonMap(el)
        ret.putAll(
                ext: el.ext
        )
        ret
    }

    protected void buildXml(el, XML xml) {
        super.buildXml(el, xml)
        if (el.ext) {
            xml.build {
                extensions {
                    for (e in el.ext.entrySet()) {
                        extension key: e.key, e.value
                    }
                }
            }
        }
    }

}
