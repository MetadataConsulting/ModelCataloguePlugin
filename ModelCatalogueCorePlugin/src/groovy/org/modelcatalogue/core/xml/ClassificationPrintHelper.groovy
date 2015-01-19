package org.modelcatalogue.core.xml

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.*

class ClassificationPrintHelper extends CatalogueElementPrintHelper<Classification> {

    @Override
    String getTopLevelName() {
        "classification"
    }

    @Override
    void processElements(Object theMkp, Classification element, PrintContext context) {
        theMkp.yield {
            super.processElements(mkp, element, context)

            for (CatalogueElement other in context.modelService.getTopLevelModels([element], [:]).items) {
                    printElement(mkp, other, context)
            }

            for (Class<? extends CatalogueElement> type in [Model, DataElement, ValueDomain, DataType, MeasurementUnit]) {
                for (CatalogueElement other in allClassified(type, element, context)) {
                    if (!context.wasPrinted(other)) {
                        printElement(mkp, other, context)
                    }
                }
            }


        }
    }


    private static <E extends CatalogueElement> List<E> allClassified(Class<E> type, Classification classification, PrintContext context) {
        DetachedCriteria<E> criteria = new DetachedCriteria<E>(type).build {
            'in'('status', [ElementStatus.DEPRECATED, ElementStatus.FINALIZED, ElementStatus.DRAFT])
            not {
                'in'('id', context.idsOfPrinted)
            }
        }
        context.classificationService.classified(criteria, [classification]).list()
    }
}
