package org.modelcatalogue.core.genomics

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.gel.RareDiseaseCsvExporter
import org.modelcatalogue.gel.GelJsonExporter
import org.modelcatalogue.gel.export.CancerTypesCsvExporter
import org.modelcatalogue.gel.export.CancerTypesJsonExporter
import org.modelcatalogue.gel.export.DataModelChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseaseDisorderListCsvExporter
import org.modelcatalogue.gel.export.RareDiseaseEligibilityChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseasePhenotypeChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseasesDocExporter
import org.modelcatalogue.gel.export.RareDiseasesJsonExporter
import org.springframework.http.HttpStatus

import static RareDiseasesDocExporter.getStandardTemplate

/**
 * Controller for GEL specific reports.
 */
class GenomicsController {

    def assetService
    AuditService auditService
    def elementService
    DataClassService dataClassService

    static final String RD_ELIGIBILITY_CSV_FILENAME = "RD Eligibility Criteria.csv"
    static final String RD_HPO_CSV_FILENAME = "RD Phenotypes and Clinical Tests.csv"
    static final String RD_ELIGIBILITY_CRITERIA_JSON = "RD Eligibility Criteria.json"
    static final String RD_PHENOTYPE_AND_CLINICAL_TESTS_XLS = "RD Change log for Phenotypes and clinical tests.xlsx"
    static final String RD_ELIGIBILITY_CHANGELOG_XLS = "RD Change Log for Eligibility Criteria.xlsx"

    static final String DOC_IMAGE_PATH = "https://www.genomicsengland.co.uk/wp-content/uploads/2015/11/Genomics-England-logo-2015.png"

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

    def exportRareDiseaseHPOAndClinicalTestsAsJson() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genRareDiseaseHPOAndClinicalTestsAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genRareDiseaseHPOAndClinicalTestsAsJson(DataClass dataClass){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dataClass.getDefaultModelCatalogueId(true))

