package org.modelcatalogue.core

import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder

/**
 * Created by sus_avi on 01/05/2014.
 */

class DataImportControllerSpec extends AbstractIntegrationSpec implements ResultRecorder {

    def fileName, recorder, filenameXsd, filenameXsd2, fileNameStarUML

    def setup() {
        fileName = "test/integration/resources/example.xls"
        filenameXsd = "test/unit/resources/SACT/XMLDataTypes.xsd"//"test/unit/resources/SACT/XSD_Example.xsd"
        filenameXsd2 = "test/unit/resources/SACT/Breast_XMLSchema.xsd"//"test/unit/resources/SACT/XSD_Example.xsd"
        fileNameStarUML = "test/integration/resources/gel_cancer_combined2.umlj"
        loadMarshallers()
        loadFixtures()
        recorder = DefaultResultRecorder.create(
                "../ModelCatalogueCorePlugin/test/js/modelcatalogue/core",
                "Importer"
        )
    }

    def testHeaderSetup(){
        HeadersMap headersMap = DataImportController.populateHeaders([])
        expect:
        headersMap.dataElementCode == "Data Item Unique Code"
        headersMap.dataElementName == "Data Item Name"
        headersMap.dataElementDescription == "Data Item Description"
        headersMap.dataType == "Data Type"
        headersMap.parentModelName == "Parent Model"
        headersMap.parentModelCode == "Parent Model Unique Code"
        headersMap.containingModelName == "Model"
        headersMap.containingModelCode == "Model Unique Code"
        headersMap.measurementUnitName == "Measurement Unit"
        headersMap.measurementSymbol == "Measurement Unit Symbol"
        headersMap.classification == "Classification"
        headersMap.metadata == "Metadata"

    }

    def testCustomHeaderSetup(){

        Map<String,String> params = [:]

        params.dataElementCode = "Data Item UC"
        params.dataElementName = "DataI Name"
        params.dataElementDescription = "Description"
        params.dataType = "DataType"
        params.parentModelName = "parentModel"
        params.parentModelCode = "Parent Model UC"
        params.containingModelName = "ModelName"
        params.containingModelCode = "ModelUC"
        params.measurementUnitName = "measurement"
        params.measurementSymbol = "MeasurementSymbol"
        params.classification = "Classification"
        params.metadata = "metadata"

        HeadersMap headersMap = DataImportController.populateHeaders(params)
        expect:
        headersMap.dataElementCode == "Data Item UC"
        headersMap.dataElementName == "DataI Name"
        headersMap.dataElementDescription == "Description"
        headersMap.dataType == "DataType"
        headersMap.parentModelName == "parentModel"
        headersMap.parentModelCode == "Parent Model UC"
        headersMap.containingModelName == "ModelName"
        headersMap.containingModelCode == "ModelUC"
        headersMap.measurementUnitName == "measurement"
        headersMap.measurementSymbol == "MeasurementSymbol"
        headersMap.classification == "Classification"
        headersMap.metadata == "metadata"

    }


    @Override
    File recordResult(String fixtureName, JSONElement json) {
        recorder.recordResult(fixtureName, json)
    }

    @Override
    File recordInputJSON(String fixtureName, Map json) {
        recorder.recordInputJSON(fixtureName, json)
    }

    @Override
    File recordInputJSON(String fixtureName, String json) {
        recorder.recordInputJSON(fixtureName, json)
    }
}
