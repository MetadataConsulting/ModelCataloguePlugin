package org.modelcatalogue.core.xml

import org.modelcatalogue.core.ValueDomain

/**
 * Created by ladin on 15.01.15.
 */
class ValueDomainPrintHelper extends CatalogueElementPrintHelper<ValueDomain> {

    @Override
    String getTopLevelName() {
        "valueDomain"
    }

    @Override
    void processElements(Object theMkp, ValueDomain element, PrintContext context) {
        theMkp.yield {
            super.processElements(mkp, element, context)
            if (element.regexDef) {
                regex element.regexDef
            } else if (element.rule) {
                rule element.rule
            }
            if (element.unitOfMeasure) {
                printElement(mkp, element.unitOfMeasure, context, 'unitOfMeasure')
            }

            if (element.dataType) {
                printElement(mkp, element.dataType, context)
            }
        }
    }
}