        return assetService.storeReportAsAsset(latestVersion.dataModel,
            name: "${latestVersion.name} - HPO and Clinical Tests report (JSON)",
            originalFileName: "${latestVersion.name}-${latestVersion.status}-${latestVersion.version}.json",
            contentType: "application/json",
        ) {
            new GelJsonExporter(it).printDiseaseOntology(latestVersion)
        }
    }

    def exportRareDiseaseEligibilityCsv() {
        exportRareDiseaseCsv(RareDiseaseCsvExporter.ELIGIBILITY)
    }

    def exportRareDiseaseHPOEligibilityCriteriaAsJson() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genRareDiseaseHPOEligibilityCriteriaAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genRareDiseaseHPOEligibilityCriteriaAsJson(DataClass dataClass){
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - Eligibility criteria report (JSON)",
            originalFileName: "$RD_ELIGIBILITY_CRITERIA_JSON",
            contentType: "application/json",
        ) {
            new RareDiseasesJsonExporter(it).exportEligibilityCriteriaAsJson(dataClass)
        }
    }

    def exportRareDiseaseHPOAndClinicalTestsAsCsv() {
        exportRareDiseaseCsv(RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS)
    }

    def exportRareDiseaseCsv(def docType) {
        DataClass dClass = DataClass.get(params.id)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genRareDiseaseCsv(dClass, docType)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genRareDiseaseCsv(DataClass dClass, def docType){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dClass.getDefaultModelCatalogueId(true))

        return assetService.storeReportAsAsset(latestVersion.dataModel,
            name: "${latestVersion.name} report (CSV)",
            originalFileName: "${docType == RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS ? RD_HPO_CSV_FILENAME : RD_ELIGIBILITY_CSV_FILENAME}",
            contentType: "text/csv",
        ) {
            new RareDiseaseCsvExporter(it, docType).printReport(latestVersion)
        }
    }

    def exportRareDiseaseDisorderListAsCsv() {
        DataClass dClass = DataClass.get(params.id)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genRareDiseaseDisorderListAsCsv(dClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genRareDiseaseDisorderListAsCsv(DataClass dClass){
        DataClass latestVersion = (DataClass) elementService.findByModelCatalogueId(DataClass, dClass.getDefaultModelCatalogueId(true))
        String name = "Rare Disease Disorder List"

        return assetService.storeReportAsAsset(latestVersion.dataModel,
            name: "${name} (CSV)",
            originalFileName: "${name}-${latestVersion.status}-${latestVersion.version}.csv",
            contentType: "text/csv",
        ) {
            new RareDiseaseDisorderListCsvExporter(it).export(latestVersion)
        }
    }


//    def exportRareDiseaseHPOAndClinicalTests() {
//        DataClass dataClass = DataClass.get(params.id)
//
//        if (!dataClass) {
//            respond status: HttpStatus.NOT_FOUND
//            return
//        }
//
//        def assetId = genRareDiseaseHPOAndClinicalTestsJson(dataClass)
//
//        response.setHeader("X-Asset-ID", assetId.toString())
//        redirect controller: 'asset', id: assetId, action: 'show'
//    }

//    long genRareDiseaseHPOAndClinicalTestsJson(DataClass dataClass){
//        Long classId = dataClass.getId()
//
//        return assetService.storeReportAsAsset(dataClass.dataModel,
//            name: "${dataClass.name} - HPO and Clinical Tests report (JSON)",
//            originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}.json",
//            contentType: "application/json",
//        ) {
//            new GelJsonExporter(it).printDiseaseOntology(DataClass.get(classId))
//        }
//    }


    def exportRareDiseaseEligibilityDoc() {
        exportEligibilityOrPhenotypesAndTests(true)
    }

    def exportRareDiseasePhenotypesAndClinicalTestsDoc() {
        exportEligibilityOrPhenotypesAndTests(false)
    }


    private void exportEligibilityOrPhenotypesAndTests(boolean eligibilityMode) {
        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genEligibilityOrPhenotypesAndTests(dataClass, eligibilityMode)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genEligibilityOrPhenotypesAndTests(DataClass dataClass, boolean eligibilityMode) {
        String documentName = RareDiseasesDocExporter.documentName(eligibilityMode)

        Long classId = dataClass.id
        return assetService.storeReportAsAsset(
            dataClass.dataModel,
            name: "${documentName} report (MS Word Document)",
            originalFileName: "${documentName}-${dataClass.status}-${dataClass.version}.docx",
            contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new RareDiseasesDocExporter(DataClass.get(classId), standardTemplate, DOC_IMAGE_PATH, eligibilityMode).export(out)
        }
    }

    def exportGelSpecification() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genGelSpecification(model)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genGelSpecification(DataModel model){
        Long modelId = model.id
        return assetService.storeReportAsAsset(
            model,
            name: "${model.name} - Data Specification Report (MS Word Document)",
            originalFileName: "${model.name}-${model.status}-${model.version}.docx",
            contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new DataModelToDocxExporter(DataModel.get(modelId), dataClassService, customTemplate, DOC_IMAGE_PATH).export(out)
        }
    }


    def exportCancerTypesAsJson() {
        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genCancerTypesAsJson()

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genCancerTypesAsJson(DataClass dataClass){
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - Cancer Types report (JSON)",
            originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}.json",
            contentType: "application/json",
        ) {
            new CancerTypesJsonExporter(it).exportCancerTypesAsJson(dataClass)
        }
    }

    def exportCancerTypesAsCsv() {
        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genCancerTypesAsCsv(dataClass)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genCancerTypesAsCsv(DataClass dataClass){
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - Cancer Types report (CSV)",
            originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}.csv",
            contentType: "text/csv",
        ) {
            new CancerTypesCsvExporter(it).exportCancerTypesAsCsv(dataClass)
        }
    }

    def exportRareDiseaseHPOAndClinicalTestsAsXls() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genRareDiseaseHPOAndClinicalTestsAsXls(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genRareDiseaseHPOAndClinicalTestsAsXls(DataClass dataClass) {
        assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - HPO and Clinical Tests (MS Excel Spreadsheet)",
            originalFileName: "$RD_PHENOTYPE_AND_CLINICAL_TESTS_XLS",
            contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream out ->
            new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 0, false).export(dataClass,out)
        }
    }

    def exportRareDiseaseEligibilityChangeLogAsXls() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genRareDiseaseEligibilityChangeLogAsXls(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genRareDiseaseEligibilityChangeLogAsXls(DataClass dataClass){
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - Eligibility change log (MS Excel Spreadsheet)",
            originalFileName: "$RD_ELIGIBILITY_CHANGELOG_XLS",
            contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream out ->
            new RareDiseaseEligibilityChangeLogXlsExporter(auditService, dataClassService, 0, false).export(dataClass, out)
        }
    }

    def exportChangeLogDocument(String name, Integer depth, Boolean includeMetadata) {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genChangeLogDocument(dataClass, name, depth, includeMetadata)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genChangeLogDocument(DataClass dataClass, String name, Integer depth, Boolean includeMetadata){
        Long classId = dataClass.id
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: name ? name : "${dataClass.name} - change log (MS Word Document)",
            originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}-changelog.docx",
            contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new ChangeLogDocxGenerator(auditService, dataClassService, depth, includeMetadata, customTemplate, DOC_IMAGE_PATH)
                .generateChangelog(DataClass.get(classId), out)
        }
    }

    def exportDataSpecChangeLogAsXls() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genDataSpecChangeLogAsXls(model)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    long genDataSpecChangeLogAsXls(DataModel model) {
        return assetService.storeReportAsAsset(model,
            name: "${model.name}  - Specification change log (MS Excel Spreadsheet)",
            originalFileName: "${model.name}-${model.status}-${model.version}-changelog.xlsx",
            contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream out ->
            new DataModelChangeLogXlsExporter(auditService, dataClassService, 0, false).export(model, out)
        }
    }

    def exportAllRareDiseaseReports() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        //Generate Model reports
        genGelSpecification(model)
        genDataSpecChangeLogAsXls(model)

        //Generate Class reports
        DataClass dataClass = dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) model)).items.get(0)
        genRareDiseaseHPOAndClinicalTestsAsXls(dataClass)
        genCancerTypesAsCsv(dataClass)
        genCancerTypesAsJson(dataClass)
        genChangeLogDocument(dataClass,dataClass.name,3,true)
        genEligibilityOrPhenotypesAndTests(dataClass,true)
        genEligibilityOrPhenotypesAndTests(dataClass,false)
        genRareDiseaseCsv(dataClass,RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS)
        genRareDiseaseCsv(dataClass,RareDiseaseCsvExporter.ELIGIBILITY)
        genRareDiseaseDisorderListAsCsv(dataClass)
        genRareDiseaseEligibilityChangeLogAsXls(dataClass)
        genRareDiseaseHPOAndClinicalTestsAsJson(dataClass)
        genRareDiseaseHPOEligibilityCriteriaAsJson(dataClass)

        redirect uri: "/#/${model.id}/asset/all?status=active"

    }

    def exportAllCancerReports() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        //Generate Model reports
        genGelSpecification(model)
        genDataSpecChangeLogAsXls(model)

        //Generate Class reports
        DataClass dataClass = dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) model)).items.get(0)
        genCancerTypesAsCsv(dataClass)
        genCancerTypesAsJson(dataClass)
        genChangeLogDocument(dataClass,dataClass.name,3,true)

        redirect uri: "/#/${model.id}/asset/all?status=active"

    }

}
