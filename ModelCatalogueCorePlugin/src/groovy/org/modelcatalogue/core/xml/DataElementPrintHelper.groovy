package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataElement

/**
 * Created by ladin on 15.01.15.
 */
class DataElementPrintHelper extends CatalogueElementPrintHelper<DataElement> {

    @Override
    String getTopLevelName() {
        "dataElement"
    }

    @Override
    void processElements(Object theMkp, DataElement element, PrintContext context) {
        theMkp.yield {
            super.processElements(mkp, element, context)
            if (element.valueDomain) {
                printElement(mkp, element.valueDomain, context)
            }
        }
    }
}
