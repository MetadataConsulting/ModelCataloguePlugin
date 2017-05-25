package org.modelcatalogue.core.cytoscape.json

import grails.gorm.DetachedCriteria
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.HibernateHelper

/**
 * Implements the visitor pattern for printing CatalogueElements in JSON for Cytoscape.
 * This class is the top level dispatcher, dispatching other helpers based on
 * the type of element to be printed.
 * Created by james on 25/04/2017.
 */
abstract class CatalogueElementCJPrintHelper<E extends CatalogueElement> {
    /** Get the appropriate helper for writing a CatalogueElement of a particular type.
     * Each helper should extend CatalogueElementCJPrintHelper and implement
     * the singleton pattern (with the @Singleton annotation).
     * It should override printElement if necessary to add type-specific actions.
     * The overridden printElement hould call super.printElement at the beginning.
     * The helper should not override dispatch; it should call dispatch
     * (which is thus effectively super.dispatch) on an element that it wants to print
     * to return to CatalogueElementCJPrintHelper to dispatch based on the element's type.
     *
     * @param type The class or type of the CatalogueElement to be written.
     * @return The appropriate helper to write the CatalogueElement. */
    private static <E extends CatalogueElement> CatalogueElementCJPrintHelper<E> getHelperForType(Class<E> type) {
        /** May recurse to DataClass */
        if (DataModel.isAssignableFrom(type)) {
            return DataModelCJPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        /** May recurse to DataClass, DataElement, ValidationRule */
        if (DataClass.isAssignableFrom(type)) {
            return DataClassCJPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        /** May recurse to DataType */
        if (DataElement.isAssignableFrom(type)) {
            return DataElementCJPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        /** May recurse to DataClass or MeasurementUnit. Still need to implement some printing. */
        if (DataType.isAssignableFrom(type)) {
            return DataTypeCJPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        if (MeasurementUnit.isAssignableFrom(type)) {
            return MeasurementUnitCJPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        /** May recurse to DataElement. Still need to implement some printing. */
        if (ValidationRule.isAssignableFrom(type)) {
            return ValidationRuleCJPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        throw new IllegalArgumentException("CJPrintHelper not yet implemented for $type")
    }

    /**
     * @param element
     * @param context
     * @param relationship The relationship by which the element was reached in the printing process.
     */
    static void dispatch(CatalogueElement element,
                         CJPrintContext context,
                         Relationship relationship = null) {
        CatalogueElementCJPrintHelper helper = getHelperForType(HibernateHelper.getEntityClass(element))
        if (context.wasPrinted(element)) { // element was printed but relationship was not
            printRelationship(context, relationship)
        }
        else {
            context.markAsPrinted(element)
            helper.printElement(element, context, "", relationship,
            context.printRecursively(element))
        }

    }

    /**
     * Print the element node with id, name, type, data model, description, metadata,
     * and the relationship, to the context. Plus, if recursively, then go down all other relationships.
     * @param element
     * @param context
     * @param relationship
     */
    void printElement(E element,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {
        def elementId = element.getDefaultModelCatalogueId(false)
        def data = ["id": elementId, "name": element.name, "type": typeName]
        if (element.dataModel) {
            data['dataModel'] = element.dataModel.name
        }
        if (element.description) {
            data['description'] = element.description
        }
        data['metadata'] = element.ext ?: [:]/*(element.ext ? // Maybe don't need to getFragment after # in the key?
                (element.ext.collectEntries{key, value -> [new URI(key).getFragment(), value]})
                : [:])*/
        context.listOfNodes << ["data": data,
                            "position": ["x": 0, "y": 0]]
        printRelationship(context, relationship)
        if (recursively) { // the following is basically adapted from the XML processElements
            for (Relationship rel in element.isBasedOnRelationships) {
                dispatch(rel.destination, context, rel)
            }
            for (Relationship rel in element.relatedToRelationships) {
                CatalogueElement other = rel.source == element ? rel.destination : rel.source
                dispatch(other, context, rel)
            }
            for (Relationship rel in element.isSynonymForRelationships) {
                CatalogueElement other = rel.source == element ? rel.destination : rel.source
                dispatch(other, context, rel)
            }
            List<Relationship> outgoing = restOfRelationships(Relationship.where { source == element && relationshipType.system != true}).list()
            List<Relationship> incoming = restOfRelationships(Relationship.where { destination == element  && relationshipType.system != true}).list()
            for (Relationship rel in outgoing) {
                dispatch(rel.destination, context, rel)
            }
            for (Relationship rel in incoming) {
                dispatch(rel.source, context, rel)
            }
        }
    }
    /**
     * Print relationship for cytoscape with extra items archived, inherited, metadata
     * to context.listOfEdges.
     * (Responsible for checking if null)
     * The source and destination should already have been printed to context.listOfNodes.
     * @param context
     * @param relationship
     */
    static void printRelationship(CJPrintContext context, Relationship relationship) {
        if (relationship) {
            def sourceId = relationship.source.getDefaultModelCatalogueId(false)
            def destinationId = relationship.destination.getDefaultModelCatalogueId(false)
            def typeName = relationship.relationshipType.name
            def name = (GrailsClassUtils.
                getStaticFieldValue(HibernateHelper.
                    getEntityClass(relationship.
                        source), 'relationships') ?: [:])['outgoing'][typeName] // a huge kludge to get the relationship name.
            def data = ["id": sourceId+typeName+destinationId,
                        "name": name,
                        "type": typeName,
                        "source": sourceId,
                        "target": destinationId,
                        "archived": relationship.archived ? true : false,
                        "inherited": relationship.inherited ? true : false]
            data['metadata'] = relationship.ext ?: [:]/*(relationship.ext ?
                (relationship.ext.collectEntries{key, value -> [new URI(key).getFragment(), value]})
                : [:])*/
            context.listOfEdges << ["data": data]
        }
    }

    /** Description of the type of element, eg "DataModel" */
    final abstract String typeName
    static DetachedCriteria<Relationship> restOfRelationships(DetachedCriteria<Relationship>  criteria) {
        /*criteria.not {
            'in' 'relationshipType', ['declaration', 'hierarchy', 'containment', 'supersession', 'base', 'relatedTo', 'synonym', 'involvedness', 'ruleContext'].collect { RelationshipType.readByName(it) }
        }
        criteria.'eq' 'archived', false*/
        criteria
    }
}
