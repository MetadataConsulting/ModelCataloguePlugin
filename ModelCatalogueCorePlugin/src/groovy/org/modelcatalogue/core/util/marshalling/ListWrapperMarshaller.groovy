package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import org.modelcatalogue.core.SecurityService
import org.modelcatalogue.core.reports.ReportDescriptor
import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired

abstract class ListWrapperMarshaller extends AbstractMarshallers {

    @Autowired ReportsRegistry reportsRegistry
    @Autowired SecurityService modelCatalogueSecurityService

    ListWrapperMarshaller(Class cls) {
        super(cls)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object elements) {
        [
                base: elements.base,
                itemType: elements.itemType?.name,
                success: true,
                total: elements.total,
                offset: elements.offset,
                page: elements.page,
                size: elements.items.size(),
                list: elements.items,
                previous: elements.previous,
                next: elements.next,
                availableReports: getAvailableReports(elements),
                sort: elements.sort,
                order: elements.order
        ]
    }

    protected getAvailableReports(el) {
        def reports = []

        for (ReportDescriptor descriptor in reportsRegistry.getAvailableReports(el)) {
            reports << [title: descriptor.getTitle(el), url: descriptor.getLink(el), type: modelCatalogueSecurityService.userLoggedIn ?  descriptor.renderType.toString() : 'LINK']
        }

        reports
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

    @Override
    protected String getElementName(Object element) {
        return element.elementName ?: 'elements'
    }

    @Override
    protected boolean isSupportingCustomElementName() {
        return true
    }
}
