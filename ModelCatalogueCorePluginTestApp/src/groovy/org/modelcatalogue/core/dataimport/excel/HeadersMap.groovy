package org.modelcatalogue.core.dataimport.excel

class HeadersMap {
    static String dataElementCode = 'dataElementCode'
    static String dataElementName = 'dataElementName'
    static String dataElementDescription = 'dataElementDescription'
    @Deprecated static String dataTypeClassification = 'dataTypeClassification'
    static String dataTypeDataModel = 'dataTypeDataModel'
    static String dataTypeCode = 'dataTypeCode'
    static String dataTypeName = 'dataTypeName'
    @Deprecated static String valueDomainClassification = 'valueDomainClassification'
    @Deprecated static String valueDomainDataModel = 'valueDomainDataModel'
    @Deprecated static String valueDomainCode = 'valueDomainCode'
    @Deprecated static String valueDomainName = 'valueDomainName'
    @Deprecated static String parentModelName = 'parentModelName'
    static String parentDataClassName = 'parentDataClassName'
    @Deprecated static String parentModelCode = 'parentModelCode'
    static String parentDataClassCode = 'parentDataClassCode'
    @Deprecated static String containingModelName = 'containingModelName'
    static String containingDataClassName = 'containingDataClassName'
    @Deprecated static String containingModelCode = 'containingModelCode'
    static String containingDataClassCode = 'containingDataClassCode'
    static String measurementUnitName = 'measurementUnitName'
    static String measurementSymbol = 'measurementSymbol'
    static String metadata = 'metadata'
    @Deprecated static String classification = 'classification'
    static String dataModel = 'dataModel'

    // for spreadsheet produced by ExcelExporter (10 Oct 2017):
    static String multiplicity = 'multiplicity'
    static String dataTypeEnumerations = 'dataTypeEnumerations'
    static String dataTypeRule = 'dataTypeRule'
    static String measurementUnitCode = 'measurementUnitCode'

    static Map<String,String> createForSpreadsheetFromExcelExporter() {
        createForStandardExcelLoader([
                (parentDataClassName): "Parent Data Class Name",
                (parentDataClassCode): "Parent Data Class ID",
                (containingDataClassName): "Data Class Name",
                (containingDataClassCode): "Data Class ID",
                (dataElementCode):"Data Element ID",
                (dataElementName):"Data Element Name",
                (dataElementDescription):"Data Element Description",
                (dataTypeCode):"Data Type ID",
                (dataTypeName):"Data Type Name",
                (measurementUnitName):"Measurement Unit Name",
        ])
    }
    static Map<String,String> createForStandardExcelLoader(Map<String, Object> params = [:]){
        Map<String,String> headersMap = new LinkedHashMap<String,String>()
        headersMap.put(dataElementCode, params.get(dataElementCode) ?: 'Data Item Unique Code')
        headersMap.put(dataElementName, params.get(dataElementName) ?: 'Data Item Name')
        headersMap.put(dataElementDescription, params.get(dataElementDescription) ?: 'Data Item Description')
        headersMap.put(dataTypeName, params.get(dataTypeName) ?: 'Data Type')
        headersMap.put(dataTypeClassification, params.get(dataTypeClassification) ?: 'Data Type Classification')
        headersMap.put(dataTypeDataModel, params.get(dataTypeDataModel) ?: 'Data Type Data Model')
        headersMap.put(dataTypeCode, params.get(dataTypeCode) ?: 'Data Type Unique Code')
        headersMap.put(valueDomainName, params.get(valueDomainName) ?: 'Value Domain')
        headersMap.put(valueDomainClassification, params.get(valueDomainClassification) ?: 'Value Domain Classification')
        headersMap.put(valueDomainDataModel, params.get(valueDomainDataModel) ?: 'Value Domain Classification')
        headersMap.put(valueDomainCode, params.get(valueDomainCode) ?: 'Value Domain Unique Code')
        headersMap.put(parentModelName, params.get(parentModelName) ?: 'Parent Model')
        headersMap.put(parentDataClassName, params.get(parentDataClassName) ?: 'Parent Data Class')
        headersMap.put(parentModelCode, params.get(parentModelCode) ?: 'Parent Model Unique Code')
        headersMap.put(parentDataClassCode, params.get(parentDataClassCode) ?: 'Parent Data Class Unique Code')
        headersMap.put(containingModelName, params.get(containingModelName) ?: 'Model')
        headersMap.put(containingDataClassName, params.get(containingDataClassName) ?: 'Data Class')
        headersMap.put(containingModelCode, params.get(containingModelCode) ?: 'Model Unique Code')
        headersMap.put(containingDataClassCode, params.get(containingDataClassCode) ?: 'Data Class Unique Code')
        headersMap.put(measurementUnitName, params.get(measurementUnitName) ?: 'Measurement Unit')
        headersMap.put(measurementSymbol, params.get(measurementSymbol) ?: 'Measurement Unit Symbol')
        headersMap.put(classification, params.get(classification) ?: 'Classification')
        headersMap.put(dataModel, params.get(dataModel) ?: 'Data Model')
        headersMap.put(metadata, params.get(metadata) ?: 'Metadata')

        // for spreadsheet produced by ExcelExporter (10 Oct 2017):
        headersMap.put(multiplicity, params.get(multiplicity) ?: 'Multiplicity')
        headersMap.put(dataTypeEnumerations, params.get(dataTypeEnumerations) ?: 'Data Type Enumerations')
        headersMap.put(dataTypeRule, params.get(dataTypeRule) ?: 'Data Type Rule')
        headersMap.put(measurementUnitCode, params.get(measurementUnitCode) ?: 'Measurement Unit ID')
        return headersMap
    }
}
