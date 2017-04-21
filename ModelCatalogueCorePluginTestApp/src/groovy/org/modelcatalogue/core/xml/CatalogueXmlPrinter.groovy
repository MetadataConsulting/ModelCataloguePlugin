package org.modelcatalogue.core.xml

import grails.util.Holders
import groovy.xml.MarkupBuilder
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataModelPolicy
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.policy.Convention
import org.modelcatalogue.core.policy.Conventions

class CatalogueXmlPrinter {

    static final String NAMESPACE_URL = 'http://www.metadataregistry.org.uk/assets/schema/2.2/metadataregistry.xsd'
    static final String ASSETS_NAMESPACE_URL = 'http://www.metadataregistry.org.uk/assets/schema/2.0/metadataregistry_asset.xsd'

    // These are used for ...
    DataModelService dataModelService
    DataClassService modelService

    CatalogueXmlPrinter(DataModelService dataModelService, DataClassService modelService) {
        this.dataModelService = dataModelService
        this.modelService = modelService
    }

    Writable bind(CatalogueElement element, @DelegatesTo(PrintContext) Closure contextConfigurer = {}) {
        PrintContext context = new PrintContext(dataModelService, modelService)
        context.with contextConfigurer

        Map<String, String> ns = [xmlns : NAMESPACE_URL]

        if (element.instanceOf(Asset)) {
            ns.xmlns = ASSETS_NAMESPACE_URL
        }

        return { Writer writer ->
            EscapeSpecialWriter escapeSpecialWriter = new EscapeSpecialWriter(writer)
            MarkupBuilder builder = new MarkupBuilder(escapeSpecialWriter)
            builder.doubleQuotes = true
            builder.catalogue (ns) {
                CatalogueElementPrintHelper.printElement(builder, element, context, null)
                printRelationshipTypes(builder, context)
                printPolicies(builder, context)
            }

            writer
        } as Writable
    }

    public <CE extends CatalogueElement> Writable bind(Iterable<CE> elements, @DelegatesTo(PrintContext) Closure contextConfigurer = {}) {
        PrintContext context = new PrintContext(dataModelService, modelService)
        context.with contextConfigurer

        return { Writer writer ->
            EscapeSpecialWriter escapeSpecialWriter = new EscapeSpecialWriter(writer)
            MarkupBuilder builder = new MarkupBuilder(escapeSpecialWriter)
            builder.doubleQuotes = true
            builder.catalogue (xmlns : NAMESPACE_URL) {
                for (CE element in elements) {
                    CatalogueElementPrintHelper.printElement(builder, element, context, null)
                }
                printRelationshipTypes(builder, context)
                printPolicies(builder, context)
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

    private static void printPolicies(MarkupBuilder builder, PrintContext context) {
        if (!context.policiesUsed) {
            return
        }
        builder.mkp.comment("Policies are only imported if and only if they are not present in the catalogue yet. Any subsequent changes are ignored!")
        builder.dataModelPolicies {
            for (String policyName in context.policiesUsed) {
                DataModelPolicy policy = DataModelPolicy.findByName(policyName)
                if (!policy) {
                    mkp.comment("Policy '$policyName' is missing in the catalogue.")
                    continue
                }
                dataModelPolicy(name: policyName) {
                    for (Convention c in policy.policy.conventions) {
                        convention {
                            target Conventions.getClassNameOrShortcut(c.target)
                            if (Conventions.isExtensionAlias(c.property)) {
                                extension Conventions.getExtension(c.property)
                            } else {
                                property c.property
                            }
                            type Conventions.getCheckerName(c.checker)
                            if (c.configuration) {
                                argument c.configuration
                            }
                        }
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

    /**
     * Dumps the element to file for debugging purposes.
     * @param element element to be dumped
     * @param path path to the file
     */
    static PipedOutputStream readStream(CatalogueElement element) {

        PipedInputStream input = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(input);

        out.withWriter {
            new CatalogueXmlPrinter(Holders.applicationContext.getBean(DataModelService), Holders.applicationContext.getBean(DataClassService)).bind(element).writeTo(it)
        }
    }

    /**
     * Dumps the element to file for debugging purposes.
     * @param element element to be dumped
     * @param path path to the file
     */
    static void dump(CatalogueElement element, String path) {
        File file = new File(path)
        file.parentFile.mkdirs()
        file.createNewFile()
        file.withWriter {
            new CatalogueXmlPrinter(Holders.applicationContext.getBean(DataModelService), Holders.applicationContext.getBean(DataClassService)).bind(element).writeTo(it)
        }
    }

}
