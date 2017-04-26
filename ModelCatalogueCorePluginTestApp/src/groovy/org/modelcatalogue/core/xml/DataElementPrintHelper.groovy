package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Relationship

/** Helper for printing Data Elements */
@Singleton
class DataElementPrintHelper extends CatalogueElementPrintHelper<DataElement> {

    @Override
    String getTopLevelName() {
        "dataElement"
    }

    @Override
    void processElements(Object markupBuilder, DataElement element, PrintContext context, Relationship rel) {
        super.processElements(markupBuilder, element, context, rel)
        if (element.dataType) {
            printElement(markupBuilder, element.dataType, context, null)
        }
    }
}
