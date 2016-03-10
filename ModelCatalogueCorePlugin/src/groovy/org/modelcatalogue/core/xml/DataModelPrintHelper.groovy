package org.modelcatalogue.core.xml

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter

class DataModelPrintHelper extends CatalogueElementPrintHelper<DataModel> {

    @Override
    String getTopLevelName() {
        "dataModel"
    }

    @Override
    Map<String, Object> collectAttributes(DataModel element, PrintContext context) {
        Map<String, Object> attrs =  super.collectAttributes(element, context)
        if (element.semanticVersion) {
            attrs.semanticVersion = element.semanticVersion
        }
        attrs
    }

    @Override
    void processElements(Object theMkp, DataModel element, PrintContext context, Relationship rel) {
        super.processElements(theMkp, element, context, rel)

        for (CatalogueElement other in context.modelService.getTopLevelDataClasses(DataModelFilter.includes(element), [:]).items) {
                printElement(theMkp, other, context, null)
        }

        for (Class<? extends CatalogueElement> type in [DataClass, DataElement, DataType, MeasurementUnit]) {
            for (CatalogueElement other in allClassified(type, element, context)) {
                if (!context.wasPrinted(other)) {
                    printElement(theMkp, other, context, null)
                }
            }
        }
    }


    private static <E extends CatalogueElement> List<E> allClassified(Class<E> type, DataModel classification, PrintContext context) {
        DetachedCriteria<E> criteria = new DetachedCriteria<E>(type).build {
            'in'('status', [ElementStatus.DEPRECATED, ElementStatus.FINALIZED, ElementStatus.DRAFT])
            not {
                'in'('id', context.idsOfPrinted)
            }
        }
        context.dataModelService.classified(criteria, DataModelFilter.includes([classification])).list()
    }
}
