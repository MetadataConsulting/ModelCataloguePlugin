package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Relationship

/**
 * Created by ladin on 15.01.15.
 */
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
    }
}
