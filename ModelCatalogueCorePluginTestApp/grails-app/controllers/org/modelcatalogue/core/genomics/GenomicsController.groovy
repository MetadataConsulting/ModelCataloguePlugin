package org.modelcatalogue.core.genomics

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
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseHPOAndClinicalTestsAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseListAsJson() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

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
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

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
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dClass = findRootClass(model)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genomicsService.genRareDiseaseCsv(dClass, docType)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseDisorderListAsCsv() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dClass = findRootClass(model)

        if (!dClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseDisorderListAsCsv(dClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    private DataClass findRootClass(DataModel model) {
        dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) model), [status: 'active']).items?.get(0)
    }

    def exportRareDiseaseEligibilityDoc() {
        exportEligibilityOrPhenotypesAndTests(true)
    }

    def exportRareDiseasePhenotypesAndClinicalTestsDoc() {
        exportEligibilityOrPhenotypesAndTests(false)
    }


    private void exportEligibilityOrPhenotypesAndTests(boolean eligibilityMode) {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def assetId = genomicsService.genEligibilityOrPhenotypesAndTests(dataClass, eligibilityMode)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def exportRareDiseaseSplitDocs() {

        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        def  assetId

        //iterate through the child of the top level maintenance class
        // then create documents for each of the children

        dataClass.parentOf.eachWithIndex{ DataClass child, idx ->
            String documentName = "Rare Disease Eligibility and Phenotypes for $child.name"
            genomicsService.genSplitDocAsset(child)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }


    def exportCancerTypesAsJson() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genCancerTypesAsJson(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportCancerTypesAsCsv() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genCancerTypesAsCsv(dataClass)

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseHPOAndClinicalTestsAsXls() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Long assetId = genomicsService.genRareDiseaseHPOAndClinicalTestsAsXls(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportRareDiseaseEligibilityChangeLogAsXls() {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

        if (!dataClass) {
            respond status: HttpStatus.NOT_FOUND
            return
        }
        Long assetId = genomicsService.genRareDiseaseEligibilityChangeLogAsXls(dataClass)

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def exportChangeLogDocument(String name, Integer depth, Boolean includeMetadata) {
        DataModel model = DataModel.get(params.id)

        if (!model) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        DataClass dataClass = findRootClass(model)

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

        //Generate Class reports
        DataClass dataClass = findRootClass(model)
// remove potentially        genomicsService.genRareDiseaseHPOAndClinicalTestsAsXls(dataClass)
        genomicsService.genEligibilityOrPhenotypesAndTests(dataClass,true)
        genomicsService.genEligibilityOrPhenotypesAndTests(dataClass,false)
        genomicsService.genRareDiseaseCsv(dataClass,RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS)
        genomicsService.genRareDiseaseCsv(dataClass,RareDiseaseCsvExporter.ELIGIBILITY)
        genomicsService.genRareDiseaseDisorderListAsCsv(dataClass)
        genomicsService.genRareDiseaseHPOAndClinicalTestsAsJson(dataClass)
        genomicsService.genRareDiseaseHPOEligibilityCriteriaAsJson(dataClass)
        genomicsService.genRareDiseaseWebsite(model)

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
        DataClass dataClass = findRootClass(model)
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
