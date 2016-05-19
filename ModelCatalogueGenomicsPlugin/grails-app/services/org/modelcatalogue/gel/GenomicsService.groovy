package org.modelcatalogue.gel

import grails.transaction.Transactional

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator
import org.modelcatalogue.gel.export.CancerTypesCsvExporter
import org.modelcatalogue.gel.export.CancerTypesJsonExporter
import org.modelcatalogue.gel.export.DataModelChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseaseDisorderListCsvExporter
import org.modelcatalogue.gel.export.RareDiseaseEligibilityChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseasePhenotypeChangeLogXlsExporter
import org.modelcatalogue.gel.export.RareDiseasesDocExporter
import org.modelcatalogue.gel.export.RareDiseasesJsonExporter

import static org.modelcatalogue.gel.export.RareDiseasesDocExporter.getStandardTemplate

@Transactional
class GenomicsService {

    def auditService
    def assetService
    def dataClassService
    def elementService

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

    long genRareDiseaseHPOEligibilityCriteriaAsJson(DataClass dataClass){
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - Eligibility criteria report (JSON)",
            originalFileName: "$RD_ELIGIBILITY_CRITERIA_JSON",
            contentType: "application/json",
        ) {
            new RareDiseasesJsonExporter(it).exportEligibilityCriteriaAsJson(dataClass)
        }
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

    long genCancerTypesAsJson(DataClass dataClass){
        return assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - Cancer Types report (JSON)",
            originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}.json",
            contentType: "application/json",
        ) {
            new CancerTypesJsonExporter(it).exportCancerTypesAsJson(dataClass)
        }
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

    long genRareDiseaseHPOAndClinicalTestsAsXls(DataClass dataClass) {
        assetService.storeReportAsAsset(dataClass.dataModel,
            name: "${dataClass.name} - HPO and Clinical Tests (MS Excel Spreadsheet)",
            originalFileName: "$RD_PHENOTYPE_AND_CLINICAL_TESTS_XLS",
            contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream out ->
            new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 0, false).export(dataClass,out)
        }
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

    long genDataSpecChangeLogAsXls(DataModel model) {
        return assetService.storeReportAsAsset(model,
            name: "${model.name}  - Specification change log (MS Excel Spreadsheet)",
            originalFileName: "${model.name}-${model.status}-${model.version}-changelog.xlsx",
            contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ) { OutputStream out ->
            new DataModelChangeLogXlsExporter(auditService, dataClassService, 0, false).export(model, out)
        }
    }

}
