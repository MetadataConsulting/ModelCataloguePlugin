package org.modelcatalogue.core.xml

import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValueDomain

class ValueDomainPrintHelper extends CatalogueElementPrintHelper<ValueDomain> {

    @Override
    String getTopLevelName() {
        "valueDomain"
    }

    @Override
    Map<String, Object> collectAttributes(ValueDomain element, PrintContext context) {
        Map<String, Object> ret = super.collectAttributes(element, context)
        if (element.multiple) {
            ret.multiple = element.multiple
        }
        ret
    }

    @Override
    void processElements(Object mkp, ValueDomain element, PrintContext context, Relationship relationship) {
        super.processElements(mkp, element, context, relationship)
        if (element.regexDef) {
            mkp.regex element.regexDef
        } else if (element.rule) {
            mkp.rule element.rule
        }
        if (element.unitOfMeasure) {
            printElement(mkp, element.unitOfMeasure, context, null, 'unitOfMeasure')
        }

        if (element.dataType) {
            printElement(mkp, element.dataType, context, null)
        }
    }
}
