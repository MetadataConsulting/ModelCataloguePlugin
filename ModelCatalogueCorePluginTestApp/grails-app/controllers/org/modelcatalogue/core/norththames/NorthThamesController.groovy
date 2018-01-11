package org.modelcatalogue.core.norththames

import org.hibernate.SessionFactory
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.dataexport.excel.norththamesreport.NorthThamesMappingReportXlsxExporter
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.springframework.http.HttpStatus

/**
 * Controller for GEL specific reports.
 */
class NorthThamesController {

    DataModelGormService dataModelGormService

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

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document-for-${organization}",
                originalFileName: "${organization}-${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            GMCGridReportXlsxExporter.create(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth, organization).export(outputStream)
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

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document-for-North Thames",
                originalFileName: "North Thames-${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            NorthThamesMappingReportXlsxExporter.create(dataModelGormService.findById(dataModelId), dataClassService, dataElementService, grailsApplication, isDatabaseFallback()).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    private boolean isDatabaseFallback() {
        if(sessionFactory.currentSession.connection().metaData.databaseProductName != 'MySQL') return false
        true
    }

}
