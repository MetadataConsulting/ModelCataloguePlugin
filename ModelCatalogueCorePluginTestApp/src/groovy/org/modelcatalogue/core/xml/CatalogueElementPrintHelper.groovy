package org.modelcatalogue.core.xml

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.HibernateHelper

/** Seems to implement the visitor pattern, using the other <Resource>PrintHelpers for printing an element. */
abstract class CatalogueElementPrintHelper<E extends CatalogueElement> {

    /** Get the appropriate helper for writing a CatalogueElement of a particular class.
     * It seems that these helpers override processElement but not printElement.
     * @param type The class or type of the CatalogueElement to be written.
     * @return The appropriate helper to write the CatalogueElement. */
    private static <E extends CatalogueElement> CatalogueElementPrintHelper<E> getHelperForType(Class<E> type) {
        // Singleton instances of helpers for each type of catalogue element
        if (MeasurementUnit.isAssignableFrom(type)) {
            /** Does not recurse. */
            return MeasurementUnitPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataType.isAssignableFrom(type)) {
            /**
             * May recurse to MeasurementUnit (as Primitive Type)
             * or DataClass (as Reference Type)
             */
            return DataTypePrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataElement.isAssignableFrom(type)) {
            /** May recurse to DataType. */
            return DataElementPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataClass.isAssignableFrom(type)) {
            /** May recurse to DataClass, DataElement */
            return DataClassPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataModel.isAssignableFrom(type)) {
            /** May recurse to DataClass, DataElement, DataType, MeasurementUnit. */
            return DataModelPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (Asset.isAssignableFrom(type)) {
            return AssetPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (ValidationRule.isAssignableFrom(type)) {
            return ValidationRulePrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        throw new IllegalArgumentException("Not yet implemented for $type")
    }

    /**
     * What is the difference between printElement and processElement?
     * processElement also does some printing.
     * printElement is not overridden by helper subclasses.
     * Helper subclasses call printElement to recurse on related elements
     * e.g. DataModel's processElement calls printElement on top level DataClasses
     * @param markupBuilder
     * @param element
     * @param context
     * @param rel The relationship by which this element was found?
     * @param elementTypeName
     */
    static void printElement(markupBuilder, CatalogueElement element, PrintContext context, Relationship rel, String elementTypeName = null) {
        // This code is confusing. It has got lots of ifs and returns.
        CatalogueElementPrintHelper helper = getHelperForType(HibernateHelper.getEntityClass(element))
        if (!elementTypeName) {
            elementTypeName = helper.topLevelName
        }

        /**
         * The following executes if the element has already been marked as printed,
         * or if it is outside the model we want to keep inside.
         */
        if (context.printOnlyReference(element)) {
            markupBuilder."${elementTypeName}"(ref(element, context, true)) {
                processRelationshipMetadata(markupBuilder, context, rel)
            }
            return
        }

        /**
         * We mark as printed before printing, which means if the element appears again
         * "below" itself we will print only a reference.
         */
        context.markAsPrinted(element) // Why do we mark as printed *before* processing?

        /** This is the main processing/printing of the element, which may be recursive.
         *
         */
        markupBuilder."${elementTypeName}"(helper.collectAttributes(element, context)) {
            helper.processElement(markupBuilder, element, context, rel)
        }
        /**
         *  why not make this part of context? Or do we want to mark
         *  element as printed only during the above step?
         */
        if (context.repetitive) {
            context.removeFromPrinted(element)
        }

    }


    Map<String, Object> collectAttributes(E element, PrintContext context) {
        Map<String, Object> attrs = [name: element.name]

        if (element.dataModel && context.currentDataModel != element.dataModel) {
            attrs.dataModel = element.dataModel.name
        }

        if (element.hasModelCatalogueId()) {
            attrs.id = element.modelCatalogueId
        } else {
            attrs.id = element.getDefaultModelCatalogueId(!context.idIncludeVersion)
        }

        if (!context.noHref) {
            attrs.href = element.getDefaultModelCatalogueId(!context.idIncludeVersion)
        }

        if (shouldPrintStatusForElement(element)) {
            if (element.status in [ElementStatus.FINALIZED, ElementStatus.DEPRECATED, ElementStatus.DRAFT]) {
                attrs.status = element.status
            } else {
                throw new IllegalArgumentException("Cannot print ${element.getClass().simpleName} with status $element.status")
            }
        }

        attrs
    }

    /**
     * For the CatalogueElementPrintHelper class, processElement is not recursive,
     * that is it doesn't get to processing other elements via relationships. It prints:
     * description, BasedOn, RelatedTo, SynonymFor relationships,
     * metadata extensions, and then the rest of the relationships.
     *
     * Helper subclasses call this method by super at the beginning of their overrides.
     * The overrides may recurse into processing other elements.
     * @param markupBuilder
     * @param element
     * @param context
     * @param relationship
     */
    void processElement(markupBuilder, E element, PrintContext context, Relationship relationship) {
        processRelationshipMetadata(markupBuilder, context, relationship)

        if (element.description) {
            markupBuilder.description element.description
        }
        for (Relationship rel in element.isBasedOnRelationships) {
            markupBuilder.basedOn(ref(rel.destination, context)) {
                processRelationshipMetadata(markupBuilder, context, rel)
            }
        }
        for (Relationship rel in element.relatedToRelationships) {
            CatalogueElement other = rel.source == element ? rel.destination : rel.source
            markupBuilder.relatedTo(ref(other, context)) {
                processRelationshipMetadata(markupBuilder, context, rel)
            }
        }
        for (Relationship rel in element.isSynonymForRelationships) {
            CatalogueElement other = rel.source == element ? rel.destination : rel.source
            markupBuilder.synonym(ref(other, context)){
                processRelationshipMetadata(markupBuilder, context, rel)
            }
        }
        if (element.ext) {
            markupBuilder.extensions {
                for (Map.Entry<String, String> entry in element.ext.entrySet()) {
                    extension(key: entry.key, entry.value)
                }
            }
        }

        List<Relationship> outgoing = restOfRelationships(Relationship.where { source == element && relationshipType.system != true}).list()
        List<Relationship> incoming = restOfRelationships(Relationship.where { destination == element  && relationshipType.system != true}).list()

        if (outgoing || incoming) {
            markupBuilder.relationships {
                for (Relationship rel in outgoing){
                    to(relationshipAttrs(rel, true, context)) {
                        processRelationshipMetadata(markupBuilder, context, rel)
                    }
                }

                for (Relationship rel in incoming){
                    from(relationshipAttrs(rel, false, context)) {
                        processRelationshipMetadata(markupBuilder, context, rel)
                    }
                }
            }
        }
    }
    // removed isBasedOn method since it is only used once

    /**
     * Write the metadata for the relationship rel to markupBuilder.
     * @param markupBuilder
     * @param context
     * @param rel
     */
    static void processRelationshipMetadata(markupBuilder, PrintContext context, Relationship rel) {
        if (!rel) {
            return
        }
        context.relationshipTypesUsed << rel.relationshipType.name
        if (rel.ext) {
            markupBuilder.metadata {
                for (Map.Entry<String, String> entry in rel.ext.entrySet()) {
                    extension(key: entry.key, entry.value)
                }
            }
        }
        if (rel.archived) {
            markupBuilder.archived true
        }
        if (rel.inherited) {
            markupBuilder.inherited true
        }
    }

    static DetachedCriteria<Relationship> restOfRelationships(DetachedCriteria<Relationship>  criteria) {
        criteria.not {
            'in' 'relationshipType', ['declaration', 'hierarchy', 'containment', 'supersession', 'base', 'relatedTo', 'synonym', 'involvedness', 'ruleContext'].collect { RelationshipType.readByName(it) }
        }
        criteria.'eq' 'archived', false
    }

    /** Returns a map used for XML attributes which describe references to (way of identifying) an element. */
    static Map<String, Object> ref(CatalogueElement element, PrintContext context, boolean includeDefaultId = false) {
        if (element.hasModelCatalogueId()) {
            return context.noHref ?
                [ref:element.modelCatalogueId]
                : [ref: element.modelCatalogueId,
                   href: element.getDefaultModelCatalogueId(!context.idIncludeVersion)]
        }

        else if (element.dataModel) {
            def map = [name: element.name]
            if (context.currentDataModel != element.dataModel) {
                map['dataModel'] = element.dataModel.name
            }
            if (includeDefaultId) {
                map ['id']= element.getDefaultModelCatalogueId(!context.idIncludeVersion)
            }
            return map
        }

        else {
            return [ref: element.getDefaultModelCatalogueId(!context.idIncludeVersion)]
        }

    }

    static Map<String, Object> relationshipAttrs(Relationship relationship, boolean outgoing, PrintContext context) {
        Map<String, Object> ret = ref(outgoing ? relationship.destination : relationship.source, context)
        ret.relationshipType = relationship.relationshipType.name
        if (!ret.ref) {
            if (outgoing && relationship.destination.getClass() != relationship.relationshipType.destinationClass) {
                ret.type = shortNameForKnown(relationship.destination.getClass())
            }
            if (!outgoing && relationship.source.getClass() != relationship.relationshipType.sourceClass) {
                ret.type = shortNameForKnown(relationship.destination.getClass())
            }
        }
        return ret
    }

    static String shortNameForKnown(Class type) {
        if (type == EnumeratedType) {
            return 'dataType'
        }
        if (type in [DataModel, DataClass, DataElement, DataType, MeasurementUnit]) {
            String simpleName = type.simpleName
            return simpleName[0].toLowerCase() + simpleName[1..-1]
        }
        return type.name
    }

    abstract String getTopLevelName()

    protected boolean shouldPrintStatusForElement(CatalogueElement element) {
        return HibernateHelper.getEntityClass(element) == DataModel || element.status == ElementStatus.DEPRECATED
    }

}
