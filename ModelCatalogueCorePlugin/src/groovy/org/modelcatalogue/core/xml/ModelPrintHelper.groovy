package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship

/**
 * Created by ladin on 15.01.15.
 */
class ModelPrintHelper extends CatalogueElementPrintHelper<Model> {

    @Override
    String getTopLevelName() {
        "model"
    }

    @Override
    void processElements(Object mkp, Model element, PrintContext context, Relationship relationship) {
        super.processElements(mkp, element, context, relationship)
        for (Relationship rel in element.containsRelationships) {
            printElement(mkp, rel.destination, context, rel)
        }
        for (Relationship rel in element.parentOfRelationships) {
            printElement(mkp, rel.destination, context, rel)
        }
    }
}
