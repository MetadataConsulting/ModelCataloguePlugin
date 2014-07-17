package org.modelcatalogue.core.util

interface ListWrapper<T> extends ListWithTotal<T> {

    String getBase()
    String getNext()
    String getPrevious()
    Class<T> getItemType()
    String getSort()
    String getOrder()
    int getPage() // max
    int getOffset()
    String getElementName()

}
