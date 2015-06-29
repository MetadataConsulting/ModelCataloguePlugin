package org.modelcatalogue.core.xml

import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ModelService

class CatalogueXmlPrinter {

    static final String NAMESPACE_URL = 'http://www.metadataregistry.org.uk/assets/schema/1.1/metadataregistry.xsd'

    ClassificationService classificationService
    ModelService modelService

    CatalogueXmlPrinter(ClassificationService classificationService, ModelService modelService) {
        this.classificationService = classificationService
        this.modelService = modelService
    }

    Writable bind(CatalogueElement element, @DelegatesTo(PrintContext) Closure contextConfigurer = {}) {
        PrintContext context = new PrintContext(classificationService, modelService)
        context.with contextConfigurer


        return { Writer writer ->
            EscapeSpecialWriter escapeSpecialWriter = new EscapeSpecialWriter(writer)
            MarkupBuilder builder = new MarkupBuilder(escapeSpecialWriter)
            builder.doubleQuotes = true
            builder.catalogue ('xmlns' : NAMESPACE_URL) {
                CatalogueElementPrintHelper.printElement(builder, element, context, null)
            }

            writer
        } as Writable
    }

}
