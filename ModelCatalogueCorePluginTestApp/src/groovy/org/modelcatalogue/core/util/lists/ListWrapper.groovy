package org.modelcatalogue.core.util.lists

interface ListWrapper<T> extends ListWithTotalAndType<T> {

    String getBase()
    String getNext()
    String getPrevious()
    String getSort()
    String getOrder()
    int getPage() // max
    int getOffset()
    /**
     * String to print representing time taken for results to be calculated
     * @return
     */
    String getTimeTaken()

}
