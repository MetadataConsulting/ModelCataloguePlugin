package org.modelcatalogue.core.xml

import groovy.xml.StreamingMarkupBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ModelService

class CatalogueXmlPrinter {

    ClassificationService classificationService
    ModelService modelService

    CatalogueXmlPrinter(ClassificationService classificationService, ModelService modelService) {
        this.classificationService = classificationService
        this.modelService = modelService
    }

    Writable bind(CatalogueElement element) {
        PrintContext context = new PrintContext(classificationService, modelService)
        StreamingMarkupBuilder builder = new StreamingMarkupBuilder()
        builder.bind {
            catalogue ('xmlns' : 'http://www.metadataregistry.org.uk/assets/schema/1.0/metadataregistry.xsd') {
                CatalogueElementPrintHelper.printElement(mkp, element, context)
            }
        }
    }

}
