package uk.co.mc.core.util.marshalling

import grails.converters.XML
import uk.co.mc.core.CatalogueElement
import uk.co.mc.core.util.Elements

/**
 * Created by ladin on 19.02.14.
 */
class ElementsMarshaller extends AbstractMarshallers {

    ElementsMarshaller() {
        super(Elements)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object elements) {
        [
                success: true,
                total: elements.total,
                offset: elements.offset,
                page: elements.page,
                size: elements.elements.size(),
                list: elements.elements,
                previous: elements.previous,
                next: elements.next,
        ]
    }

    @Override
    protected void buildXml(Object elements, XML xml) {
        xml.build {
            for (CatalogueElement el in elements.elements) {
                element el
            }
            previous elements.previous
            next elements.next
        }
    }

    @Override
    protected void addXmlAttributes(Object elements, XML xml) {
        xml.attribute("total", "${elements.total}")
        xml.attribute("page", "${elements.page}")
        xml.attribute("offset", "${elements.offset}")
        xml.attribute("size", "${elements.elements.size()}")
        xml.attribute("success", "true")
    }
}
