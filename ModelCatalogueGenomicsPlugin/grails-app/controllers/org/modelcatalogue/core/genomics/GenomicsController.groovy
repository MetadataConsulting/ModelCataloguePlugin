package org.modelcatalogue.core.genomics

import groovy.util.logging.Log4j
import org.modelcatalogue.core.AbstractCatalogueElementController
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.api.ElementStatus
import org.springframework.http.HttpStatus

import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
/**
 * Controller for GEL specific reports.
 */
class GenomicsController {

    def assetService
    DataClassService dataClassService
    def gelJsonService

    def exportRareDiseaseHPOAndClinicalTests() {
        DataClass model = DataClass.get(params.id)

        Long classId = model.getId()

        Long assetId = assetService.storeReportAsAsset(model.dataModel,
            name: "${model.name} report as Json",
            originalFileName: "${model.name}-${model.status}-${model.version}.json",
            contentType: "application/json",
        ) {
            it << gelJsonService.printDiseaseOntology(DataClass.get(classId))
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }




    def imagePath = "https://www.genomicsengland.co.uk/wp-content/uploads/2015/11/Genomics-England-logo-2015.png"

    def customTemplate = {
        'document' font: [family: 'Calibri', size: 11], margin: [left: 20, right: 10]
        'paragraph.title' font: [color: '#1F497D', size: 32.pt, bold:true], margin: [top: 150.pt, bottom: 10.pt]
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

    def exportGelSpecification() {

        DataModel model = DataModel.get(params.id)

        if(!model) {
            response.status = 404
            return
        }

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
            model,
            name: "${model.name} report as MS Word Document",
            originalFileName: "${model.name}-${model.status}-${model.version}.docx",
            contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )  { OutputStream out ->
            new DataModelToDocxExporter(DataModel.get(modelId), dataClassService, customTemplate, imagePath).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

}
