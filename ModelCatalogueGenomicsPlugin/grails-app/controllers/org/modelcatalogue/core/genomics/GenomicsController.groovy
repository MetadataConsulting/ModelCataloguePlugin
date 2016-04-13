package org.modelcatalogue.core.genomics

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.gel.GelCsvExporter
import org.modelcatalogue.gel.GelJsonExporter
import org.modelcatalogue.gel.export.CancerTypesCsvExporter
import org.modelcatalogue.gel.export.CancerTypesJsonExporter
import org.modelcatalogue.gel.export.RareDiseaseDisorderListCsvExporter
import org.modelcatalogue.gel.export.RareDiseasesDocExporter
import org.modelcatalogue.gel.export.RareDiseasesJsonExporter
import org.springframework.http.HttpStatus

import static RareDiseasesDocExporter.getStandardTemplate

/**
 * Controller for GEL specific reports.
 */
class GenomicsController {

    def assetService
    def elementService
    DataClassService dataClassService

    static final String RD_HPO_CSV_FILENAME = "RD Phenotypes and Clinical Tests.csv"
    static final String RD_ELIGIBILITY_CRITERIA_JSON = "RD Eligibility Criteria.json"
    static final String RD_PHENOTYPE_AND_CLINICAL_TESTS_JSON = "RD Phenotype and Clinical tests.json"

    static final String DOC_IMAGE_PATH = "https://www.genomicsengland.co.uk/wp-content/uploads/2015/11/Genomics-England-logo-2015.png"

    static Closure customTemplate = {
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

    def exportRareDiseaseHPOAndClinicalTestsAsJson() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} report as Json",
            originalFileName: "$RD_PHENOTYPE_AND_CLINICAL_TESTS_JSON",
            contentType: "application/json",
        ) {
            new GelJsonExporter(it).printDiseaseOntology(dataClass)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseHPOEligibilityCriteriaAsJson() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} report as Json",
            originalFileName: "$RD_ELIGIBILITY_CRITERIA_JSON",
            contentType: "application/json",
        ) {
            new RareDiseasesJsonExporter(it).exportEligibilityCriteriaAsJson(dataClass)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseHPOAndClinicalTestsAsCsv() {

        DataClass dClass = DataClass.get(params.id)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dClass.getDefaultModelCatalogueId(true))

        Long assetId = assetService.storeReportAsAsset(latestVersion.dataModel,
            name: "${latestVersion.name} report as CSV",
            originalFileName: "$RD_HPO_CSV_FILENAME",
            contentType: "text/csv",
        ) {
            new GelCsvExporter(it).printDiseaseOntology(latestVersion)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseDisorderListAsCsv() {
        DataClass dClass = DataClass.get(params.id)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dClass.getDefaultModelCatalogueId(true))
        String name = "Rare Disease Disorder List"

        Long assetId = assetService.storeReportAsAsset(latestVersion.dataModel,
            name: "${name} as csv",
            originalFileName: "${name}-${latestVersion.status}-${latestVersion.version}.csv",
            contentType: "text/csv",
        ) {
            new RareDiseaseDisorderListCsvExporter(it).export(latestVersion)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def exportRareDiseaseHPOAndClinicalTests() {
        DataClass model = DataClass.get(params.id)

        if(!model) {
            response.status = 404
            return
        }

        Long classId = model.getId()

        Long assetId = assetService.storeReportAsAsset(model.dataModel,
            name: "${model.name} report as Json",
            originalFileName: "${model.name}-${model.status}-${model.version}.json",
            contentType: "application/json",
        ) {
            new GelJsonExporter(it).printDiseaseOntology(DataClass.get(classId))
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def exportRareDiseaseEligibilityDoc() {
        exportEligibilityOrPhenotypesAndTests(true)
    }

    def exportRareDiseasePhenotypesAndClinicalTestsDoc() {
        exportEligibilityOrPhenotypesAndTests(false)
    }


    private void exportEligibilityOrPhenotypesAndTests(boolean eligibilityMode) {
        DataClass model = DataClass.get(params.id)

        if (!model) {
            response.status = 404
            return
        }

        String documentName = RareDiseasesDocExporter.documentName(eligibilityMode)

        Long classId = model.id
        def assetId = assetService.storeReportAsAsset(
            model.dataModel,
            name: "${documentName} report as MS Word Document",
            originalFileName: "${documentName}-${model.status}-${model.version}.docx",
            contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new RareDiseasesDocExporter(DataClass.get(classId), standardTemplate, DOC_IMAGE_PATH, eligibilityMode).export(out)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
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
            new DataModelToDocxExporter(DataModel.get(modelId), dataClassService, customTemplate, DOC_IMAGE_PATH).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def exportCancerTypesAsJson() {
        DataClass model = DataClass.get(params.id)

        if(!model) {
            response.status = 404
            return
        }

        Long assetId = assetService.storeReportAsAsset(model.dataModel,
            name: "${model.name} report as Json",
            originalFileName: "${model.name}-${model.status}-${model.version}.json",
            contentType: "application/json",
        ) {
            new CancerTypesJsonExporter(it).exportCancerTypesAsJson(model)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportCancerTypesAsCsv() {
        DataClass model = DataClass.get(params.id)

        if(!model) {
            response.status = 404
            return
        }

        Long assetId = assetService.storeReportAsAsset(model.dataModel,
            name: "${model.name} report as csv",
            originalFileName: "${model.name}-${model.status}-${model.version}.csv",
            contentType: "text/csv",
        ) {
            new CancerTypesCsvExporter(it).exportCancerTypesAsCsv(model)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

}
