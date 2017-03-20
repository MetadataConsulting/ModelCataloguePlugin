import grails.util.Environment
import org.modelcatalogue.builder.api.ModelCatalogueTypes
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.audit.AuditJsonMarshallingCustomizer
import org.modelcatalogue.core.reports.ReportsRegistry
import org.modelcatalogue.core.security.ajax.AjaxAwareLoginUrlAuthenticationEntryPoint
import org.modelcatalogue.core.security.ss2x.ApiKeyDaoAuthenticationProvider
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.js.ApiRootFrontendConfigurationProvider
import org.modelcatalogue.core.util.js.FrontendConfigurationProviderRegistry
import org.modelcatalogue.core.util.marshalling.ActionMarshaller
import org.modelcatalogue.core.util.marshalling.AssetMarshaller
import org.modelcatalogue.core.util.marshalling.BatchMarshaller
import org.modelcatalogue.core.util.marshalling.ChangeMarshaller
import org.modelcatalogue.core.util.marshalling.CsvTransformationMarshaller
import org.modelcatalogue.core.util.marshalling.DataClassMarshaller
import org.modelcatalogue.core.util.marshalling.DataElementMarshaller
import org.modelcatalogue.core.util.marshalling.DataModelMarshaller
import org.modelcatalogue.core.util.marshalling.DataModelPolicyMarshaller
import org.modelcatalogue.core.util.marshalling.DataTypeMarshaller
import org.modelcatalogue.core.util.marshalling.ElementsMarshaller
import org.modelcatalogue.core.util.marshalling.EnumeratedTypeMarshaller
import org.modelcatalogue.core.util.marshalling.JsonMarshallingCustomizerRegistry
import org.modelcatalogue.core.util.marshalling.ListWithTotalAndTypeWrapperMarshaller
import org.modelcatalogue.core.util.marshalling.MappingMarshaller
import org.modelcatalogue.core.util.marshalling.MappingsMarshaller
import org.modelcatalogue.core.util.marshalling.MeasurementUnitMarshaller
import org.modelcatalogue.core.util.marshalling.ModelCatalogueCorePluginCustomObjectMarshallers
import org.modelcatalogue.core.util.marshalling.PrimitiveTypeMarshaller
import org.modelcatalogue.core.util.marshalling.ProgressMonitorMarshaller
import org.modelcatalogue.core.util.marshalling.ReferenceTypeMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipMarshallers
import org.modelcatalogue.core.util.marshalling.RelationshipTypeMarshaller
import org.modelcatalogue.core.util.marshalling.RelationshipsMarshaller
import org.modelcatalogue.core.util.marshalling.TagMarshaller
import org.modelcatalogue.core.util.marshalling.UserMarshaller
import org.modelcatalogue.core.util.marshalling.ValidationRuleMarshaller
import org.modelcatalogue.core.xml.render.RelationshipsXmlRenderer

import java.util.concurrent.Executors
import  grails.plugin.executor.PersistenceContextExecutorWrapper

// Place your Spring DSL code here
beans = {
    authenticationEntryPoint(AjaxAwareLoginUrlAuthenticationEntryPoint) {
        loginFormUrl = '/login/auth' // has to be specified even though it's ignored
        portMapper = ref('portMapper')
        portResolver = ref('portResolver')
    }

    executorService(PersistenceContextExecutorWrapper ) { bean->
        bean.destroyMethod = 'destroy'
        persistenceInterceptor = ref("persistenceInterceptor")
        executor = Executors.newCachedThreadPool()
    }

    springConfig.addAlias('modelCatalogueSecurityService','springSecurity2SecurityService')

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
            new DataModelPolicyMarshaller(),
            new TagMarshaller()
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

    springConfig.addAlias('userDetailsService','gormWithEmailUserDetailsService')
    springConfig.addAlias('daoAuthenticationProvider','apiKeyDaoAuthenticationProvider')

    if ((!System.properties["grails.test.phase"] || System.properties["grails.test.phase"] == 'functional')
        &&
        (application.config.mc.search.elasticsearch.local || application.config.mc.search.elasticsearch.host || System.getProperty('mc.search.elasticsearch.host') || System.getProperty('mc.search.elasticsearch.local'))
    ) {
        springConfig.addAlias('modelCatalogueSearchService','elasticSearchService')
    }
}
