package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
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
        if (element.regexDef) {
            mkp.regex element.regexDef
        } else if (element.rule) {
            mkp.rule element.rule
        }
        if (element instanceof EnumeratedType && element.enumerations) {
            mkp.enumerations {
                for (Map.Entry<String, String> entry in element.enumerations) {
                    if (entry.value) {
                        enumeration(value: entry.key, entry.value)
                    } else {
                        enumeration(value: entry.key)
                    }
                }
            }
        }
        if (element instanceof ReferenceType && element.dataClass) {
            if (element.dataClass) {
                printElement(mkp, element.dataClass, context, null)
            }
        }
        if (element instanceof PrimitiveType && element.measurementUnit) {
            if (element.measurementUnit) {
                printElement(mkp, element.measurementUnit, context, null)
            }
        }
    }
}
