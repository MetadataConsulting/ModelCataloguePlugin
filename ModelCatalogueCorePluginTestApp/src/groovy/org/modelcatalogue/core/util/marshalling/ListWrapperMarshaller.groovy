package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.reports.ReportsRegistry
import org.springframework.beans.factory.annotation.Autowired

abstract class ListWrapperMarshaller extends AbstractMarshaller {

    @Autowired ReportsRegistry reportsRegistry

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
                size: getSize(elements),
                list: getList(elements),
                previous: elements.previous,
                next: elements.next,
                sort: elements.sort,
                order: elements.order
        ]
    }

    protected getList(Object elements) {
        elements.items
    }

    protected getSize(Object elements) {
        elements.items.size()
    }
}
