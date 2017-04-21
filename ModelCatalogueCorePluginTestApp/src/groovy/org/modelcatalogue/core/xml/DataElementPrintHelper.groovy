package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship

@Singleton
class DataElementPrintHelper extends CatalogueElementPrintHelper<DataElement> {

    @Override
    String getTopLevelName() {
        "dataElement"
    }

    @Override
    void processElements(Object mkp, DataElement element, PrintContext context, Relationship rel) {
        super.processElements(mkp, element, context, rel)
        if (element.dataType) {
            printElement(mkp, element.dataType, context, null)
        }
    }
}
