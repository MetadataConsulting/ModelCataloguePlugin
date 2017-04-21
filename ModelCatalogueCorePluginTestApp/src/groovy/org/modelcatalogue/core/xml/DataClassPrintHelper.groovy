package org.modelcatalogue.core.xml

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.Relationship

@Singleton
class DataClassPrintHelper extends CatalogueElementPrintHelper<DataClass> {

    @Override
    String getTopLevelName() {
        "dataClass"
    }

    @Override
    void processElements(Object mkp, DataClass element, PrintContext context, Relationship relationship) {
        super.processElements(mkp, element, context, relationship)
        for (Relationship rel in element.containsRelationships) {
            printElement(mkp, rel.destination, context, rel)
        }
        for (Relationship rel in element.parentOfRelationships) {
            printElement(mkp, rel.destination, context, rel)
        }
        for (Relationship rel in element.contextForRelationships) {
            printElement(mkp, rel.source, context, rel)
        }
    }
}
