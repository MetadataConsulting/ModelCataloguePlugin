package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship

class DataTypePrintHelper extends CatalogueElementPrintHelper<DataType> {

    @Override
    String getTopLevelName() {
        "dataType"
    }

    @Override
    void processElements(Object mkp, DataType element, PrintContext context, Relationship rel) {
        super.processElements(mkp, element, context, rel)
        if (element instanceof EnumeratedType) {
            mkp.enumerations {
                for (Map.Entry<String, String> entry in element.enumerations) {
                    enumeration(value: entry.key, entry.value)
                }
            }
        }
        if (element instanceof ReferenceType) {
            if (element.dataClass) {
                printElement(mkp, element.dataClass, context, null)
            }
        }
    }
}
