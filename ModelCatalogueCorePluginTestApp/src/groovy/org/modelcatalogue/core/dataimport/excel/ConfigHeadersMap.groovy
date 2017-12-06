package org.modelcatalogue.core.dataimport.excel

import org.modelcatalogue.core.dataimport.excel.HeadersMap

class ConfigHeadersMap extends HeadersMap {
    // These have changed their names
    static String containingDataClassCode = 'dataClassID'
    static String containingDataClassName = 'dataClassName'
    static String dataElementCode = 'dataElementID'
    static String multiplicity = 'dataElementMultiplicity'
    static String dataTypeCode = 'dataTypeID'
    static String dataTypeEnumerations = 'dataTypeEnums'
    static String measurementUnitCode = 'measurementUnitID'
    static String measurementSymbol = 'measurementUnitSymbol'

    static Map<String,String> createForGenericExcelLoader(Map<String, Object> params = [:]){
        Map<String,String> headersMap = new LinkedHashMap<String,String>()

        // for spreadsheet produced by ExcelExporter (10 Oct 2017):
        headersMap.put(multiplicity, params.get(multiplicity) ?: 'Multiplicity')
        headersMap.put(dataTypeEnumerations, params.get(dataTypeEnumerations) ?: 'Data Type Enumerations')
        headersMap.put(HeadersMap.dataTypeRule, params.get(HeadersMap.dataTypeRule) ?: 'Data Type Rule')
        headersMap.put(measurementUnitCode, params.get(measurementUnitCode) ?: 'Measurement Unit ID')

        return headersMap
    }
}
