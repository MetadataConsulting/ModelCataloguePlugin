package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.Relationship

/** Helper for printing Data Classes */
@Singleton
class DataClassPrintHelper extends CatalogueElementPrintHelper<DataClass> {

    @Override
    String getTopLevelName() {
        "dataClass"
    }

    @Override
    void processElement(Object markupBuilder, DataClass element, PrintContext context, Relationship relationship) {
        super.processElement(markupBuilder, element, context, relationship)

        /** But how can we get these relationships? These must be dynamic methods.
         * Vlad: These are dynamic methods defined in core.util.CatalogueElementDynamicHelper.
         */
        /**
         * Contains relationships are DataClass to DataElement, so this prints the
         * DataElements contained in this class.
         */
        for (Relationship rel in element.containsRelationships) {
            dispatch(markupBuilder, rel.destination, context, rel)
        }

        /**
         * ParentOf relationships are Hierarchy relationships which are DataClass to DataClass,
         * so this prints "subclasses" or something like that.
         */
        for (Relationship rel in element.parentOfRelationships) {
            dispatch(markupBuilder, rel.destination, context, rel)
        }
        /**
         * Context for a ValidationRule
         */
        for (Relationship rel in element.contextForRelationships) {
            dispatch(markupBuilder, rel.source, context, rel)
        }
    }
}
