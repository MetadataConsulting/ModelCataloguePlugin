package org.modelcatalogue.core.xml

import groovy.xml.StreamingMarkupBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ModelService

class CatalogueXmlPrinter {

    static final String NAMESPACE_URL = 'http://www.metadataregistry.org.uk/assets/schema/1.0/metadataregistry.xsd'

    ClassificationService classificationService
    ModelService modelService

    CatalogueXmlPrinter(ClassificationService classificationService, ModelService modelService) {
        this.classificationService = classificationService
        this.modelService = modelService
    }

    Writable bind(CatalogueElement element, @DelegatesTo(PrintContext) Closure contextConfigurer = {}) {
        PrintContext context = new PrintContext(classificationService, modelService)
        context.with contextConfigurer
        StreamingMarkupBuilder builder = new StreamingMarkupBuilder()
        builder.bind {
            catalogue ('xmlns' : NAMESPACE_URL) {
                CatalogueElementPrintHelper.printElement(mkp, element, context)
            }
        }
    }

}
