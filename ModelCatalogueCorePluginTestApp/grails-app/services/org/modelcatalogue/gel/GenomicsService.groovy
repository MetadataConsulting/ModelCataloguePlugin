package org.modelcatalogue.gel

import grails.gsp.PageRenderer
import grails.transaction.Transactional
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetMetadata
import org.modelcatalogue.core.AssetMetadataService
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.PerformanceUtilService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.gel.export.DataModelChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseaseDisorderListCsvExporter
import org.modelcatalogue.gel.export.RareDiseaseEligibilityChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseaseMaintenanceSplitDocsExporter
import org.modelcatalogue.gel.export.RareDiseasePhenotypeChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseasesDocExporter
import org.modelcatalogue.gel.export.RareDiseasesJsonExporter
import org.modelcatalogue.gel.export.RareDiseasesWebsiteExporter

@Transactional
class GenomicsService {

    def auditService
    def assetService
    AssetGormService assetGormService
    GenomicsAssetMetadataService genomicsAssetMetadataService
    def dataClassService
    ElementService elementService
    PerformanceUtilService performanceUtilService
    PageRenderer groovyPageRenderer

    static final String DOC_IMAGE_PATH = GenomicsService.getResource('Genomics-England-logo-2015.png')?.toExternalForm()

    static Closure customTemplate = {
        'document' font: [family: 'Calibri', size: 11], margin: [left: 20, right: 10]
        'paragraph.title' font: [color: '#1F497D', size: 32.pt, bold: true], margin: [top: 150.pt, bottom: 10.pt]
        'paragraph.subtitle' font: [color: '#1F497D', size: 36.pt], margin: [top: 0.pt]
        'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
        'heading1' font: [size: 18, bold: true]
        'heading2' font: [size: 18, bold: true]
        'heading3' font: [size: 16, bold: true]
        'heading4' font: [size: 16, bold: true]
        'heading5' font: [size: 15]
        'heading6' font: [size: 14]
        'table.row.cell.headerCell' font: [color: '#FFFFFF', size: 12.pt, bold: true], background: '#1F497D'
        'table.row.cell' font: [size: 10.pt]
        'paragraph.headerImage' height: 1.366.inches, width: 2.646.inches
    }

    Long genRareDiseaseHPOAndClinicalTestsAsJson(DataClass dataClass){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dataClass.getDefaultModelCatalogueId(true))
        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseHPOAndClinicalTestsAsJson(latestVersion), latestVersion.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) {
            new GelJsonExporter(it).printDiseaseOntology(latestVersion)
        }

        asset.id
    }

    Long genDiseaseListOnlyAsJson(DataClass dataClass){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dataClass.getDefaultModelCatalogueId(true))
        Asset asset = saveAsset(genomicsAssetMetadataService.genDiseaseListOnlyAsJson(latestVersion), latestVersion.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) {
            new RareDiseasesJsonExporter(it).exportDiseaseListOnly(latestVersion)
        }

        asset.id
    }

    Long genRareDiseaseHPOEligibilityCriteriaAsJson(DataClass dataClass) {
        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseHPOEligibilityCriteriaAsJson(dataClass), dataClass.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) {
            new RareDiseasesJsonExporter(it).exportEligibilityCriteriaAsJson(dataClass)
        }
        asset.id
    }

    Long genRareDiseaseCsv(DataClass dClass, def docType){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dClass.getDefaultModelCatalogueId(true))
        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseCsv(docType), latestVersion.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) {
            new RareDiseaseCsvExporter(it, docType).printReport(latestVersion)
        }
        asset.id
    }

    Long genRareDiseaseDisorderListAsCsv(DataClass dClass){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dClass.getDefaultModelCatalogueId(true))
        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseDisorderListAsCsv(latestVersion), latestVersion.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) {
            new RareDiseaseDisorderListCsvExporter(it).export(latestVersion)
        }
        asset.id
    }

    Long genEligibilityOrPhenotypesAndTests(DataClass dataClass, boolean eligibilityMode) {
        Long classId = dataClass.id
        Asset asset = saveAsset(genomicsAssetMetadataService.genEligibilityOrPhenotypesAndTests(dataClass, eligibilityMode), dataClass.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream out ->
            new RareDiseasesDocExporter(DataClass.get(classId), RareDiseasesDocExporter.standardTemplate, DOC_IMAGE_PATH, eligibilityMode).export(out)
        }
        asset.id
    }


    Long genSplitDocAsset(DataClass dataClass) {
        Asset asset = saveAsset(genomicsAssetMetadataService.genSplitDocAsset(dataClass), dataClass.dataModel)
        Long classId = dataClass.id
        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream out ->
            new RareDiseaseMaintenanceSplitDocsExporter(DataClass.get(classId), RareDiseaseMaintenanceSplitDocsExporter.standardTemplate, DOC_IMAGE_PATH, 2).export(out)
        }
        asset.id
    }


    Long genRareDiseaseHPOAndClinicalTestsAsXls(DataClass dataClass) {

        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseHPOAndClinicalTestsAsXls(dataClass), dataClass.dataModel)

        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream out ->
            new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 0, false).export(dataClass,out)
        }
        asset.id
    }

    Long genRareDiseaseEligibilityChangeLogAsXls(DataClass dataClass) {
        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseEligibilityChangeLogAsXls(dataClass), dataClass.dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream out ->
            new RareDiseaseEligibilityChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 0, false).export(dataClass, out)
        }
        asset.id
    }

    Long genChangeLogDocument(DataClass dataClass, String name, Integer depth, Boolean includeMetadata){

        Asset asset = saveAsset(genomicsAssetMetadataService.genChangeLogDocument(dataClass, name), dataClass.dataModel)

        Long classId = dataClass.id
        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream out ->
            new ChangeLogDocxGenerator(auditService, dataClassService, performanceUtilService, elementService, depth, includeMetadata, customTemplate, DOC_IMAGE_PATH)
                .generateChangelog(DataClass.get(classId), out)
        }
        asset.id
    }

    Long genDataSpecChangeLogAsXls(DataModel model) {
        Asset asset = saveAsset(genomicsAssetMetadataService.genDataSpecChangeLogAsXls(model), model)
        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream out ->
            new DataModelChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 0, false).export(model, out)
        }
        asset.id
    }

    ListWithTotalAndType<DataClass> findRareDiseases(Map<String, Object> params = [:], DataModel dataModel) {
        Lists.fromCriteria(params, DataClass) {
            eq('dataModel', dataModel)
            ne('status', ElementStatus.DEPRECATED)
            outgoingRelationships {
                eq('relationshipType', RelationshipType.hierarchyType)
                destination {
                    or {
                        ilike('name', '% Eligibility')
                        ilike('name', '% Phenotypes')
                        ilike('name', '% Clinical Tests')
                    }
                }
            }
            order 'name'
        }
    }

    Long genRareDiseaseWebsite(DataModel dataModel) {
        Asset asset = saveAsset(genomicsAssetMetadataService.genRareDiseaseWebsite(dataModel), dataModel)
        assetService.storeReportAsAsset(asset.id, asset.contentType) { OutputStream it, Long assetId ->
            new RareDiseasesWebsiteExporter(this, dataModel, groovyPageRenderer, BuildProgressMonitor.create("${dataModel.name} - Static Website", assetId)).export(it)
        }
        asset.id
    }

    Asset saveAsset(Asset asset, DataModel dataModel) {
        asset.dataModel = dataModel
        assetGormService.save(asset)
    }
}
