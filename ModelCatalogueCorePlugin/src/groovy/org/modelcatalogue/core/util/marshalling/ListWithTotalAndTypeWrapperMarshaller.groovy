package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.JsonAwareListWithTotalAndType
import org.modelcatalogue.core.util.ListWithTotalAndTypeWrapper

class ListWithTotalAndTypeWrapperMarshaller extends ListWrapperMarshaller {

    ListWithTotalAndTypeWrapperMarshaller() {
        super(ListWithTotalAndTypeWrapper)
    }

    @Override
    protected getList(Object elements) {
        if (elements.list instanceof JsonAwareListWithTotalAndType) {
            return elements.list.jsonItems
        }
        return super.getList(elements)
    }

    @Override
    protected getSize(Object elements) {
        if (elements.list instanceof JsonAwareListWithTotalAndType) {
            return elements.list.jsonItems.size()
        }
        return super.getList(elements)
    }
}
