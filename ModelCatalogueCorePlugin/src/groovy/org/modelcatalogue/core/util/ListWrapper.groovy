package org.modelcatalogue.core.util

interface ListWrapper<T> {

    String getBase()
    String getNext()
    String getPrevious()
    Class<T> getItemType()
    String getSort()
    String getOrder()
    int getTotal()
    int getPage() // max
    int getOffset()
    List<T> getItems()
    String getElementName()
    List<Map<String, String>> getAvailableReports()

}
