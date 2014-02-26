package uk.co.mc.core.util.marshalling

import grails.converters.XML

abstract class ListWrapperMarshaller extends AbstractMarshallers {

    ListWrapperMarshaller(Class cls) {
        super(cls)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object elements) {
        [
                success: true,
                total: elements.total,
                offset: elements.offset,
                page: elements.page,
                size: elements.items.size(),
                list: elements.items,
                previous: elements.previous,
                next: elements.next,
        ]
    }

    @Override
    protected void buildXml(Object elements, XML xml) {
        buildItemsXml(elements, xml)
        xml.build {
            previous elements.previous
            next elements.next
        }
    }


    protected String getItemNodeName() {
        "item"
    }

    protected void buildItemsXml(Object elements, XML xml) {
        xml.build {
            for (el in elements.items) {
                "$itemNodeName" el
            }
        }
    }

    @Override
    protected void addXmlAttributes(Object elements, XML xml) {
        xml.attribute("total", "${elements.total}")
        xml.attribute("page", "${elements.page}")
        xml.attribute("offset", "${elements.offset}")
        xml.attribute("size", "${elements.items.size()}")
        xml.attribute("success", "true")
    }
}
