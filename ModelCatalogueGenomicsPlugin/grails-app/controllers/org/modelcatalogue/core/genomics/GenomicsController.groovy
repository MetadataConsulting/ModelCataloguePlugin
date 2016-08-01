package org.modelcatalogue.core.genomics

import grails.gsp.PageRenderer
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.gel.GenomicsService
import org.modelcatalogue.gel.RareDiseaseCsvExporter
import org.springframework.http.HttpStatus
/**
 * Controller for GEL specific reports.
 */
class GenomicsController {

    DataClassService dataClassService
    GenomicsService genomicsService

    def exportRareDiseaseHPOAndClinicalTestsAsJson() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseHPOAndClinicalTestsAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseListAsJson() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genDiseaseListOnlyAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
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

        Long assetId = genomicsService.genRareDiseaseHPOEligibilityCriteriaAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
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

        def assetId = genomicsService.genRareDiseaseCsv(dClass, docType)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseDisorderListAsCsv() {
        DataClass dClass = DataClass.get(params.id)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseDisorderListAsCsv(dClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
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

        def assetId = genomicsService.genEligibilityOrPhenotypesAndTests(dataClass, eligibilityMode)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportGelSpecification(Integer depth) {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }
        def assetId = genomicsService.genGelSpecification(model, depth)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportCancerTypesAsJson() {
        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genCancerTypesAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportCancerTypesAsCsv() {
        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genCancerTypesAsCsv(dataClass)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseHPOAndClinicalTestsAsXls() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseHPOAndClinicalTestsAsXls(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseEligibilityChangeLogAsXls() {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseEligibilityChangeLogAsXls(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportChangeLogDocument(String name, Integer depth, Boolean includeMetadata) {

        DataClass dataClass = DataClass.get(params.id)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genomicsService.genChangeLogDocument(dataClass, name, depth, includeMetadata)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportDataSpecChangeLogAsXls() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genDataSpecChangeLogAsXls(model)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportAllRareDiseaseReports() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        //Generate Model reports
        genomicsService.genGelSpecification(model,3)
        genomicsService.genDataSpecChangeLogAsXls(model)

        //Generate Class reports
        DataClass dataClass = dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) model)).items.get(0)
        genomicsService.genRareDiseaseHPOAndClinicalTestsAsXls(dataClass)
        genomicsService.genCancerTypesAsCsv(dataClass)
        genomicsService.genCancerTypesAsJson(dataClass)
        genomicsService.genChangeLogDocument(dataClass,dataClass.name,3,true)
        genomicsService.genEligibilityOrPhenotypesAndTests(dataClass,true)
        genomicsService.genEligibilityOrPhenotypesAndTests(dataClass,false)
        genomicsService.genRareDiseaseCsv(dataClass,RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS)
        genomicsService.genRareDiseaseCsv(dataClass,RareDiseaseCsvExporter.ELIGIBILITY)
        genomicsService.genRareDiseaseDisorderListAsCsv(dataClass)
        genomicsService.genRareDiseaseEligibilityChangeLogAsXls(dataClass)
        genomicsService.genRareDiseaseHPOAndClinicalTestsAsJson(dataClass)
        genomicsService.genRareDiseaseHPOEligibilityCriteriaAsJson(dataClass)

        redirect uri: "/#/${model.id}/asset/all?status=active"

    }

    def exportAllCancerReports() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        //Generate Model reports
        genomicsService.genGelSpecification(model,3)
        genomicsService.genDataSpecChangeLogAsXls(model)

        //Generate Class reports
        DataClass dataClass = dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) model)).items.get(0)
        genomicsService.genCancerTypesAsCsv(dataClass)
        genomicsService.genCancerTypesAsJson(dataClass)
        genomicsService.genChangeLogDocument(dataClass,dataClass.name,3,true)

        redirect uri: "/#/${model.id}/asset/all?status=active"

    }

    def exportRareDiseasesWebsite() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }


        Long assetId = genomicsService.genRareDiseaseWebsite(model)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'

    }

}
