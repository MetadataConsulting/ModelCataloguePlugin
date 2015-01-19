package org.modelcatalogue.core.xml

import grails.gorm.DetachedCriteria
import org.modelcatalogue.core.*

/**
 * Created by ladin on 15.01.15.
 */
abstract class CatalogueElementPrintHelper<E extends CatalogueElement> {

    private static <E extends CatalogueElement> CatalogueElementPrintHelper<E> get(Class<E> type) {
        // TODO: pool helpers as they are stateless
        if (MeasurementUnit.isAssignableFrom(type)) {
            return new MeasurementUnitPrintHelper() as CatalogueElementPrintHelper<E>
        }
        if (DataType.isAssignableFrom(type)) {
            return new DataTypePrintHelper() as CatalogueElementPrintHelper<E>
        }
        if (ValueDomain.isAssignableFrom(type)) {
            return new ValueDomainPrintHelper() as CatalogueElementPrintHelper<E>
        }
        if (DataElement.isAssignableFrom(type)) {
            return new DataElementPrintHelper() as CatalogueElementPrintHelper<E>
        }
        if (Model.isAssignableFrom(type)) {
            return new ModelPrintHelper() as CatalogueElementPrintHelper<E>
        }
        if (Classification.isAssignableFrom(type)) {
            return new ClassificationPrintHelper() as CatalogueElementPrintHelper<E>
        }
        throw new IllegalArgumentException("Not yet implemented for $type")
    }

    static void printElement(theMkp, CatalogueElement element, PrintContext context, String elementName = null) {
        CatalogueElementPrintHelper helper = get(element.class)
        if (!elementName) {
            elementName = helper.topLevelName
        }

        if (element instanceof Classification) {
            if (context.currentClassification) {
                theMkp.yield {
                    "${elementName}"(ref(element, context))
                }
                return
            }
            context.currentClassification = element
        }

        if (context.wasPrinted(element)) {
            theMkp.yield {
                "${elementName}"(ref(element, context))
            }
            return
        }

        context.markAsPrinted(element)

        theMkp.yield {
            "${elementName}"(helper.collectAttributes(element, context)) {
                helper.processElements(mkp, element, context)
            }
        }

        if (element instanceof Classification) {
            context.currentClassification = null
        }
    }


    Map<String, Object> collectAttributes(E element, PrintContext context) {
        Map<String, Object> attrs = [name: element.name]

        if (element.classifications && !(context.currentClassification in element.classifications)) {
            attrs.classification = element.classifications.first().name
        }

        if (element.hasModelCatalogueId()) {
            attrs.id = element.modelCatalogueId
        } else {
            attrs.id = element.getDefaultModelCatalogueId(context.idWithoutVersion)
        }

        if (element.status != ElementStatus.FINALIZED) {
            if (element.status in [ElementStatus.DRAFT, ElementStatus.DEPRECATED]) {
                attrs.status = element.status
            } else {
                throw new IllegalArgumentException("Cannot print ${element.getClass().simpleName} with status $element.status")
            }
        }

        attrs
    }

    void processElements(theMkp, E element, PrintContext context) {
        theMkp.yield {
            if (element.description) {
                description element.description
            }
            for (CatalogueElement other in element.isBasedOn) {
                basedOn(ref(other, context))
            }
            for (CatalogueElement other in element.relatedTo) {
                relatedTo(ref(other, context))
            }
            for (CatalogueElement other in element.isSynonymFor) {
                synonym(ref(other, context))
            }
            if (element.ext) {
                extensions {
                    for (Map.Entry<String, String> entry in element.ext.entrySet()) {
                            extension(key: entry.key, entry.value)
                    }
                }
            }




            List<Relationship> outgoing = restOfRelationships(Relationship.where { source == element }).list()
            List<Relationship> incoming = restOfRelationships(Relationship.where { destination == element }).list()

            if (outgoing || incoming) {
                relationships {
                    for (Relationship rel in outgoing){
                        to(relationship(rel, true, context))
                    }

                    for (Relationship rel in incoming){
                        from(relationship(rel, false, context))
                    }
                }
            }
        }
    }

    static DetachedCriteria<Relationship> restOfRelationships(DetachedCriteria<Relationship>  criteria) {
        criteria.not {
            'in' 'relationshipType', ['classification', 'hierarchy', 'containment', 'supersession', 'base', 'relatedTo', 'synonym'].collect { RelationshipType.readByName(it) }
        }
        criteria.'eq' 'archived', false
    }

    static Map<String, Object> ref(CatalogueElement element, PrintContext context) {
        if (element.hasModelCatalogueId()) {
            return [ref: element.modelCatalogueId]
        }

        if (element.classifications) {
            if (context.currentClassification in element.classifications) {
                return [name: element.name]
            } else {
                return [name: element.name, classification: element.classifications.first().name]
            }
        }


        return [ref: element.getDefaultModelCatalogueId(true)]
    }

    static Map<String, Object> relationship(Relationship relationship, boolean outgoing, PrintContext context) {
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
        if (type in [Classification, Model, DataElement, ValueDomain, DataType, MeasurementUnit]) {
            String simpleName = type.simpleName
            return simpleName[0].toLowerCase() + simpleName[1..-1]
        }
        return type.name
    }

    abstract String getTopLevelName()


}
