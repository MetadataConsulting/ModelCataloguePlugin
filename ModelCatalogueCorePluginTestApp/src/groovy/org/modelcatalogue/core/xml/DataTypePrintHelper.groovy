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
        } else if(element.isBasedOn){
            if (element.isBasedOn.regexDef.first()!=null) {
                mkp.regex element.isBasedOn.regexDef.first()
            } else if (element.isBasedOn.rule.first()!=null) {
                mkp.rule element.isBasedOn.rule.first()
            }
        }
        if (element instanceof EnumeratedType && element.enumerations) {
            mkp.enumerations {
                for (Enumeration entry in element.enumerationsObject) {
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

    @Override
    protected void printBasedOn(Object mkp, Relationship rel, PrintContext context) {
        printElement(mkp, rel.destination, context, rel)
    }
}
