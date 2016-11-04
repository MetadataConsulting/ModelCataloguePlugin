import grails.rest.render.RenderContext
import grails.util.Environment
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.runtime.StackTraceUtils
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditJsonMarshallingCustomizer
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.security.ss2x.ApiKeyDaoAuthenticationProvider
import org.modelcatalogue.core.util.CatalogueElementDynamicHelper
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.js.ApiRootFrontendConfigurationProvider
import org.modelcatalogue.core.util.js.FrontendConfigurationProviderRegistry
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.marshalling.*
import org.modelcatalogue.core.util.marshalling.xlsx.XLSXListRenderer
import org.modelcatalogue.core.xml.render.RelationshipsXmlRenderer

class ModelCatalogueCoreGrailsPlugin {


    static {
        StackTraceUtils.addClassTest {
            !it.startsWith('rx.')
        }
    }

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
        ModelCatalogueTypes.CATALOGUE_ELEMENT.implementation = CatalogueElement
        ModelCatalogueTypes.DATA_MODEL.implementation = DataModel
        ModelCatalogueTypes.DATA_CLASS.implementation = DataClass
        ModelCatalogueTypes.DATA_ELEMENT.implementation = DataElement
        ModelCatalogueTypes.DATA_TYPE.implementation = DataType
        ModelCatalogueTypes.MEASUREMENT_UNIT.implementation = MeasurementUnit
        ModelCatalogueTypes.ENUMERATED_TYPE.implementation = EnumeratedType
        ModelCatalogueTypes.PRIMITIVE_TYPE.implementation = PrimitiveType
        ModelCatalogueTypes.REFERENCE_TYPE.implementation = ReferenceType
        ModelCatalogueTypes.VALIDATION_RULE.implementation = ValidationRule


        mergeConfig(application)

        xlsxListRenderer(XLSXListRenderer)
        relationshipsXmlRenderer(RelationshipsXmlRenderer)
        reportsRegistry(ReportsRegistry)
        jsonMarshallingCustomizerRegistry(JsonMarshallingCustomizerRegistry)
        frontendConfigurationProviderRegistry(FrontendConfigurationProviderRegistry)

        apiRootFrontendConfigurationProvider(ApiRootFrontendConfigurationProvider)

        auditJsonMarshallingCustomizer(AuditJsonMarshallingCustomizer)

        modelCatalogueCorePluginCustomObjectMarshallers(ModelCatalogueCorePluginCustomObjectMarshallers) {
            marshallers = [
                    new AssetMarshaller(),
                    new DataModelMarshaller(),
                    new DataElementMarshaller(),
                    new DataTypeMarshaller(),
                    new ElementsMarshaller(),
                    new EnumeratedTypeMarshaller(),
                    new ReferenceTypeMarshaller(),
                    new PrimitiveTypeMarshaller(),
                    new MeasurementUnitMarshaller(),
                    new DataClassMarshaller(),
                    new RelationshipTypeMarshaller(),
                    new RelationshipMarshallers(),
                    new RelationshipsMarshaller(),
                    new MappingMarshaller(),
                    new MappingsMarshaller(),
                    new ListWithTotalAndTypeWrapperMarshaller(),
                    new BatchMarshaller(),
                    new ActionMarshaller(),
                    new CsvTransformationMarshaller(),
                    new UserMarshaller(),
                    new ChangeMarshaller(),
                    new ValidationRuleMarshaller(),
                    new ProgressMonitorMarshaller(),
                    new DataModelPolicyMarshaller()
            ]
        }

        if (application.config.mc.storage.s3.bucket) {
            springConfig.addAlias('modelCatalogueStorageService','amazonStorageService')
        } else if (Environment.current == Environment.DEVELOPMENT || application.config.mc.storage.directory) {
            springConfig.addAlias('modelCatalogueStorageService','localFilesStorageService')
        }

        catalogueBuilder(DefaultCatalogueBuilder, ref('dataModelService'), ref('elementService')) { bean ->
            bean.scope = 'prototype'
        }

