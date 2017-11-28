package org.modelcatalogue.core.dataimport

import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.dataimport.excel.HeadersMap
import org.modelcatalogue.core.util.DefaultResultRecorder
import org.modelcatalogue.core.util.ResultRecorder

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
                "../ModelCatalogueCorePluginTestApp/test/js/modelcatalogue/core",
                "Importer"
        )
    }

    def testHeaderSetup() {
        Map<String,String> headersMap = HeadersMap.createForStandardExcelLoader()
        expect:
        headersMap.dataElementCode == "Data Item Unique Code"
        headersMap.dataElementName == "Data Item Name"
        headersMap.dataElementDescription == "Data Item Description"
        headersMap.dataTypeClassification == "Data Type Classification"
        headersMap.dataTypeCode == "Data Type Unique Code"
        headersMap.dataTypeName == "Data Type"
        headersMap.parentModelName == "Parent Model"
        headersMap.parentModelCode == "Parent Model Unique Code"
        headersMap.containingModelName == "Model"
        headersMap.containingModelCode == "Model Unique Code"
        headersMap.measurementUnitName == "Measurement Unit"
        headersMap.measurementSymbol == "Measurement Unit Symbol"
        headersMap.classification == "Classification"
        headersMap.metadata == "Metadata"
    }

    def testCustomHeaderSetup() {

        Map<String,Object> params = [:]

        params.put("dataElementCode", "Data Item UC")
        params.put("dataElementName", "DataI Name")
        params.put("dataElementDescription", "Description")
        params.put("dataTypeClassification", "DataTypeClassification")
        params.put("dataTypeCode", "DataTypeUniqueCode")
        params.put("dataTypeName", "DataType")
        params.put("parentModelName", "parentModel")
        params.put("parentModelCode", "Parent Model UC")
        params.put("containingModelName", "ModelName")
        params.put("containingModelCode", "ModelUC")
        params.put("measurementUnitName", "measurement")
        params.put("measurementSymbol", "MeasurementSymbol")
        params.put("classification", "Classification")
        params.put("metadata", "metadata")

        Map<String,String> headersMap = HeadersMap.createForStandardExcelLoader(params)

        expect:
        headersMap.dataElementCode == "Data Item UC"
        headersMap.dataElementName == "DataI Name"
        headersMap.dataElementDescription == "Description"
        headersMap.dataTypeClassification == "DataTypeClassification"
        headersMap.dataTypeCode == "DataTypeUniqueCode"
        headersMap.dataTypeName == "DataType"
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
