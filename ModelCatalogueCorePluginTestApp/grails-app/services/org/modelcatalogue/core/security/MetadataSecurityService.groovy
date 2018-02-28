package org.modelcatalogue.core.security

import grails.util.Environment
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.env.GrailsEnvironment
import org.modelcatalogue.core.persistence.RequestmapGormService
import org.springframework.http.HttpMethod

@CompileStatic
class MetadataSecurityService {

    RequestmapGormService requestmapGormService

    public static final List MODEL_CATALOGUE_NORTH_THAMES_URL_MAPPINGS = [
            ['/api/modelCatalogue/core/northThames/northThamesGridHierarchyMappingSummaryReport/*', 'isAuthenticated()', HttpMethod.GET]
    ]

    public static final List DATAIMPORTCREATE_URL_MAPPINGS = [
        ["/dataImport/obo",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
        ["/dataImport/dsl",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
        ["/dataImport/excel",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
        ["/dataImport/xml",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
    ]

    public static final List LOGS_MAPPINGS = [
            ["/logs/index",  MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.GET],
    ]

    public static final List REINDEXCATALOGUE_MAPPINGS = [
            ["/reindexCatalogue/index",  MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.GET],
    ]

    public static final List LASTSEEN_URL_MAPPINGS = [
            ["/lastSeen/index",  MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.GET],
    ]

    public static final List MODELCATALOGUEVERSION_URL_MAPPINGS = [
            ["/modelCatalogueVersion/index",  MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.GET],
    ]

    public static final List APIKEY_URL_MAPPINGS = [
            ["/apiKey/index",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/apiKey/reset",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
    ]

    public static final List MAPPINGS_SUGGESTIONS_URL_MAPPINGS = [
            ["/mappingsuggestions",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/mappingsuggestions",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/mappingsuggestions/reject",  MetadataRolesUtils.roles('CURATOR') ,HttpMethod.POST],
            ["/mappingsuggestions/approve",  MetadataRolesUtils.roles('CURATOR') ,HttpMethod.POST],
    ]

    public static final List MODEL_CATALOGUE_GENOMICS_URL_MAPPINGS = [
            ["/api/modelCatalogue/core/genomics/imports/upload", MetadataRolesUtils.roles('CURATOR') ,HttpMethod.POST],
            ["/api/modelCatalogue/core/genomics/exportGelSpecification/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsJson/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseListAsJson/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseHPOEligibilityCriteriaAsJson/*", 'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsCsv/*", 'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTestsAsXls/*", 'isAuthenticated()', HttpMethod.GET],
            //["/api/modelCatalogue/core/genomics/exportRareDiseaseHPOAndClinicalTests/*", 'isAuthenticated()',,         HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityDoc/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseasePhenotypesAndClinicalTestsDoc/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseDisorderListCsv/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityCsv/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportCancerTypesAsJson/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportCancerTypesAsCsv/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportChangeLogDocument/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseEligibilityChangeLogAsXls/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportDataSpecChangeLogAsXls/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportAllCancerReports/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportAllRareDiseaseReports/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseasesWebsite/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/genomics/exportRareDiseaseSplitDocs/*", 'isAuthenticated()', HttpMethod.GET],
    ]

    public static final List MODEL_CATALOGUE_FORM_URL_MAPPINGS = [
            ["/api/modelCatalogue/core/forms/generate/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/forms/preview/*", 'isAuthenticated()', HttpMethod.GET],
    ]

    public static final List MODEL_CATALOGUE_AUDIT_PLUGIN_MAPPINGS = [
            ["/api/modelCatalogue/core/change/", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/change/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/change/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/change/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataModel/*/activity", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/activity", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/changes", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/changes", 'isAuthenticated()', HttpMethod.GET],
    ]

    public static final List ASSET_MAPPINGS = [
            ["/api/modelCatalogue/core/asset", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/asset/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/asset/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/asset/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/asset/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/asset/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/asset/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/asset/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/history", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/path", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/archive", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/restore", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/clone/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/merge/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/upload", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/upload", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/asset/*/download", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/content", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/asset/*/validateXml", 'isAuthenticated()', HttpMethod.POST],
    ]

    public static final List DATA_ELEMENTS_MAPPINGS = [
            ["/api/modelCatalogue/core/dataElement", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataElement/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataElement/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataElement/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataElement/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataElement/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataElement/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataElement/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/history", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/path", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataElement/*/archive", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/restore", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/clone/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/merge/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataElement/*/content", 'isAuthenticated()', HttpMethod.GET],
    ]

    public static final List DATA_CLASS_MAPPINGS = [
            ["/api/modelCatalogue/core/dataClass", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataClass/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataClass/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataClass/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataClass/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataClass/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataClass/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataClass/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/history", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/path", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/archive", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/restore", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/clone/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/merge/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataClass/*/inventoryDoc", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/classificationChangelog", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/inventorySpreadsheet", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/referenceType", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataClass/*/content", 'isAuthenticated()', HttpMethod.GET],
    ]

    public static final List CATALOGUE_ELEMENT_MAPPINGS = [
            ["/api/modelCatalogue/core/catalogueElement", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/catalogueElement/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/catalogueElement/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/catalogueElement/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/catalogueElement/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*/clone/*",'isAuthenticated()',         HttpMethod.POST],
            ["/api/modelCatalogue/core/catalogueElement/*/merge/*",'isAuthenticated()',         HttpMethod.POST],
    ]

    public static final List DATA_TYPE_MAPPINGS = [
            ["/api/modelCatalogue/core/dataType", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataType/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataType/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataType/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataType/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataType/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataType/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataType/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/archive",'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/clone/*",'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataType/*/dataElement",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/convert/*",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataType/*/validateValue",'isAuthenticated()',HttpMethod.GET],
    ]

    public static final List ENUMERATED_TYPE_MAPPINGS = [
            ["/api/modelCatalogue/core/enumeratedType", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/setDeprecated", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/enumeratedType/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing/*",MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/enumeratedType/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/enumeratedType/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/enumeratedType/*/dataElement",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/convert/*",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/validateValue",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/enumeratedType/*/content",'isAuthenticated()',HttpMethod.GET],
    ]

    public static final List REFERENCE_TYPE_MAPPINGS = [
            ["/api/modelCatalogue/core/referenceType", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/referenceType/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/referenceType/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/referenceType/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/referenceType/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/referenceType/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/referenceType/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/referenceType/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/referenceType/*/dataElement",'isAuthenticated()',       HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/convert/*",'isAuthenticated()',        HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/validateValue",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/referenceType/*/content",'isAuthenticated()',HttpMethod.GET],
    ]

    public static final List DATA_ARCHITECT_MAPPINGS = [
            ["/api/modelCatalogue/core/dataArchitect/metadataKeyCheck/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataArchitect/getSubModelElements/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataArchitect/getSubModelElements/", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataArchitect/findRelationsByMetadataKeys/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataArchitect/elementsFromCSV", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataArchitect/modelsFromCSV", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataArchitect/generateSuggestions", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataArchitect/deleteSuggestions", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataArchitect/suggestionsNames", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataArchitect/imports/upload", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataArchitect/imports/excelImportTypes", MetadataRolesUtils.roles('ADMIN'), HttpMethod.GET],
    ]

    public static final List DATA_MODEL_POLICY_MAPPINGS = [
            ["/api/modelCatalogue/core/dataModelPolicy", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModelPolicy", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModelPolicy/search", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModelPolicy/search/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModelPolicy/*/validate", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModelPolicy/validate", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModelPolicy/*", MetadataRolesUtils.roles('ADMIN'), HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModelPolicy/*", MetadataRolesUtils.roles('ADMIN'), HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataModelPolicy/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
    ]

    public static final List PRIMITIVE_TYPE_MAPPINGS = [
            ["/api/modelCatalogue/core/primitiveType", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/primitiveType/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/primitiveType/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/primitiveType/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/primitiveType/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/primitiveType/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/primitiveType/*/dataElement",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/convert/*",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/validateValue",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/primitiveType/*/content",'isAuthenticated()',HttpMethod.GET],
    ]

    public static final List CSV_TRANSFORMATION_MAPPINGS = [
            ["/api/modelCatalogue/core/csvTransformation", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/csvTransformation", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/csvTransformation/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/csvTransformation/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/csvTransformation/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/csvTransformation/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/csvTransformation/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/csvTransformation/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/csvTransformation/*/transform", MetadataRolesUtils.roles('VIEWER'), HttpMethod.POST],
    ]

    public static final List RELATIONSHIP_TYPE_MAPPINGS = [
            ["/api/modelCatalogue/core/relationshipType", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/relationshipType", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/relationshipType/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/relationshipType/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/relationshipType/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/relationshipType/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/relationshipType/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.PUT],
            ["/api/modelCatalogue/core/relationshipType/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/relationshipType/elementClasses", 'isAuthenticated()', HttpMethod.GET],
    ]

    public static final List TAG_MAPPINGS = [
            ["/api/modelCatalogue/core/tag/forDataModel/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/tag/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/tag/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/outgoing/*", 'isAuthenticated()',  HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/tag/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/tag/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/tag/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/tag/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/tag/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/history", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/path", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/tag/*/archive", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*/restore", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*/clone/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/tag/*/merge/*", 'isAuthenticated()', HttpMethod.POST],
    ]

    public static final List VALIDATION_RULE_MAPPINGS = [
            ["/api/modelCatalogue/core/validationRule", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/validationRule/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/validationRule/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/validationRule/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/validationRule/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/validationRule/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/validationRule/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/validationRule/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/history", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/path", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/validationRule/*/archive", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/restore", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/clone/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/merge/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/validationRule/*/content", 'isAuthenticated()', HttpMethod.GET],
    ]


    public static final List DASHBOARD_MAPPINGS = [
            ["/dashboard/index", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET]
            ]

    public static final List BATCH_MAPPINGS = [

            ["/batch/all", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/batch/create", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/batch/generateSuggestions", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/batch/archive", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],


            ["/api/modelCatalogue/core/batch", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/batch", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/search/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/batch/search/",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/batch/*/validate", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/batch/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.PUT],
            ["/api/modelCatalogue/core/batch/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
        
            ["/api/modelCatalogue/core/batch/*/archive", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/*/run", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/*/actions/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.GET],
            ["/api/modelCatalogue/core/batch/*/actions/*/dismiss", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/*/actions/*/reactivate", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/*/actions/*/run", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/batch/*/actions/*/parameters", MetadataRolesUtils.roles('CURATOR'), HttpMethod.PUT],

            ["/api/modelCatalogue/core/batch/*/actions/*/dependsOn", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/batch/*/actions/*/dependsOn", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
    ]

    public static final List MODEL_MAPPINGS = [
            ["/api/modelCatalogue/core/model", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/model/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/model/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/model/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/model/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/model/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/model/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/model/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/model/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/model/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/model/*/inventoryDoc",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/classificationChangelog",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/inventorySpreadsheet",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/referenceType",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/model/*/content",'isAuthenticated()',HttpMethod.GET],
    ]


    public static final List CLASSIFICATION_MAPPINGS = [
            ["/api/modelCatalogue/core/classification", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/classification/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/classification/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/classification/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/classification/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/classification/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/classification/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/classification/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/preload",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/preload",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/declares",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/containsOrImports/*",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/content",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/newVersion",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/classification/*/inventorySpreadsheet",'isAuthenticated()',        HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/gridSpreadsheet",'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/excelExporterSpreadsheet",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/inventoryDoc",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/dependents",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/classification/*/reindex",'isAuthenticated()',HttpMethod.POST],
    ]

    public static final List DATA_MODEL_MAPPINGS = [
            ["/api/modelCatalogue/core/dataModel", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataModel/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataModel/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataModel/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataModel/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataModel/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/dataModel/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/dataModel/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/finalize",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/preload",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/preload",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/declares",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/containsOrImports/*",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/content",'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/newVersion", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/dataModel/*/inventorySpreadsheet",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/gridSpreadsheet",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/excelExporterSpreadsheet",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/inventoryDoc",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/dependents",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/dataModel/*/reindex",'isAuthenticated()',HttpMethod.POST],
    ]

    public static final List USER_MAPPINGS = [
            ["/api/modelCatalogue/core/user", MetadataRolesUtils.roles('ADMIN'), HttpMethod.GET],
            ["/api/modelCatalogue/core/user", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/search/", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/user/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*", MetadataRolesUtils.roles('VIEWER'), HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/availableReportDescriptors", MetadataRolesUtils.roles('VIEWER'), HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.PUT],
            ["/api/modelCatalogue/core/user/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/user/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/outgoing/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/outgoing/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/user/*/outgoing/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.PUT],
            ["/api/modelCatalogue/core/user/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/incoming/*",  MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/incoming/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/user/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/user/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/mapping/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/user/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/user/*/archive", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/restore", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/clone/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/merge/*", MetadataRolesUtils.roles('SUPERVISOR'), HttpMethod.POST],
            ["/user/current",'permitAll', HttpMethod.GET],
            ["/api/modelCatalogue/core/user/current",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/user/classifications", MetadataRolesUtils.roles('SUPERVISOR'),HttpMethod.POST],
            ["/api/modelCatalogue/core/user/lastSeen", MetadataRolesUtils.roles('ADMIN'), HttpMethod.GET],
            ["/api/modelCatalogue/core/user/apikey", MetadataRolesUtils.roles('VIEWER'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/favourite",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/favourite",'isAuthenticated()',HttpMethod.DELETE],
            ["/api/modelCatalogue/core/user/*/enable", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/disable", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
            ["/api/modelCatalogue/core/user/*/role/*", MetadataRolesUtils.roles('ADMIN'), HttpMethod.POST],
    ]

    public static final List MEASUREMENT_MAPPINGS = [
            ["/api/modelCatalogue/core/measurementUnit", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/validate", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/availableReportDescriptors", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/measurementUnit/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming/*/search", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming/*",  MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming/*", 'isAuthenticated()', HttpMethod.PUT],
            ["/api/modelCatalogue/core/measurementUnit/*/incoming", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/outgoing", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/mapping/*", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/mapping/*", MetadataRolesUtils.roles('CURATOR'), HttpMethod.DELETE],
            ["/api/modelCatalogue/core/measurementUnit/*/mapping", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/typeHierarchy", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/history",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/path",'isAuthenticated()',HttpMethod.GET],
            ["/api/modelCatalogue/core/measurementUnit/*/archive",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/restore",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/clone/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/merge/*",'isAuthenticated()',HttpMethod.POST],
            ["/api/modelCatalogue/core/measurementUnit/*/primitiveType",'isAuthenticated()',HttpMethod.GET],
    ]

    void secureUrlMappings() {
        //permit all for assets and initial pages
        for (String url in [
                '/',
                '/**/favicon.ico',
                '/fonts/**',
                '/stomp/**',
                '/assets/**',
                '/plugins/**/js/**',
                '/plugins/jquery-ui-*/**',
                '/js/vendor/**',
                '/**/*.less',
                '/**/js/**',
                '/**/css/**',
                '/**/images/**',
                '/**/img/**',
                '/login', '/login.*', '/login/*',
                '/j_spring_security_check',
                '/logout', '/logout.*', '/logout/*',
                '/register/*', '/errors', '/errors/*',
                '/load',
                '/index.gsp'
        ]) {
            requestmapGormService.createRequestmapIfMissing(url, 'permitAll', null)
        }

        requestmapGormService.createRequestmapIfMissing('/asset/download/*',                      'isAuthenticated()',   HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/oauth/*/**',                            'IS_AUTHENTICATED_ANONYMOUSLY')
        requestmapGormService.createRequestmapIfMissing('/catalogue/*/**',                        'isAuthenticated()',   HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',   HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/*/comments',  'isAuthenticated()',   HttpMethod.POST) // post a comment
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',         HttpMethod.POST)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',         HttpMethod.PUT)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',         HttpMethod.DELETE)


        requestmapGormService.createRequestmapIfMissing('/sso/*/**',                              'isAuthenticated()',   HttpMethod.GET)

        secureSpringSecurityUiEndpoints()

        requestmapGormService.createRequestmapIfMissing('/console/**',                            'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/plugins/console*/**',                   'ROLE_SUPERVISOR')

        if (Environment.current == Environment.DEVELOPMENT) {
            requestmapGormService.createRequestmapIfMissing('/dbconsole/**', 'permitAll')
        }

        requestmapGormService.createRequestmapIfMissing('/monitoring/**',                         'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/plugins/console-1.5.0/**',              'ROLE_SUPERVISOR')

//      requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/dataClass/**', 'IS_AUTHENTICATED_ANONYMOUSLY')
//      requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/dataElement/**', 'ROLE_METADATA_CURATOR')
//      requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/dataType/**', 'ROLE_USER')
//      requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'ROLE_METADATA_CURATOR')
//      requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/relationshipTypes/**', 'ROLE_ADMIN')


        secureMappings(MODEL_CATALOGUE_NORTH_THAMES_URL_MAPPINGS)
        secureMappings(MODEL_CATALOGUE_GENOMICS_URL_MAPPINGS)
        secureMappings(MODEL_CATALOGUE_FORM_URL_MAPPINGS)
        secureMappings(MODEL_CATALOGUE_AUDIT_PLUGIN_MAPPINGS)
        secureModelCatalogueCorePluginUrlMappings()

        secureDataModelPermissionEndpoints()
    }

    void secureDataModelPermissionEndpoints() {
        requestmapGormService.createRequestmapIfMissing('/dataModelPermission/grant',                               'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/dataModelPermission/revoke',                               'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/dataModelPermission/show/*',                               'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/dataModelPermission/index',                               'ROLE_SUPERVISOR')
    }

    void secureSpringSecurityUiEndpoints() {
        //requestmapGormService.createRequestmapIfMissing('/user/*',                               'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/userAdmin/**',                          'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/role/**',                               'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/aclClass/**', 'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/aclSid/**', 'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/aclEntry/**', 'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/aclObjectIdentity/**', 'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/requestMap/**',                         'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/registrationCode/**',                   'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/securityInfo/**',                       'ROLE_SUPERVISOR')
    }

    private void secureMappings(List<List> mappings) {
        for ( List l : mappings) {
            requestmapGormService.createRequestmapIfMissing(l[0] as String, l[1] as String, l[2] as HttpMethod)
        }
    }

    private void secureModelCatalogueCorePluginUrlMappings() {
        secureMappings([
            ["/api/modelCatalogue/core/forms/generate/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/forms/preview/*", 'isAuthenticated()', HttpMethod.GET],
            ["/catalogue/upload", MetadataRolesUtils.roles('CURATOR'), HttpMethod.POST],
            ["/catalogue/ext/*/*", 'isAuthenticated()', HttpMethod.GET],
            ["/catalogue/ext/*/*/export", 'isAuthenticated()', HttpMethod.GET],
            ["/catalogue/*/*", 'isAuthenticated()', HttpMethod.GET],
            ["/catalogue/*/*.*", 'isAuthenticated()', HttpMethod.GET],
            ["/catalogue/*/*.*/export", 'isAuthenticated()', HttpMethod.GET],
            ["/catalogue/*/*/export", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/feedback", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/feedback/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/logs", 'ROLE_SUPERVISOR', HttpMethod.GET],
            ["/", 'permitAll', HttpMethod.GET],
            ["/load", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/search/reindex", 'isAuthenticated()', HttpMethod.POST],
            ["/api/modelCatalogue/core/search/*", 'isAuthenticated()', HttpMethod.GET],
            ["/api/modelCatalogue/core/relationship/*/restore", 'ROLE_SUPERVISOR()', HttpMethod.POST],
        ])
        secureMappings(ASSET_MAPPINGS)
        secureMappings(DATA_ELEMENTS_MAPPINGS)
        secureMappings(DATA_CLASS_MAPPINGS)
        secureMappings(CATALOGUE_ELEMENT_MAPPINGS)
        secureMappings(DATA_TYPE_MAPPINGS)
        secureMappings(ENUMERATED_TYPE_MAPPINGS)
        secureMappings(REFERENCE_TYPE_MAPPINGS)
        secureMappings(PRIMITIVE_TYPE_MAPPINGS)
        secureMappings(MEASUREMENT_MAPPINGS)
        secureMappings(USER_MAPPINGS)
        secureMappings(DATA_MODEL_MAPPINGS)
        secureMappings(CLASSIFICATION_MAPPINGS)
        secureMappings(MODEL_MAPPINGS)
        secureMappings(VALIDATION_RULE_MAPPINGS)
        secureMappings(TAG_MAPPINGS)
        secureMappings(BATCH_MAPPINGS)
        secureMappings(DASHBOARD_MAPPINGS)
        secureMappings(RELATIONSHIP_TYPE_MAPPINGS)
        secureMappings(CSV_TRANSFORMATION_MAPPINGS)
        secureMappings(DATA_MODEL_POLICY_MAPPINGS)
        secureMappings(DATA_ARCHITECT_MAPPINGS)
        secureMappings(MAPPINGS_SUGGESTIONS_URL_MAPPINGS)
        secureMappings(APIKEY_URL_MAPPINGS)
        secureMappings(MODELCATALOGUEVERSION_URL_MAPPINGS)
        secureMappings(LASTSEEN_URL_MAPPINGS)
        secureMappings(DATAIMPORTCREATE_URL_MAPPINGS)
        secureMappings(REINDEXCATALOGUE_MAPPINGS)
        secureMappings(LOGS_MAPPINGS)
    }
}
