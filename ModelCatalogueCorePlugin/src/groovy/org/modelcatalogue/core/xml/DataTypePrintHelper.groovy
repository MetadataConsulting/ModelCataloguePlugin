package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration

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
                for (Enumeration entry in element.enumerationsObject) {
                    if (entry.value) {
                        enumeration(value: entry.key, id: entry.id, entry.value)
                    } else {
                        enumeration(value: entry.key, id: entry.id)
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
