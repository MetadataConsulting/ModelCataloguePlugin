package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.actions.Batch

class BatchMarshaller extends AbstractMarshallers {

    BatchMarshaller() {
        super(Batch)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]
        [
                id: el.id,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                elementTypeName: GrailsNameUtils.getNaturalName(el.class.simpleName),
                archived: el.archived,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id"
        ]
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            description el.description
        }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(el.archived, "archived", xml)
        addXmlAttribute(GrailsNameUtils.getNaturalName(el.class.simpleName), "elementTypeName", xml)
    }

}




