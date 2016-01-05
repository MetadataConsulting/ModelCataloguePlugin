package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.lists.JsonAwareListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper

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
        return super.getSize(elements)
    }
}
