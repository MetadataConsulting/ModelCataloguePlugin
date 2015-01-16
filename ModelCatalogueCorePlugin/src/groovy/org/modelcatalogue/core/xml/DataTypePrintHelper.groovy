package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType

/**
 * Created by ladin on 15.01.15.
 */
class DataTypePrintHelper extends CatalogueElementPrintHelper<DataType> {

    @Override
    String getTopLevelName() {
        "dataType"
    }

    @Override
    void processElements(Object theMkp, DataType element, PrintContext context) {
        theMkp.yield {
            super.processElements(mkp, element, context)
            if (element instanceof EnumeratedType) {
                enumerations {
                    for (Map.Entry<String, String> entry in element.enumerations) {
                        enumeration(value: entry.key, entry.value)
                    }
                }
            }
        }
    }
}
