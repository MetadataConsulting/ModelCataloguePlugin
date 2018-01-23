package org.modelcatalogue.core.norththames

import org.hibernate.SessionFactory
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetMetadata
import org.modelcatalogue.core.AssetMetadataService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.asset.MicrosoftOfficeDocument
import org.modelcatalogue.core.dataexport.excel.norththamesreport.NorthThamesMappingReportXlsxSqlExporter
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.springframework.http.HttpStatus
import org.modelcatalogue.core.util.builder.BuildProgressMonitor

/**
 * Controller for GEL specific reports.
 */
class NorthThamesController {

    DataModelGormService dataModelGormService
    AssetGormService assetGormService
    AssetMetadataService assetMetadataService
    def dataClassService, assetService, dataElementService
    SessionFactory sessionFactory

    //produce a grid report spreadsheet where the whole data set is displayed as a grid with metadata and relationships (rather then tabs)

    def northThamesGridHierarchyMappingSummaryReport(String name, Integer depth) {
        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        String organization = params.organization as String
        Long dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        String assetName = name ?: "${dataModel.name} report as MS Excel Document-for-${organization}"

        MicrosoftOfficeDocument doc = MicrosoftOfficeDocument.XLSX
        AssetMetadata assetMetadata = new AssetMetadata(
                name: assetName,
                originalFileName: "${organization}-${dataModel.name}-${dataModel.status}-${dataModel.version}.${MicrosoftOfficeDocument.suffix(doc)}",
                contentType: MicrosoftOfficeDocument.contentType(doc)
        )
        Asset asset = assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
        asset.dataModel = dataModel
        asset = assetGormService.save(asset)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream, Long assetIdentifier ->
            BuildProgressMonitor buildProgressMonitor = BuildProgressMonitor.create("Exporting asset $assetName", assetId)
            GMCGridReportXlsxExporter.createWithBuildProgressMonitor(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth, organization, buildProgressMonitor).export(outputStream)
        }


        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    //produce a mapping report

    def northThamesMappingReport(String name, Integer depth) {
        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        Long dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        AssetMetadata assetMetadata = new AssetMetadata(
                name: name ? name : "${dataModel.name} report as MS Excel Document-for-North Thames",
                originalFileName: "North Thames-${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        Asset asset = assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
        asset.dataModel = dataModel
        asset = assetGormService.save(asset)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream ->
            NorthThamesMappingReportXlsxSqlExporter.create(dataModelGormService.findById(dataModelId), dataClassService, dataElementService, grailsApplication, isDatabaseFallback()).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    private boolean isDatabaseFallback() {
        if(sessionFactory.currentSession.connection().metaData.databaseProductName != 'MySQL') return false
        true
    }

}
