package org.modelcatalogue.gel

import groovy.transform.CompileStatic
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.AssetMetadata
import org.modelcatalogue.core.AssetMetadataService
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.asset.MicrosoftOfficeDocument
import org.modelcatalogue.gel.export.RareDiseasesDocExporter

@CompileStatic
class GenomicsAssetMetadataService {
    AssetMetadataService assetMetadataService
    ElementService elementService

    static final String RD_ELIGIBILITY_CSV_FILENAME = "RD Eligibility Criteria.csv"
    static final String RD_HPO_CSV_FILENAME = "RD Phenotypes and Clinical Tests.csv"
    static final String RD_ELIGIBILITY_CRITERIA_JSON = "RD Eligibility Criteria.json"
    static final String RD_WEB = "website.zip"
    static final String RD_PHENOTYPE_AND_CLINICAL_TESTS_XLS = "RD Change log for Phenotypes and clinical tests.xlsx"
    static final String RD_ELIGIBILITY_CHANGELOG_XLS = "RD Change Log for Eligibility Criteria.xlsx"


    Asset genRareDiseaseHPOAndClinicalTestsAsJson(DataClass latestVersion){
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${latestVersion.name} - HPO and Clinical Tests report (JSON)",
                originalFileName: "${latestVersion.name}-${latestVersion.status}-${latestVersion.version}.json",
                contentType: "application/json",
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genDiseaseListOnlyAsJson(DataClass latestVersion) {
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${latestVersion.name} - Disease List Only (JSON)",
                originalFileName: "${latestVersion.name}-${latestVersion.status}-${latestVersion.version}.json",
                contentType: "application/json",
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genRareDiseaseHPOEligibilityCriteriaAsJson(DataClass dataClass) {
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${dataClass.name} - Eligibility criteria report (JSON)",
                originalFileName: "$RD_ELIGIBILITY_CRITERIA_JSON",
                contentType: "application/json",
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genRareDiseaseCsv(def docType){
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "Eligibility Report: ${docType == RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS ? RD_HPO_CSV_FILENAME : RD_ELIGIBILITY_CSV_FILENAME}",
                originalFileName: "${docType == RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS ? RD_HPO_CSV_FILENAME : RD_ELIGIBILITY_CSV_FILENAME}",
                contentType: "text/csv",
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genRareDiseaseDisorderListAsCsv(DataClass latestVersion) {
        String name = "Rare Disease Disorder List"
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${name} (CSV)",
                originalFileName: "${name}-${latestVersion.status}-${latestVersion.version}.csv",
                contentType: "text/csv",
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genEligibilityOrPhenotypesAndTests(DataClass dataClass, boolean eligibilityMode) {
        String documentName = RareDiseasesDocExporter.documentName(eligibilityMode)
        MicrosoftOfficeDocument doc = MicrosoftOfficeDocument.DOC
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${documentName} report (${MicrosoftOfficeDocument.documentType(doc)})",
                originalFileName: "${documentName}-${dataClass.status}-${dataClass.version}.${MicrosoftOfficeDocument.suffix(doc)}",
                contentType: MicrosoftOfficeDocument.contentType(doc)
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genSplitDocAsset(DataClass dataClass){
        String documentName = "Rare Disease Eligibility, Phenotypes and Clinical Tests for $dataClass.name"

        MicrosoftOfficeDocument doc = MicrosoftOfficeDocument.DOC
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${documentName} report (${MicrosoftOfficeDocument.documentType(doc)})",
                originalFileName: "${documentName}-${dataClass.status}-${dataClass.version}.${MicrosoftOfficeDocument.suffix(doc)}",
                contentType: MicrosoftOfficeDocument.contentType(doc)
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }


    Asset genRareDiseaseHPOAndClinicalTestsAsXls(DataClass dataClass) {

        MicrosoftOfficeDocument doc = MicrosoftOfficeDocument.XLSX
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${dataClass.name} - HPO and Clinical Tests (${MicrosoftOfficeDocument.documentType(doc)})",
                originalFileName: "$RD_PHENOTYPE_AND_CLINICAL_TESTS_XLS",
                contentType: MicrosoftOfficeDocument.contentType(doc)
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genRareDiseaseEligibilityChangeLogAsXls(DataClass dataClass) {
        MicrosoftOfficeDocument doc = MicrosoftOfficeDocument.XLSX
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${dataClass.name} - Eligibility change log (${MicrosoftOfficeDocument.documentType(doc)})",
                originalFileName: "$RD_ELIGIBILITY_CHANGELOG_XLS",
                contentType: MicrosoftOfficeDocument.contentType(doc)
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genChangeLogDocument(DataClass dataClass, String name){
        AssetMetadata assetMetadata = new AssetMetadata(
                name: name ? name : "${dataClass.name} - change log (MS Word Document)".toString(),
                originalFileName: "${dataClass.name}-${dataClass.status}-${dataClass.version}-changelog.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genDataSpecChangeLogAsXls(DataModel model) {
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${model.name}  - Specification change log (MS Excel Spreadsheet)",
                originalFileName: "${model.name}-${model.status}-${model.version}-changelog.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }

    Asset genRareDiseaseWebsite(DataModel dataModel) {
        AssetMetadata assetMetadata = new AssetMetadata(
                name: "${dataModel.name} - Static Website",
                originalFileName: "$RD_WEB",
                contentType: "application/zip",
        )
         assetMetadataService.instantiateAssetWithMetadata(assetMetadata)
    }
}