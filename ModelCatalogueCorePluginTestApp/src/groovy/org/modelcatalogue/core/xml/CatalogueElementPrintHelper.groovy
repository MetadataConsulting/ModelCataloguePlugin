package org.modelcatalogue.core.xml

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.HibernateHelper

/** Seems to implement the visitor pattern for printing */
abstract class CatalogueElementPrintHelper<E extends CatalogueElement> {

    private static <E extends CatalogueElement> CatalogueElementPrintHelper<E> get(Class<E> type) {
        // Singleton instances of helpers for each type of catalogue element
        if (MeasurementUnit.isAssignableFrom(type)) {
            return MeasurementUnitPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataType.isAssignableFrom(type)) {
            return DataTypePrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataElement.isAssignableFrom(type)) {
            return DataElementPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataClass.isAssignableFrom(type)) {
            return DataClassPrintHelper.instance as CatalogueElementPrintHelper<E>
        }
        if (DataModel.isAssignableFrom(type)) {
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

    static void printElement(theMkp, CatalogueElement element, PrintContext context, Relationship rel, String elementName = null) {
        CatalogueElementPrintHelper helper = get(HibernateHelper.getEntityClass(element))
        if (!elementName) {
            elementName = helper.topLevelName
        }

        if (element instanceof DataModel) {
            if (context.currentDataModel) {
                theMkp."${elementName}"(ref(element, context, true)) {
                    processRelationshipMetadata(theMkp, context, rel)
                }
                return
            }
            context.currentDataModel = element
            context.typesUsed << 'declaration'
        }

        if (context.printOnlyReference(element)) {
            theMkp."${elementName}"(ref(element, context, true)) {
                processRelationshipMetadata(theMkp, context, rel)
            }
            return
        }

        context.markAsPrinted(element)

        theMkp."${elementName}"(helper.collectAttributes(element, context)) {
            helper.processElements(theMkp, element, context, rel)
        }

        if (context.repetitive) {
            context.removeFromPrinted(element)
        }

        if (element instanceof DataModel) {
            context.currentDataModel = null
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

    void processElements(theMkp, E element, PrintContext context, Relationship relationship) {
        processRelationshipMetadata(theMkp, context, relationship)

        if (element.description) {
            theMkp.description element.description
        }
        for (Relationship rel in element.isBasedOnRelationships) {
            printBasedOn(theMkp, rel, context)
        }
        for (Relationship rel in element.relatedToRelationships) {
            CatalogueElement other = rel.source == element ? rel.destination : rel.source
            theMkp.relatedTo(ref(other, context)) {
                processRelationshipMetadata(theMkp, context, rel)
            }
        }
        for (Relationship rel in element.isSynonymForRelationships) {
            CatalogueElement other = rel.source == element ? rel.destination : rel.source
            theMkp.synonym(ref(other, context)){
                processRelationshipMetadata(theMkp, context, rel)
            }
        }
        if (element.ext) {
            theMkp.extensions {
                for (Map.Entry<String, String> entry in element.ext.entrySet()) {
                    extension(key: entry.key, entry.value)
                }
            }
        }

        List<Relationship> outgoing = restOfRelationships(Relationship.where { source == element && relationshipType.system != true}).list()
        List<Relationship> incoming = restOfRelationships(Relationship.where { destination == element  && relationshipType.system != true}).list()

        if (outgoing || incoming) {
            theMkp.relationships {
                for (Relationship rel in outgoing){
                    to(relationshipAttrs(rel, true, context)) {
                        processRelationshipMetadata(theMkp, context, rel)
                    }
                }

                for (Relationship rel in incoming){
                    from(relationshipAttrs(rel, false, context)) {
                        processRelationshipMetadata(theMkp, context, rel)
                    }
                }
            }
        }
    }

    protected void printBasedOn(theMkp, Relationship rel, PrintContext context) {
        theMkp.basedOn(ref(rel.destination, context)) {
            processRelationshipMetadata(theMkp, context, rel)
        }
    }

    static void processRelationshipMetadata(theMkp, PrintContext context, Relationship rel) {
        if (!rel) {
            return
        }
        context.typesUsed << rel.relationshipType.name
        if (rel.ext) {
            theMkp.metadata {
                for (Map.Entry<String, String> entry in rel.ext.entrySet()) {
                    extension(key: entry.key, entry.value)
                }
            }
        }
        if (rel.archived) {
            theMkp.archived true
        }
        if (rel.inherited) {
            theMkp.inherited true
        }
    }

    static DetachedCriteria<Relationship> restOfRelationships(DetachedCriteria<Relationship>  criteria) {
        criteria.not {
            'in' 'relationshipType', ['declaration', 'hierarchy', 'containment', 'supersession', 'base', 'relatedTo', 'synonym', 'involvedness', 'ruleContext'].collect { RelationshipType.readByName(it) }
        }
        criteria.'eq' 'archived', false
    }

    static Map<String, Object> ref(CatalogueElement element, PrintContext context, boolean includeDefaultId = false) {
        if (element.hasModelCatalogueId()) {
            if (context.noHref) {
                return [ref: element.modelCatalogueId]
            }
            return [ref: element.modelCatalogueId, href: element.getDefaultModelCatalogueId(!context.idIncludeVersion)]
        }

        if (element.dataModel) {
            if (includeDefaultId) {
                if (context.currentDataModel == element.dataModel) {
                    return [name: element.name, id: element.getDefaultModelCatalogueId(context.idIncludeVersion)]
                } else {
                    return [name: element.name, dataModel: element.dataModel.name, id: element.getDefaultModelCatalogueId(context.idIncludeVersion)]
                }
            }
            if (context.currentDataModel == element.dataModel) {
                return [name: element.name]
            } else {
                return [name: element.name, dataModel: element.dataModel.name]
            }
        }


        return [ref: element.getDefaultModelCatalogueId(!context.idIncludeVersion)]
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
