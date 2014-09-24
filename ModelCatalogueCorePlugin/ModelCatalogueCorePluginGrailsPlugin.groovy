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

    private static String stringSeparator = "\r\n"

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
                    new ActionMarshaller(),
                    new CsvTransformationMarshaller()
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

        xlsxListRenderer.registerRowWriter ('Classifications'){
            title "Classifications to Excel"
            headers  'Model Catalogue ID',  'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                    context.actionName in [null, 'index', 'search', 'incoming', 'outgoing'] && (!container.itemType || Classification.isAssignableFrom(container.itemType))
            } then { Classification classification ->
                [[ classification.modelCatalogueId,  classification.name, classification.description]]
            }
        }

        xlsxListRenderer.registerRowWriter ('ConceptualDomainsFromContextRelationship'){
            title "Conceptual Domains to Excel"
            headers  'Model Catalogue ID',  'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                ((context.actionName in [null,  'incoming' ] ) && (context.getWebRequest().getParams().get("type") in ['context'])  && (!container.itemType || Relationship.isAssignableFrom(container.itemType)) && (context.controllerName == 'model') )
            } then { Relationship relationship ->
                [[ relationship.source.modelCatalogueId,  relationship.source.name, relationship.source.description]]
            }
        }

        xlsxListRenderer.registerRowWriter ('ConceptualDomains'){
            title "Conceptual Domains to Excel"
            headers  'Model Catalogue ID',  'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                ((context.actionName in [null,  'index' ] ) && (context.getWebRequest().getParams().get("type") in [null])  && (!container.itemType || ConceptualDomain.isAssignableFrom(container.itemType)) && (context.controllerName == 'model') )
            } then { ConceptualDomain conceptualDomain ->
                [[ conceptualDomain.modelCatalogueId, conceptualDomain.name, conceptualDomain.description]]
            }
        }

        xlsxListRenderer.registerRowWriter ('ConceptualDomainsIndex') {
            title "Conceptual Domains to Excel"
            //headers ' Model Catalogue ID',  'Symbol', 'Unit of Measurement', 'Description'
            headers  'Model Catalogue ID',  'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index'] && (!container.itemType || ConceptualDomain.isAssignableFrom(container.itemType))
            } then { ConceptualDomain conceptualDomain ->
                [[ conceptualDomain.modelCatalogueId, conceptualDomain.name, conceptualDomain.description]]
            }
        }


        xlsxListRenderer.registerRowWriter ('DataTypes') {
            title "DataTypes to Excel"
            headers 'Model Catalogue ID', 'Name', 'Enumerations', 'Value Domains'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing', 'properties'] && (!container.itemType || DataType.isAssignableFrom(container.itemType))
            } then { DataType dataType ->
                [[dataType.id, dataType.name, getEnumerationString(dataType), getValueDomainString(dataType)]]
            }
        }


        xlsxListRenderer.registerRowWriter ('ValueDomains') {
            title "ValueDomains to Excel"
            headers 'Model Catalogue ID', 'Name', 'Conceptual Domains', 'Unit of Measurement', 'Rules', 'Data Type Model Catalogue ID', 'DataType Name', 'Data Type Enumeration'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing', 'valueDomains'] && (!container.itemType || ValueDomain.isAssignableFrom(container.itemType))
            } then { ValueDomain valueDomain ->
                [[valueDomain.modelCatalogueId, valueDomain.name, getConceptualDomainString(valueDomain), valueDomain.unitOfMeasure, getValueDomainRuleString(valueDomain), valueDomain.dataTypeId, valueDomain.dataType.name, getEnumerationString(valueDomain.dataType)]]
            }
        }

        xlsxListRenderer.registerRowWriter ('MeasurementUnits'){
            title "Measurement Units to Excel"
            headers  'Model Catalogue ID', 'Symbol', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing'] && (!container.itemType || MeasurementUnit.isAssignableFrom(container.itemType))
            } then { MeasurementUnit measurementUnit ->
                [[ measurementUnit.modelCatalogueId, measurementUnit.symbol, measurementUnit.name, measurementUnit.description]]
            }
        }

        xlsxListRenderer.registerRowWriter ('Relationships') {
            title "Relationships to Excel"
            headers 'Source Model Catalogue Id', 'Source Name','Relationship','Destination Model Catalogue Id', 'Destination Name'
            when { ListWrapper container, RenderContext context ->
                (context.actionName in [null, 'index', 'search','relationships'] && (!container.itemType || Relationship.isAssignableFrom(container.itemType)))
            } then { Relationship relationship ->
                [[relationship.source.modelCatalogueId, relationship.source.name, relationship.relationshipType.sourceToDestination, relationship.destination.modelCatalogueId, relationship.destination.name]]
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

        //   *******  MODELS  **********
        xlsxListRenderer.registerRowWriter ('Models') {
            title "Models to Excel"
            headers  'Model Catalogue ID', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null,'index'] && (!container.itemType || Model.isAssignableFrom(container.itemType))
            } then { Model model ->
                [[ model.modelCatalogueId, model.name, model.description]]
            }
        }
        xlsxListRenderer.registerRowWriter ('ModelsOutgoingContextRelationshipsContext'){
            title "Models to Excel"
            headers  'Source Model Catalogue Id', 'Source Name','Relationship','Destination Model Catalogue Id', 'Destination Name'
            when { ListWrapper container, RenderContext context ->
                (!container.itemType || Relationship.isAssignableFrom(container.itemType) && (context.actionName in [null,  'outgoing' ] ) && (context.getWebRequest().getParams().get("type") in ['context', 'hierarchy']) )
            } then { Relationship relationship ->
                [[ relationship.source.modelCatalogueId, relationship.source.name, relationship.relationshipType.sourceToDestination, relationship.destination.modelCatalogueId, relationship.destination.name]]
                //relationship.source.modelCatalogueId, relationship.source.name, relationship.relationshipType.sourceToDestination, relationship.destination.modelCatalogueId, relationship.destination.name
            }
        }

        xlsxListRenderer.registerRowWriter ('ModelsIncomingContextRelationshipsContext'){
            title "Models to Excel"
            headers 'Source Model Catalogue Id', 'Source Name','Relationship','Destination Model Catalogue Id', 'Destination Name'// 'Model Catalogue ID', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                (!container.itemType || Relationship.isAssignableFrom(container.itemType) && ((context.actionName in [null,  'incoming' ] ) && (context.getWebRequest().getParams().get("type") in ['context']) && (context.controllerName != 'model') ))
            } then { Relationship relationship ->
                [[ relationship.source.modelCatalogueId, relationship.source.name, relationship.relationshipType.sourceToDestination, relationship.destination.modelCatalogueId, relationship.destination.name]]
            }
        }
        xlsxListRenderer.registerRowWriter ('ModelsIncomingContextRelationshipsHierarchy'){
            title "Models to Excel"
            headers  'Source Model Catalogue Id', 'Source Name','Relationship','Destination Model Catalogue Id', 'Destination Name'//'Model Catalogue ID', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                (!container.itemType || Relationship.isAssignableFrom(container.itemType) && ((context.actionName in [null,  'incoming' ] ) && (context.getWebRequest().getParams().get("type") in ['hierarchy'])&& (context.controllerName == 'model') ))
            } then { Relationship relationship ->
                [[ relationship.source.modelCatalogueId, relationship.source.name, relationship.relationshipType.sourceToDestination, relationship.destination.modelCatalogueId, relationship.destination.name]]
            }
        }

        xlsxListRenderer.registerRowWriter ('ModelsIncomingContextRelationshipsContainment'){
            title "Models to Excel"
            headers 'Source Model Catalogue Id', 'Source Name','Relationship','Destination Model Catalogue Id', 'Destination Name'// 'Model Catalogue ID', 'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                (!container.itemType || Relationship.isAssignableFrom(container.itemType) && ((context.actionName in [null,  'incoming' ] ) && (context.getWebRequest().getParams().get("type") in ['containment'])&& (context.controllerName == 'dataElement') ))
            } then { Relationship relationship ->
                [[ relationship.source.modelCatalogueId, relationship.source.name, relationship.relationshipType.sourceToDestination, relationship.destination.modelCatalogueId, relationship.destination.name]]
            }
        }

        xlsxListRenderer.registerRowWriter('NHIC') {
            title "Data Elements to Excel"
            append metadata
            headers "Classification", "Conceptual Domain", "Parent Model Unique Code",
            "Parent Model", "Model Unique Code", "Model",
            "Data Item Unique Code", "Data Item Name", "Data Item Description",
            "Measurement Unit","Measurement Unit Symbol", "Data type", "Metadata"

            when { ListWrapper container, RenderContext context ->
                container.itemType && DataElement.isAssignableFrom(container.itemType)
            } then { DataElement element ->
                [[getClassificationString(element), getConceptualDomainString(element), getParentModel(element)?.modelCatalogueId,
                  getParentModel(element)?.name, getContainingModel(element)?.modelCatalogueId, getContainingModel(element)?.name,
                  element.modelCatalogueId, element.name, element.description,
                  getUnitOfMeasure(element), getUnitOfMeasureSymbol(element) , getDataType(element), "-"]]
            }
        }

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
    def static getConceptualDomainString(DataElement dataElement){
        return getConceptualDomainString(dataElement.valueDomain)
    }




    def static getValueDomain(DataElement dataElement){
        return dataElement.valueDomain
    }

    def static getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return null
    }

    def static getUnitOfMeasureSymbol(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.symbol
        }
        return null
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

    def static getClassificationString(DataElement dataElement){
        String classifications = ""
        dataElement.classifications.eachWithIndex{ def classification, int i ->
            if (classifications != "") classifications += (stringSeparator + classification.name)
            else classifications = classification.name
        }
        return classifications
    }

    def static getValueDomainString(DataType dataType){
        String valueDomains = ""

        dataType.relatedValueDomains.eachWithIndex{ ValueDomain valueDomain, int i ->
            String vdText = valueDomain.id + " \t " + valueDomain.name + " \t "
            if (valueDomains!="") valueDomains += (stringSeparator + vdText )
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
            if (conceptualDomains!="") conceptualDomains += (stringSeparator + conceptualDomain.name )
            else conceptualDomains = conceptualDomain.name
        }
        return conceptualDomains
    }

    def static getValueDomainRuleString(ValueDomain valueDomain){
        String rules = ""
        valueDomain.rule.eachWithIndex{ String rule, int i ->
            if (rules!="") rules += (stringSeparator + rule )
            else rules = rule
        }
        return rules
    }
}
