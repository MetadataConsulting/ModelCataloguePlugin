package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.HibernateHelper

/**
 * Implements the visitor pattern for printing CatalogueElements in JSON for Cytoscape.
 * Created by james on 25/04/2017.
 */
abstract class CatalogueElementCJPrintHelper<E extends CatalogueElement> {
    /** Get the appropriate helper for writing a CatalogueElement of a particular class.
     *
     * @param type The class or type of the CatalogueElement to be written.
     * @return The appropriate helper to write the CatalogueElement. */
    private static <E extends CatalogueElement> CatalogueElementCJPrintHelper<E> getHelperForType(Class<E> type) {
        if (DataModel.isAssignableFrom(type)) {
            return DataModelPrintHelper.instance as CatalogueElementCJPrintHelper<E>
        }
        throw new IllegalArgumentException("Not yet implemented for $type")
    }

    static void printElement(element, listOfNodes, listOfEdges, String elementTypeName = null) {
        CatalogueElementCJPrintHelper helper = getHelperForType(HibernateHelper.getEntityClass(element))
        if (!elementTypeName) {
            elementTypeName = helper.getTopLevelTypeName()
        }

    }
    abstract String getTopLevelTypeName()
}
