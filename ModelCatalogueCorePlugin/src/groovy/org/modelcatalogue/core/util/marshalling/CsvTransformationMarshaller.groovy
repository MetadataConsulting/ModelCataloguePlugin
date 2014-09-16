package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionRunner
import org.modelcatalogue.core.actions.ActionService
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.springframework.beans.factory.annotation.Autowired

class CsvTransformationMarshaller extends AbstractMarshallers {

    CsvTransformationMarshaller() {
        super(CsvTransformation)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]



        return [
                id: el.id,
                name: el.name,
                version: el.version,
                elementType: el.class.name,
                description: el.description,
                separator: el.separator,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                columns:  el.columnDefinitions.collect { [source: CatalogueElementMarshallers.minimalCatalogueElementJSON(it.source), destination: CatalogueElementMarshallers.minimalCatalogueElementJSON(it.destination), header: it.header] },
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",
        ]
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            description el.description
            separator el.separator
            dateCreated el.dateCreated
            lastUpdated el.lastUpdated
        }
        if (el.columnDefinitions) {
            xml.build {
                columns {
                    for (c in el.columnDefinitions) {
                        column {
                            header c.header
                            source c.source
                            destination c.destination
                        }
                    }
                }
            }
        }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
    }

}




