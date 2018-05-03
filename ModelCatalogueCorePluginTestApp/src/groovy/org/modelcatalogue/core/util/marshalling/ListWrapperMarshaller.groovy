package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.reports.ReportDescriptorRegistry
import org.springframework.beans.factory.annotation.Autowired

abstract class ListWrapperMarshaller extends AbstractMarshaller {

    @Autowired ReportDescriptorRegistry reportDescriptorRegistry

    ListWrapperMarshaller(Class cls) {
        super(cls)
    }

    @Override
    protected Map<String, Object> prepareJsonMap(Object elements) {
        def base =  elements.base
        def itemType =  elements.itemType?.name
        def success =  true
        def total =  elements.total
        def offset =  elements.offset
        def page =  elements.page
        def size =  getSize(elements)
        def list =  getList(elements)
        def previous =  elements.previous
        def next =  elements.next
        def sort =  elements.sort
        def order =  elements.order
        def timeTaken = elements.timeTaken
        [
             base: base,
             itemType: itemType,
             success: success,
             total: total,
             offset: offset,
             page: page,
             size: size,
             list: list,
             previous: previous,
             next: next,
             sort: sort,
             order: order,
             timeTaken: timeTaken
        ]
    }

    protected getList(Object elements) {
        elements.items
    }

    protected getSize(Object elements) {
        elements.items.size()
    }
}
