package org.modelcatalogue.core.util.marshalling

import grails.converters.XML

abstract class ListWrapperMarshaller extends AbstractMarshallers {

    ListWrapperMarshaller(Class cls) {
        super(cls)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object elements) {
        [
                itemType: elements.itemType?.name,
                listType: type.name,
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
        addXmlAttribute(elements.total, "total", xml)
        addXmlAttribute(elements.page, "page", xml)
        addXmlAttribute(elements.offset, "offset", xml)
        addXmlAttribute(elements.items.size(), "size", xml)
        addXmlAttribute("true", "success", xml)
    }
}
