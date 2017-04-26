package org.modelcatalogue.core.xml

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter

/** Helper for printing DataModels */
@Singleton
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
    void processElements(Object markupBuilder, DataModel dataModel, PrintContext context, Relationship rel) {
        /**
         * if we are working in the context of a currentDataModel then just print this
         * Actually this stuff with currentDataModel seems to be implemented by
         * printOnlyReference and keepInside and params.full=true.
         */
        if (context.currentDataModel) {

            processRelationshipMetadata(markupBuilder, context, rel)
        }
        else {

            context.typesUsed << 'declaration' //what's this?
            // process everything in this data model with context.currentDataModel set.
            context.currentDataModel = element

            super.processElements(markupBuilder, dataModel, context, rel)
            if (dataModel.revisionNotes) {
                markupBuilder.revisionNotes dataModel.revisionNotes
            }
            for (DataModelPolicy policy in dataModel.policies) {
                markupBuilder.policy(policy.name)
                context.policiesUsed << policy.name
            }
            /** Recursively print data classes, which should recursively print
             * their child classes and child elements.
             */
            for (DataClass dataClass in context.dataClassService.getTopLevelDataClasses(DataModelFilter.includes(dataModel), [:]).items) {
                super.printElement(markupBuilder, dataClass, context, null)
            }

            /**
             * Print everything else under the model for good measure
             */
            for (Class<? extends CatalogueElement> type in [DataClass, DataElement, DataType, MeasurementUnit]) {
                for (CatalogueElement other in allClassified(type, dataModel, context)) {
                    if (!context.wasPrinted(other)) {
                        printElement(markupBuilder, other, context, null)
                    }
                }
            }

            // unset currentDataModel when done processing all elements under the data model.
            context.currentDataModel = null

        }
    }

    /**
     *
     * @param type
     * @param dataModel
     * @param context
     * @return All elements of type type in dataModel that are classified as deprecated, finalized or draft. At the moment this is somewhat vacuous as all element should have a status and that status should only be one of those three.
     */
    private static <E extends CatalogueElement> List<E> allClassified(Class<E> type, DataModel dataModel, PrintContext context) {
        /** Searches for elements with given status and not printed yet */
        DetachedCriteria<E> criteria = new DetachedCriteria<E>(type).build {
            'in'('status', [ElementStatus.DEPRECATED, ElementStatus.FINALIZED, ElementStatus.DRAFT])
            not {
                'in'('id', context.idsOfPrinted)
            }
        }
        /** To see whether */
        DataModelFilter dataModelFilter = DataModelFilter.includes([dataModel])
        def list = context.dataModelService.classified(criteria, dataModelFilter).list()
        return list
    }
}
