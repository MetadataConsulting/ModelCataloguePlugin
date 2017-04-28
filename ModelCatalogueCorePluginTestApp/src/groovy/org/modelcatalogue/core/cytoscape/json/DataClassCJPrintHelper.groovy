package org.modelcatalogue.core.cytoscape.json

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.Relationship

/**
 * Helper for printing DataClasses to Cytoscape JSON. Still a stub.
 * Created by james on 27/04/2017.
 */
@Singleton
class DataClassCJPrintHelper extends CatalogueElementCJPrintHelper<DataClass>{
    final String typeName = "DataClass"
    @Override
    void printElement(DataClass dataClass,
                      CJPrintContext context,
                      String typeName,
                      Relationship relationship = null,
                      boolean recursively = true) {
        super.printElement(dataClass, context, this.typeName, relationship, recursively)
        if (recursively) {
            /**
             * Contains relationships are DataClass to DataElement, so this prints the
             * DataElements contained in this class.
             */
            for (Relationship rel in dataClass.containsRelationships) {
                dispatch(rel.destination, context, rel)
            }

            /**
             * ParentOf relationships are Hierarchy relationships which are DataClass to DataClass,
             * so this prints "subclasses" or something like that.
             */
            for (Relationship rel in dataClass.parentOfRelationships) {
                dispatch(rel.destination, context, rel)
            }
            /**
             * Context for a ValidationRule
             */
            for (Relationship rel in dataClass.contextForRelationships) {
                dispatch(rel.source, context, rel)
            }
        }
    }
}
