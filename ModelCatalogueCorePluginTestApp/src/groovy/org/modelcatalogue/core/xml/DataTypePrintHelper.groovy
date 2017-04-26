package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration

/** Helper for printing Data Types */
@Singleton
class DataTypePrintHelper extends CatalogueElementPrintHelper<DataType> {

    @Override
    String getTopLevelName() {
        "dataType"
    }

    @Override
    void processElements(Object markupBuilder, DataType dataType, PrintContext context, Relationship rel) {
        super.processElements(markupBuilder, dataType, context, rel)
        if (dataType.regexDef) {
            markupBuilder.regex dataType.regexDef
        } else if (dataType.rule) {
            markupBuilder.rule dataType.rule
        }
        if (dataType instanceof EnumeratedType && dataType.enumerations) {
            markupBuilder.enumerations {
                for (Enumeration entry in dataType.enumerationsObject) {
                    if (entry.value) {
                        if (entry.deprecated) {
                            enumeration(value: entry.key, id: entry.id, deprecated: true, entry.value)
                        } else {
                            enumeration(value: entry.key, id: entry.id, entry.value)
                        }
                    } else {
                        if (entry.deprecated) {
                            enumeration(value: entry.key, id: entry.id, deprecated: true)
                        } else {
                            enumeration(value: entry.key, id: entry.id)
                        }
                    }
                }
            }
        }
        if (dataType instanceof ReferenceType && dataType.dataClass) {
            if (dataType.dataClass) {
                printElement(markupBuilder, dataType.dataClass, context, null)
            }
        }
        if (dataType instanceof PrimitiveType && dataType.measurementUnit) {
            if (dataType.measurementUnit) {
                printElement(markupBuilder, dataType.measurementUnit, context, null)
            }
        }
    }

    @Override
    protected void printBasedOn(Object markupBuilder, Relationship rel, PrintContext context) {
        printElement(markupBuilder, rel.destination, context, rel)
    }
}
