import grails.rest.render.RenderContext
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.*
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.core.util.marshalling.*
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer

class ModelCatalogueCoreGrailsPlugin {
    // the plugin version
    def version = "1.0.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/index.gsp",
    ]

    def title = "Model Catalogue Core Plugin " // Headline display name of the plugin
    def author = "Adam Milward, Vladimír Oraný"
    def authorEmail = "adam.milward@outlook.com, vladimir@orany.cz"
    def description = '''\
Model catalogue core plugin (metadata registry)
'''

    // URL to the plugin's documentation
    def documentation = "https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/javadoc/guide/"

    //def packaging = "binary"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "Apache"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    def issueManagement = [ system: "GitHub", url: "https://github.com/MetadataRegistry/ModelCataloguePlugin/issues" ]

    def scm = [ url: "https://github.com/MetadataRegistry/ModelCataloguePlugin" ]

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
                    new ClassificationMarshaller(),
                    new DataElementMarshaller(),
                    new DataTypeMarshaller(),
                    new ElementsMarshaller(),
                    new EnumeratedTypeMarshaller(),
                    new MeasurementUnitMarshallers(),
                    new ModelMarshaller(),
                    new RelationshipTypeMarshaller(),
                    new RelationshipMarshallers(),
                    new RelationshipsMarshaller(),
                    new ValueDomainMarshaller(),
                    new MappingMarshallers(),
                    new MappingsMarshaller(),
                    new ListWithTotalAndTypeWrapperMarshaller(),
                    new BatchMarshaller(),
                    new ActionMarshaller(),
                    new CsvTransformationMarshaller(),
                    new UserMarshaller()
            ]
        }

        if (Environment.current == Environment.DEVELOPMENT) {
            springConfig.addAlias('modelCatalogueStorageService','localFilesStorageService')
        }

        catalogueBuilder(CatalogueBuilder, ref('classificationService'), ref('elementService')) { bean ->
            bean.scope = 'prototype'
        }

    }

    def doWithDynamicMethods = { ctx ->
        ctx.grailsApplication.domainClasses.each {
            if (CatalogueElement.isAssignableFrom(it.clazz)) {
                CatalogueElementDynamicHelper.addShortcuts(it.clazz)
            }
        }
        org.codehaus.groovy.grails.web.json.JSONObject.Null.metaClass.getId = {->
            null
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
            headers 'Model Catalogue ID', 'Name', 'Classifications', 'Unit of Measurement', 'Rules', 'Data Type Model Catalogue ID', 'DataType Name', 'Data Type Enumeration'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing', 'valueDomains'] && (!container.itemType || ValueDomain.isAssignableFrom(container.itemType))
            } then { ValueDomain valueDomain ->
                [[valueDomain.modelCatalogueId, valueDomain.name, getClassificationString(valueDomain), valueDomain.unitOfMeasure, getValueDomainRuleString(valueDomain), valueDomain.dataTypeId, valueDomain.dataType.name, getEnumerationString(valueDomain.dataType)]]
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
            headers "Classification",
                    "Parent Model Unique Code",
                    "Parent Model",
                    "Model Unique Code",
                    "Model",
                    "Data Item Unique Code",
                    "Data Item Name",
                    "Data Item Description",
                    "Value Domain Classification",
                    "Value Domain Unique Code",
                    "Value Domain",
                    "Measurement Unit",
                    "Measurement Unit Symbol",
                    "Data Type Classification",
                    "Data Type Unique Code",
                    "Data Type",
                    "Metadata"

            when { ListWrapper container, RenderContext context ->
                container.itemType && DataElement.isAssignableFrom(container.itemType)
            } then { DataElement element ->
                Model parent = getParentModel(element)
                Model model = getContainingModel(element)
                ValueDomain valueDomain = element.valueDomain
                DataType dataType = valueDomain?.dataType
                [[
                         getClassificationString(element),
                         parent?.modelCatalogueId ?: parent?.getDefaultModelCatalogueId(true),
                         parent?.name,
                         model?.modelCatalogueId  ?: model?.getDefaultModelCatalogueId(true),
                         model?.name,
                         element.modelCatalogueId ?: element.getDefaultModelCatalogueId(true),
                         element.name,
                         element.description,
                         getClassificationString(valueDomain),
                         valueDomain?.modelCatalogueId ?: valueDomain?.getDefaultModelCatalogueId(true),
                         valueDomain?.name,
                         getUnitOfMeasure(element),
                         getUnitOfMeasureSymbol(element) ,
                         getClassificationString(dataType),
                         dataType?.modelCatalogueId ?: dataType?.getDefaultModelCatalogueId(true),
                         getDataType(element),
                         "-"
                 ]]
            }
        }

        ReportsRegistry reportsRegistry = ctx.getBean(ReportsRegistry)


        reportsRegistry.register {
            creates asset
            title { "Export All Elements of ${it.name} to Excel XSLX" }
            type Model
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report:'NHIC'], id: true
        }

        reportsRegistry.register {
            creates link
            title { "Inventory Report" }
            type Classification
            link controller: 'classification', action: 'report', id: true
        }

        reportsRegistry.register {
            creates link
            title { "GE Inventory Report" }
            type Classification
            link controller: 'classification', action: 'gereport', id: true
        }

        reportsRegistry.register {
            creates link
            type Classification, Model, DataElement, ValueDomain, DataType, MeasurementUnit
            title { "Export to Catalogue XML" }
            link { CatalogueElement element ->
                [url: element.getDefaultModelCatalogueId(false) + '?format=xml']
            }
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
            if (!dataType) {
                return ''
            }
            if (dataType instanceof EnumeratedType) {
                return dataType.enumerations.collect { key, value -> "$key:$value"}.join('\n')
            }
            return dataType.name
        }
        return null
    }

    def static getClassificationString(CatalogueElement dataElement) {
        if (!dataElement?.classifications) {
            return ""
        }
        dataElement.classifications.first().name
    }

    def static getValueDomainString(DataType dataType){
        String valueDomains = ""

        dataType.relatedValueDomains.eachWithIndex{ ValueDomain valueDomain, Integer i ->
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

    def static getValueDomainRuleString(ValueDomain valueDomain){
        String rules = ""
        valueDomain.rule.eachWithIndex{ String rule, int i ->
            if (rules!="") rules += (stringSeparator + rule )
            else rules = rule
        }
        return rules
    }
}
