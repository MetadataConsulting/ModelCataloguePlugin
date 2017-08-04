package org.modelcatalogue.integration.excel

class HeadersMap {
    String dataElementCode
    String dataElementName
    String dataElementDescription
    @Deprecated String dataTypeClassification
    String dataTypeDataModel
    String dataTypeCode
    String dataTypeName
    @Deprecated String valueDomainClassification
    @Deprecated String valueDomainDataModel
    @Deprecated String valueDomainCode
    @Deprecated String valueDomainName
    @Deprecated String parentModelName
    String parentDataClassName
    @Deprecated String parentModelCode
    String parentDataClassCode
    @Deprecated String containingModelName
    String containingDataClassName
    @Deprecated String containingModelCode
    String containingDataClassCode
    String measurementUnitName
    String measurementSymbol
    String metadata
    @Deprecated String classification
    String dataModel

    private HeadersMap(){}

    static Map<String,String> createForStandardExcelLoader(Map<String, Object> params = [:]){
        Map<String,String> headersMap = new LinkedHashMap<String,String>()
        headersMap.put('dataElementCode', params.dataElementCode ?: "Data Item Unique Code")
        headersMap.put('dataElementName', params.dataElementName ?: "Data Item Name")
        headersMap.put('dataElementDescription', params.dataElementDescription ?: "Data Item Description")
        headersMap.put('dataTypeName', params.dataTypeName ?: "Data Type")
        headersMap.put('dataTypeClassification', params.dataTypeClassification ?: "Data Type Classification")
        headersMap.put('dataTypeDataModel', params.dataTypeDataModel ?: "Data Type Data Model")
        headersMap.put('dataTypeCode', params.dataTypeCode ?: "Data Type Unique Code")
        headersMap.put('valueDomainName', params.valueDomainName ?: "Value Domain")
        headersMap.put('valueDomainClassification', params.valueDomainClassification ?: "Value Domain Classification")
        headersMap.put('valueDomainDataModel', params.valueDomainDataModel ?: "Value Domain Classification")
        headersMap.put('valueDomainCode', params.valueDomainCode ?: "Value Domain Unique Code")
        headersMap.put('parentModelName', params.parentModelName ?: "Parent Model")
        headersMap.put('parentDataClassName', params.parentDataClassName ?: "Parent Data Class")
        headersMap.put('parentModelCode', params.parentModelCode ?: "Parent Model Unique Code")
        headersMap.put('parentDataClassCode', params.parentDataClassCode ?: "Parent Data Class Unique Code")
        headersMap.put('containingModelName', params.containingModelName ?: "Model")
        headersMap.put('containingDataClassName', params.containingDataClassName ?: "Data Class")
        headersMap.put('containingModelCode', params.containingModelCode ?: "Model Unique Code")
        headersMap.put('containingDataClassCode', params.containingDataClassCode ?: "Data Class Unique Code")
        headersMap.put('measurementUnitName', params.measurementUnitName ?: "Measurement Unit")
        headersMap.put('measurementSymbol', params.measurementSymbol ?: "Measurement Unit Symbol")
        headersMap.put('classification', params.classification ?: "Classification")
        headersMap.put('dataModel', params.dataModel ?: "Data Model")
        headersMap.put('metadata', params.metadata ?: "Metadata")
        return headersMap
    }

}