        apiKeyDaoAuthenticationProvider(ApiKeyDaoAuthenticationProvider) {
            userDetailsService = ref('userDetailsService')
            passwordEncoder = ref('passwordEncoder')
            userCache = ref('userCache')
            saltSource = ref('saltSource')
            preAuthenticationChecks = ref('preAuthenticationChecks')
            postAuthenticationChecks = ref('postAuthenticationChecks')
            authoritiesMapper = ref('authoritiesMapper')
            hideUserNotFoundExceptions = true
        }

        springConfig.addAlias('modelCatalogueSecurityService','springSecurity2SecurityService')
        springConfig.addAlias('userDetailsService','gormWithEmailUserDetailsService')
        springConfig.addAlias('daoAuthenticationProvider','apiKeyDaoAuthenticationProvider')

        if (System.getenv('MC_ES_DISABLED') != 'true' && (application.config.mc.search.elasticsearch.local || application.config.mc.search.elasticsearch.host || System.getProperty('mc.search.elasticsearch.host') || System.getProperty('mc.search.elasticsearch.local'))) {
            springConfig.addAlias('modelCatalogueSearchService','elasticSearchService')
        }
    }

    def doWithDynamicMethods = { ctx ->
        ctx.grailsApplication.domainClasses.each { GrailsDomainClass it ->
            if (CatalogueElement.isAssignableFrom(it.clazz)) {
                CatalogueElementDynamicHelper.addShortcuts(it.clazz)
            }
        }
        JSONObject.Null.metaClass.getId = {->
            null
        }
    }

    def doWithApplicationContext = { ctx ->
        //register custom json Marshallers
        //ctx.domainModellerService.modelDomains()
        ctx.getBean('modelCatalogueCorePluginCustomObjectMarshallers').register()

        XLSXListRenderer xlsxListRenderer = ctx.getBean(XLSXListRenderer)

        xlsxListRenderer.registerRowWriter ('Classifications'){
            title "Data Models to Excel"
            headers  'Model Catalogue ID',  'Name', 'Description'
            when { ListWrapper container, RenderContext context ->
                    context.actionName in [null, 'index', 'search', 'incoming', 'outgoing'] && (!container.itemType || DataModel.isAssignableFrom(container.itemType))
            } then { DataModel dataModel ->
                [[ dataModel.modelCatalogueId,  dataModel.name, dataModel.description]]
            }
        }

        xlsxListRenderer.registerRowWriter ('DataTypes') {
            title "Data Types to Excel"
            headers 'Model Catalogue ID', 'Name', 'Enumerations'
            when { ListWrapper container, RenderContext context ->
                context.actionName in [null, 'index', 'search', 'incoming', 'outgoing', 'properties'] && (!container.itemType || DataType.isAssignableFrom(container.itemType))
            } then { DataType dataType ->
                [[dataType.id, dataType.name, getEnumerationString(dataType)]]
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
                context.actionName in [null,'index'] && (!container.itemType || DataClass.isAssignableFrom(container.itemType))
            } then { DataClass model ->
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
            headers "Data Model",
                    "Parent Data Class Unique Code",
                    "Parent Data Class",
                    "Data Class Unique Code",
                    "Data Class",
                    "Data Item Unique Code",
                    "Data Item Name",
                    "Data Item Description",
                    "Measurement Unit",
                    "Measurement Unit Symbol",
                    "Data Type Data Model",
                    "Data Type Unique Code",
                    "Data Type",
                    "Metadata"

            when { ListWrapper container, RenderContext context ->
                container.itemType && DataElement.isAssignableFrom(container.itemType)
            } then { DataElement element ->
                DataClass parent = getParentModel(element)
                DataClass model = getContainingModel(element)
                DataType dataType = element.dataType
                [[
                         getDataModelsString(element),
                         parent?.modelCatalogueId ?: parent?.getDefaultModelCatalogueId(true),
                         parent?.name,
                         model?.modelCatalogueId  ?: model?.getDefaultModelCatalogueId(true),
                         model?.name,
                         element.modelCatalogueId ?: element.getDefaultModelCatalogueId(true),
                         element.name,
                         element.description,
                         getUnitOfMeasure(element),
                         getUnitOfMeasureSymbol(element) ,
                         getDataModelsString(dataType),
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
            defaultName { "Export All Elements of ${it.name} to Excel XSLX" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.countContains() > 0
            }
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'NHIC'], id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'inventorySpreadsheet', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Document" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventoryDoc', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventorySpreadsheet', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "Changelog Document" }
            defaultName { "${it.name} changelog as MS Word Document" }
            depth 3
            includeMetadata true
            type DataClass
            link controller: 'dataClass', action: 'changelogDoc', id: true
        }

        reportsRegistry.register {
            creates link
            type DataModel, DataClass, DataElement, DataType, MeasurementUnit
            title { "Export to Catalogue XML" }
            link { CatalogueElement element ->
                [url: element.getDefaultModelCatalogueId(false) + '?format=xml']
            }
        }

        reportsRegistry.register {
            creates link
            title { "Generate all ${it.name} files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_CANCER_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllCancerReports', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Generate all ${it.name} files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllRareDiseaseReports', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "GEL Changelog (Word Doc)" }
            defaultName { "${it.name} changelog as MS Word Document" }
            depth 3
            includeMetadata true
            type DataClass
            link controller: 'genomics', action: 'exportChangeLogDocument', id: true
        }

        reportsRegistry.register {
            creates asset
            title { "GEL Data Specification Report (Word Doc)" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataModel
            link controller: 'genomics', action: 'exportGelSpecification', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Disorder List (CSV)" }
            type DataClass
            link controller: 'genomics', action: 'exportRareDiseaseDisorderListAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria Report (Word Doc)" }
            type DataClass
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Phenotypes and Clinical Tests Report (Word Doc)" }
            type DataClass
            link controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Disorder List Only (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseListAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOEligibilityCriteriaAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (CSV)" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Disease Eligibility Criteria Report (CSV)" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Cancer Types (JSON)" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.CANCER_TYPES_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportCancerTypesAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Cancer Types (CSV)" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.CANCER_TYPES_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportCancerTypesAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Change Log for RD Phenotypes And Clinical Tests (Excel)" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsXls', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Change Log for RD Eligibility (Excel)" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityChangeLogAsXls', id: true
        }

        reportsRegistry.register {
            creates link
            title { "GEL Data Specification Change Log (Excel)" }
            type DataModel
            link controller: 'genomics', action: 'exportDataSpecChangeLogAsXls', id: true
        }
        reportsRegistry.register {
            creates link
            title { "Rare Diseases Static Website" }
            type DataModel
            link controller: 'genomics', action: 'exportRareDiseasesWebsite', id: true
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
        DataClass containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return null
    }

    def static getUnitOfMeasure(DataElement dataElement){
        if (dataElement.dataType && dataElement.dataType instanceof PrimitiveType) {
            return dataElement.dataType.measurementUnit?.name
        }
        return null
    }

    def static getUnitOfMeasureSymbol(DataElement dataElement){
        if (dataElement.dataType && dataElement.dataType instanceof PrimitiveType) {
            return dataElement.dataType.measurementUnit?.symbol
        }
        return null
    }

    def static getDataType(DataElement dataElement){
        DataType dataType = dataElement.dataType
        if (!dataType) {
            return ''
        }
        if (dataType instanceof EnumeratedType) {
            return dataType.enumerations.collect { key, value -> "$key:$value"}.join('\n')
        }
        return dataType.name

    }

    def static getDataModelsString(CatalogueElement dataElement) {
        if (!dataElement?.dataModel) {
            return ""
        }
        dataElement.dataModel.name
    }

    def static getEnumerationString(DataType dataType){
        if (dataType instanceof EnumeratedType) {
            return dataType.prettyPrint()
        }
        return null
    }
}
