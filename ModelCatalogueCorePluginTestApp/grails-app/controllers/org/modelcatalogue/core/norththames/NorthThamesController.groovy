package org.modelcatalogue.core.norththames

import org.modelcatalogue.core.DataModel
import org.modelcatalogue.integration.excel.nt.uclh.UCLHGridReportXlsxExporter
import org.springframework.http.HttpStatus

/**
 * Controller for GEL specific reports.
 */
class NorthThamesController {


    def dataClassService, assetService

    //produce a grid report spreadsheet where the whole data set is diaplyed as a grid with metadata and relationships (rather then tabs)

    def northThamesSummaryReport(String name, Integer depth) {
        DataModel dataModel = DataModel.get(params.id)

        def dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = assetService.storeReportAsAsset(
                dataModel,
                name: name ? name : "${dataModel.name} report as MS Excel Document",
                originalFileName: "${dataModel.name}-${dataModel.status}-${dataModel.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream outputStream ->
            // reload domain class as this is called in separate thread
            UCLHGridReportXlsxExporter.create(DataModel.get(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

}
