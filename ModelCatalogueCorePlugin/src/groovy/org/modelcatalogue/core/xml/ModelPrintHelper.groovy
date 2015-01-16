package org.modelcatalogue.core.xml

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Model

/**
 * Created by ladin on 15.01.15.
 */
class ModelPrintHelper extends CatalogueElementPrintHelper<Model> {

    @Override
    String getTopLevelName() {
        "model"
    }

    @Override
    void processElements(Object theMkp, Model element, PrintContext context) {
        theMkp.yield {
            super.processElements(mkp, element, context)
            for (CatalogueElement other in element.contains) {
                printElement(mkp, other, context)
            }
            for (CatalogueElement other in element.parentOf) {
                printElement(mkp, other, context)
            }
        }
    }
}
