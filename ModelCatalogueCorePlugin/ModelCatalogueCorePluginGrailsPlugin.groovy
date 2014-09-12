import grails.rest.render.RenderContext
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.marshalling.*
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer

class ModelCatalogueCorePluginGrailsPlugin {
    // the plugin version
    def version = "0.5.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/index.gsp",
            "grails-app/assets/javascripts/demo.coffee"
    ]

    // TODO Fill in these fields
    def title = "Model Catalogue Core Plugin " // Headline display name of the plugin
    def author = "Adam Milward, Vladimír Oraný"
    def authorEmail = "adam.milward@outlook.com, vladimir@orany.cz"
    def description = '''\
Model catalogue core plugin (metadata registry)
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/model-catalogue-core-plugin"

    //def packaging = "binary"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "MIT"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }


    def doWithSpring = {
        mergeConfig(application)

        xlsxListRenderer(XLSXListRenderer)
        reportsRegistry(ReportsRegistry)

        modelCatalogueCorePluginCustomObjectMarshallers(ModelCatalogueCorePluginCustomObjectMarshallers) {
            marshallers = [
                    new AssetMarshaller(),
                    new ConceptualDomainMarshaller(),
                    new ClassificationMarshaller(),
                    new DataElementMarshaller(),
                    new DataTypeMarshaller(),
                    new ElementsMarshaller(),
                    new ValueDomainsMarshaller(),
                    new PublishedElementsMarshaller(),
                    new EnumeratedTypeMarshaller(),
                    new MeasurementUnitMarshallers(),
                    new ModelMarshaller(),
                    new RelationshipTypeMarshaller(),
                    new RelationshipMarshallers(),
                    new RelationshipsMarshaller(),
                    new ValueDomainMarshaller(),
                    new MappingMarshallers(),
                    new MappingsMarshaller(),
                    new ImportRowMarshaller(),
                    new ImportRowsMarshaller(),
                    new DataImportMarshaller(),
                    new ListWithTotalAndTypeWrapperMarshaller(),
                    new BatchMarshaller(),
                    new ActionMarshaller()
            ]
        }

    }

    def doWithDynamicMethods = { ctx ->
        ctx.grailsApplication.domainClasses.each {
            if (CatalogueElement.isAssignableFrom(it.clazz)) {
                CatalogueElementDynamicHelper.addShortcuts(it.clazz)
            }
        }
    }

    def doWithApplicationContext = { ctx ->
        //register custom json Marshallers
        //ctx.domainModellerService.modelDomains()
        ctx.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()

        XLSXListRenderer xlsxListRenderer = ctx.getBean(XLSXListRenderer)

        xlsxListRenderer.registerRowWriter {
            title "Catalogue Elements to Excel"
            headers 'ID', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing'] && (!container.itemType || CatalogueElement.isAssignableFrom(container.itemType))
            } then { CatalogueElement element ->
                [[element.id, element.name, element.description]]
            }
        }

        xlsxListRenderer.registerRowWriter {
            title "DataTypes to Excel"
            headers 'Model Catalogue ID', 'Name', 'Enumerations', 'Value Domain'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing'] && (!container.itemType || CatalogueElement.isAssignableFrom(container.itemType))
            } then { DataType dataType ->
                [[dataType.id, dataType.name, getEnumerationString(dataType), getValueDomainString(dataType)]]
            }
        }

        xlsxListRenderer.registerRowWriter {
            title "ValueDomains to Excel"
            headers 'Model Catalogue ID', 'Name', 'Conceptual Domains', 'Unit of Measurement', 'Rules', 'Data Type Model Catalogue ID', 'DataType Name', 'Data Type Enumeration'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing'] && (!container.itemType || CatalogueElement.isAssignableFrom(container.itemType))
            } then { ValueDomain valueDomain ->
                [[valueDomain.id, valueDomain.name, getConceptualDomainString(valueDomain), valueDomain.unitOfMeasure, getValueDomainRuleString(valueDomain), valueDomain.dataTypeId, valueDomain.dataType.name, getEnumerationString(valueDomain.dataType)]]
            }
        }
        xlsxListRenderer.registerRowWriter {
            title "Relationship Types to Excel"
            headers 'Name', 'Source to Destination', 'Destination to Source'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search'] && (!container.itemType || RelationshipType.isAssignableFrom(container.itemType))
            } then { RelationshipType type ->
                [[type.name, type.sourceToDestination, type.destinationToSource]]
            }
        }

        xlsxListRenderer.registerRowWriter {
            title "Current Relations"
            headers 'Type', 'Source', 'Destination'
            when { container, context ->
                container instanceof Relationships
            } then { Relationship r ->
                [[r.relationshipType, r.source.name, r.destination.name]]
            }
        }

        xlsxListRenderer.registerRowWriter('NHIC') {
            title "NHIC"
            headers "Classification", "Parent Model Unique Code",
            "Parent Model", "Model Unique Code", "Model",
            "Data Item Unique Code", "Data Item Name", "Data Item Description",
            "Measurement Unit", "Data type", "Metadata",
            "NHIC_Identifier", "Link_to_existing_definition", "Notes_from_GD_JCIS",
            "Optional_Local_Identifier", "A", "B",
            "C", "D", "E","F",
            "G", "H", "E2", "System",
            "Comments", "Group", "More-comments", "Multiplicity",
            "Temp", "Index", "NIHR Code", "Section_0",
            "Section_1", "Section_2", "Section_3", "Supporting",
            "Associated date and time", "Given Data type", "Template", "List content",
            "Timing of Data Collection", "Source UCH", "label1 - UCH", "label2 - UCH",
            "More metadata1", "Reference", "ranges - UCH", "Cambridge",
            "Source Cambridge", "Type of Anonymisation", "Data Dictionary Element", "Link to existing definition",
            "Anonymizing Rules", "File Name (COSD XSD)", "XSD Element Name",
            "Original Data Item Name (COSD v1.2 xls)","Pathway Group"
//            headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "Data item No.","Schema Specification","Data Dictionary Element", "Current Collection", "Format"
            when { ListWrapper container, RenderContext context ->
                context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && container.itemType && DataElement.isAssignableFrom(container.itemType)
            } then { DataElement element ->
                [[getClassificationString(element), getParentModel(element)?.modelCatalogueId,
                  getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name,
                  element.modelCatalogueId, element.name, element.description,
                  getUnitOfMeasure(element), getDataType(element), "-",
                  element.ext.get("NHIC_Identifier"), element.ext.get("Link_to_existing_definition"), element.ext.get("Notes_from_GD_JCIS"),
                  element.ext.get("Optional_Local_Identifier"), element.ext.get("A"), element.ext.get("B"),
                  element.ext.get("C"), element.ext.get("D"), element.ext.get("E"), element.ext.get("F"),
                  element.ext.get("G"), element.ext.get("H"), element.ext.get("E2"), element.ext.get("System"),
                  element.ext.get("Comments"), element.ext.get("Group"), element.ext.get("More-comments"), element.ext.get("Multiplicity"),
                  element.ext.get("Temp"), element.ext.get("Index"), element.ext.get("NIHR Code"), element.ext.get("Section_0"),
                  element.ext.get("Section_1"), element.ext.get("Section_2"), element.ext.get("Section_3"), element.ext.get("Supporting"),
                  element.ext.get("Associated date and time"), element.ext.get("Given Data type"), element.ext.get("Template"), element.ext.get("List content"),
                  element.ext.get("Timing of Data Collection"), element.ext.get("Source UCH"), element.ext.get("label1 - UCH"), element.ext.get("label2 - UCH"),
                  element.ext.get("More metadata1"), element.ext.get("Reference"), element.ext.get("ranges - UCH"), element.ext.get("Cambridge"),
                  element.ext.get("Source Cambridge"), element.ext.get("Type of Anonymisation"), element.ext.get("Data Dictionary Element"), element.ext.get("Link to existing definition"),
                  element.ext.get("Anonymizing Rules"), element.ext.get("File Name (COSD XSD)"), element.ext.get("XSD Element Name"),
                  element.ext.get("Original Data Item Name (COSD v1.2 xls)"), element.ext.get("Pathway Group")]]
            }
        }

//        element.ext.NHIC_Identifier, element.ext.Link_to_existing_definition, element.ext.Notes_from_GD_JCIS , element.ext.Optional_Local_Identifier, element.ext.A, element.ext.B, element.ext.C , element.ext.D , element.ext.E , element.ext.F , element.ext.G, element.ext.H, element.ext.E2, element.ext.System, element.ext.Comments, element.ext.Group]]
//EXAMPLE OF the kinds of reports you can configure:
//
//        xlsxListRenderer.registerRowWriter('COSD') {
//            title 'Export All to COSD'
//            headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "Data item No.","Schema Specification","Data Dictionary Element", "Current Collection", "Format"
//            when { ListWrapper container, RenderContext context ->
//                context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && container.itemType && DataElement.isAssignableFrom(container.itemType)
//            } then { DataElement element ->
//                [[getParentModel(element)?.modelCatalogueId, getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name, element.modelCatalogueId, element.name, element.description, getUnitOfMeasure(element), getDataType(element), "-", element.ext.get("Data item No."), element.ext.get("Schema Specification"), element.ext.get("Data Dictionary Element"), element.ext.get("Current Collection"), element.ext.get("Format") ]]
//            }
//        }
//
//        xlsxListRenderer.registerRowWriter('NHIC') {
//            title "Export All to NHIC"
//            headers "Parent Model Unique Code",	"Parent Model",	"Model Unique Code", "Model", "Data Item Unique Code", "Data Item Name", "Data Item Description", "Measurement Unit", "Data type",	"Metadata", "NHIC_Identifier","Link_to_existing_definition", "Notes_from_GD_JCIS" ,"Optional_Local_Identifier","A" ,"B","C" ,"D" ,"E" ,"F" ,"G","H","E2", "System", "Comments", "Group"
//            when { ListWrapper container, RenderContext context ->
//                context.actionName in ['index', 'search', 'metadataKeyCheck', 'uninstantiatedDataElements', 'getSubModelElements'] && container.itemType && DataElement.isAssignableFrom(container.itemType)
//            } then { DataElement element ->
//                [[getParentModel(element)?.modelCatalogueId, getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name, element.modelCatalogueId, element.name, element.description, getUnitOfMeasure(element), getDataType(element), "-", element.ext.NHIC_Identifier, element.ext.Link_to_existing_definition, element.ext.Notes_from_GD_JCIS , element.ext.Optional_Local_Identifier, element.ext.A, element.ext.B, element.ext.C , element.ext.D , element.ext.E , element.ext.F , element.ext.G, element.ext.H, element.ext.E2, element.ext.System, element.ext.Comments, element.ext.Group]]
//            }
//        }

        ReportsRegistry reportsRegistry = ctx.getBean(ReportsRegistry)

        reportsRegistry.register {
            creates asset
            title { "Export All Elements of ${it.name} to XML" }
            type Model
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xml'], id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Export All Elements of ${it.name} to Excel XSLX" }
            type Model
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report:'NHIC'], id: true
        }

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }

    protected static mergeConfig(GrailsApplication application){
        application.config.merge(loadConfig(application))
    }

    protected static loadConfig(GrailsApplication application){
        new ConfigSlurper(Environment.current.name).parse(application.classLoader.loadClass("ModelCatalogueConfig"))
    }



    def static getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return null
    }

    def static getParentModel(DataElement dataElement){
        Model containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    def static getValueDomain(DataElement dataElement){
        return dataElement.valueDomain
    }

    def static getDataType(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            DataType dataType = valueDomain.dataType
            if (dataType instanceof EnumeratedType) {
                return dataType.enumAsString
            }
            return dataType.name
        }
        return null
    }

    def static getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return null
    }
    
    def static getClassificationString(DataElement dataElement){
        String classifications = ""
        dataElement.classifications.eachWithIndex{ def classification, int i ->
            if (classifications != "") classifications += (", " + classification.name)
            else classifications = classification.name
        }
        return classifications
    }

    def static getValueDomainString(DataType dataType){
        String valueDomains = ""

        dataType.relatedValueDomains.eachWithIndex{ ValueDomain valueDomain, int i ->
            String vdText = valueDomain.id + " \t " + valueDomain.name + " \t "
            if (valueDomains!="") valueDomains += ("\r\n" + vdText )
            else valueDomains = vdText
        }
        return valueDomains
    }

    def static getEnumerationString(DataType dataType){
        if (dataType instanceof EnumeratedType) {
            return dataType.enumAsString
        }
        return null
    }

    def static getConceptualDomainString(ValueDomain valueDomain){
        String conceptualDomains = ""

        valueDomain.conceptualDomains.eachWithIndex{ ConceptualDomain conceptualDomain, int i ->
            if (conceptualDomains!="") conceptualDomains += ("\r\n" + conceptualDomain.name )
            else conceptualDomains = conceptualDomain.name
        }
        return conceptualDomains
    }

    def static getValueDomainRuleString(ValueDomain valueDomain){
        String rules = ""
        valueDomain.rule.eachWithIndex{ String rule, int i ->
            if (rules!="") rules += ("\r\n" + rule )
            else rules = rule
        }
        return rules
    }
}
