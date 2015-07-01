package org.modelcatalogue.core.xml

import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ModelService
import org.modelcatalogue.core.RelationshipType

class CatalogueXmlPrinter {

    static final String NAMESPACE_URL = 'http://www.metadataregistry.org.uk/assets/schema/1.1.2/metadataregistry.xsd'

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
                printRelationshipTypes(builder, context)
            }

            writer
        } as Writable
    }

    private static void printRelationshipTypes(MarkupBuilder builder, PrintContext context) {
        if (!context.typesUsed) {
            return
        }
        builder.mkp.comment("Relationship types are only imported if and only if they are not present in the catalogue yet. Any subsequent changes are ignored! For non-admin users, the types are always imported as system ones and they need to be approved by the catalogue admin first.")
        builder.relationshipTypes {
            for (String relationshipTypeName in context.typesUsed) {
                RelationshipType type = RelationshipType.readByName(relationshipTypeName)
                relationshipType(collectRelationshipTypeAttrs(type)) {
                    sourceToDestination(label: type.sourceToDestination, type.sourceToDestinationDescription)
                    destinationToSource(label: type.destinationToSource, type.destinationToSourceDescription)
                    if (type.rule) {
                        rule type.rule
                    }
                }
            }
        }
    }


    static Map<String, Object> collectRelationshipTypeAttrs(RelationshipType relationshipType) {
        Map<String, Object> ret = [name: relationshipType.name, source: relationshipType.sourceClass.name, destination: relationshipType.destinationClass.name]

        if (relationshipType.system) {
            ret.system = true
        }

        if (relationshipType.versionSpecific) {
            ret.versionSpecific = true
        }

        if (relationshipType.bidirectional) {
            ret.bidirectional = true
        }

        ret
    }

}
