package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship

/**
 * Created by ladin on 15.01.15.
 */
class DataElementPrintHelper extends CatalogueElementPrintHelper<DataElement> {

    @Override
    String getTopLevelName() {
        "dataElement"
    }

    @Override
    void processElements(Object mkp, DataElement element, PrintContext context, Relationship rel) {
        super.processElements(mkp, element, context, rel)
        if (element.valueDomain) {
            printElement(mkp, element.valueDomain, context, null)
        }
    }
}
