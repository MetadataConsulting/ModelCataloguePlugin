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

    static HeadersMap create(Map<String, Object> params = [:]){
        HeadersMap headersMap = new HeadersMap()
        headersMap.dataElementCode = params.dataElementCode ?: "Data Item Unique Code"
        headersMap.dataElementName = params.dataElementName ?: "Data Item Name"
        headersMap.dataElementDescription = params.dataElementDescription ?: "Data Item Description"
        headersMap.dataTypeName = params.dataTypeName ?: "Data Type"
        headersMap.dataTypeClassification = params.dataTypeClassification ?: "Data Type Classification"
        headersMap.dataTypeDataModel = params.dataTypeDataModel ?: "Data Type Data Model"
        headersMap.dataTypeCode = params.dataTypeCode ?: "Data Type Unique Code"
        headersMap.valueDomainName = params.valueDomainName ?: "Value Domain"
        headersMap.valueDomainClassification = params.valueDomainClassification ?: "Value Domain Classification"
        headersMap.valueDomainDataModel = params.valueDomainDataModel ?: "Value Domain Classification"
        headersMap.valueDomainCode = params.valueDomainCode ?: "Value Domain Unique Code"
        headersMap.parentModelName = params.parentModelName ?: "Parent Model"
        headersMap.parentDataClassName = params.parentDataClassName ?: "Parent Data Class"
        headersMap.parentModelCode = params.parentModelCode ?: "Parent Model Unique Code"
        headersMap.parentDataClassCode = params.parentDataClassCode ?: "Parent Data Class Unique Code"
        headersMap.containingModelName = params.containingModelName ?: "Model"
        headersMap.containingDataClassName = params.containingDataClassName ?: "Data Class"
        headersMap.containingModelCode = params.containingModelCode ?: "Model Unique Code"
        headersMap.containingDataClassCode = params.containingDataClassCode ?: "Data Class Unique Code"
        headersMap.measurementUnitName = params.measurementUnitName ?: "Measurement Unit"
        headersMap.measurementSymbol = params.measurementSymbol ?: "Measurement Unit Symbol"
        headersMap.classification = params.classification ?: "Classification"
        headersMap.dataModel = params.dataModel ?: "Data Model"
        headersMap.metadata = params.metadata ?: "Metadata"
        return headersMap
    }
}
