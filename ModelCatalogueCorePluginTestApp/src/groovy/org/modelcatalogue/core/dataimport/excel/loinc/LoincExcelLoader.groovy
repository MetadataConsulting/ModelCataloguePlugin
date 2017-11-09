package org.modelcatalogue.core.dataimport.excel.loinc

import groovy.util.logging.Log
import org.apache.poi.ss.usermodel.*
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.HeadersMap

/**
 * This used to be a class for one purpose ("importData", now called "buildXmlFromStandardWorkbookSheet"), but now we have made it a parent class of
 * future NT Excel Loaders, so that they can access similar methods.
 * This may not be the best way
 */
@Log
class LoincExcelLoader extends ExcelLoader {

    static protected Map<String, String> createRowMap(Row row, List<String> headers) {
        Map<String, String> rowMap = new LinkedHashMap<>()
        // Important that it's LinkedHashMap, for order to be kept, to get the last section which is metadata!
        for (Cell cell : row) {
            rowMap = updateRowMap(rowMap, cell, headers)
        }
        return rowMap
    }
    /**
     * getCatalogueElementDtoFromRow
     * @param Cell cell
     * @param List rowData
     * @return CatalogueElementDto
     */
    static protected Map<String, String> updateRowMap(Map<String,String> rowMap, Cell cell,  List<String> headers) {
        def colIndex = cell.getColumnIndex()
        rowMap[headers[colIndex]] = valueHelper(cell)
        rowMap
    }

    static List<String> getRowData(Row row) {
        def data = []
        for (Cell cell : row) {
            getValue(cell, data)
        }
        data
    }

    static List<Map<String,String>> getRowMaps(Sheet sheet) {

        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)
        log.info("Headers are ${headers as String}")
        List<Map<String, String>> rowMaps = []
        while (rowIt.hasNext()) {
            row = rowIt.next()
            Map<String, String> rowMap = createRowMap(row, headers)
            rowMaps << rowMap
        }
        return rowMaps
    }

    static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c)
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false
        }
        return true
    }

	static void getValue(Cell cell, List<String> data) {
		def colIndex = cell.getColumnIndex()
		data[colIndex] = valueHelper(cell)
	}

    static String valueHelper(Cell cell){
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString().trim();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
        }
        return ""
    }
    static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]

    /**
     * "Standard" refers to an old way of importing excel files...
     * This thing with headersMap is done in a particular way to generically handle a few excel formats
     * regardless of the order of the headers.. and handle legacy "Classifications/Models" instead of "Data Models/Data Classes"
     * in future we will prefer to use a list of headers which exactly matches the headers in the file
     * @param headersMap
     * @param workbook
     * @param catalogueBuilder
     * @param index
     */
    String buildModelFromStandardWorkbookSheet(Map<String, String> headersMap, Workbook workbook, CatalogueBuilder catalogueBuilder, int index=0) {

        buildModelFromWorkbookSheet(headersMap, workbook, index, catalogueBuilder)

    }


    /**
     * This function can take multiple catalogue builders i.e. XML or default
     * This thing with headersMap is done in a particular way to generically handle a few excel formats;
     * regardless of the order of the headers.. and handle legacy "Classifications/Models" instead of "Data Models/Data Classes";
     * in future we will prefer to use a list of headers which exactly matches the headers in the file;
     * The headersMap maps internal header names to actual header names used in the spreadsheet. There is a default setting.
     * @param headersMap
     * @param workbook
     * @param catalogueBuilder
     * @param index
     */
    String buildModelFromWorkbookSheet(Map<String, String> headersMap, Workbook workbook, int index = 0, CatalogueBuilder catalogueBuilder){

        log.info("Using headersMap ${headersMap as String}")
        // headersMap maps internal names of headers to what are hopefully the headers used in the actual spreadsheet.
        if(!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        Sheet sheet = workbook.getSheetAt(index);
        List<Map<String,String>> rowMaps = getRowMaps(sheet)

        catalogueBuilder.build {
            copy relationships
            rowMaps.each { Map<String, String> rowMap ->

                //take the LOINC class name and split to see if there is a hierarchy
                def loincClassNames = tryHeader(LoincHeadersMap.containingDataClassName, headersMap, rowMap) ?: tryHeader(HeadersMap.containingDataClassName, headersMap, rowMap)
                def parentClassName
                def className
                if(loincClassNames) {
                    String[] parts = loincClassNames.split("\\.")
                    if(parts.size() > 0){
                        className = parts[parts.size()-1]
                        parentClassName = parts[(parts.size() - 2)]
                    }else{
                        className = tryHeader(LoincHeadersMap.containingModelName, headersMap, rowMap) ?: tryHeader(HeadersMap.containingDataClassName, headersMap, rowMap)
                    }
                }

                dataModel(name: "LOINC") {
                    globalSearchFor dataType

                    def createChildModel = {
                        def createDataElement = {
                            if(tryHeader(LoincHeadersMap.dataElementName, headersMap, rowMap)) {
                                dataElement(name: tryHeader(LoincHeadersMap.dataElementName, headersMap, rowMap),
                                        description: tryHeader(LoincHeadersMap.dataElementDescription, headersMap, rowMap),
                                        id: tryHeader(LoincHeadersMap.dataElementCode, headersMap, rowMap)) {
                                    if (tryHeader(LoincHeadersMap.measurementUnitName, headersMap, rowMap) ||
                                            tryHeader(LoincHeadersMap.dataTypeName, headersMap, rowMap)) {
                                        importDataTypes(catalogueBuilder,
                                                tryHeader(LoincHeadersMap.dataElementName, headersMap, rowMap),
                                                tryHeader(LoincHeadersMap.dataTypeName, headersMap, rowMap),
                                                tryHeader(LoincHeadersMap.dataTypeCode, headersMap, rowMap),
                                                tryHeader(LoincHeadersMap.dataTypeClassification, headersMap, rowMap) ?: tryHeader(LoincHeadersMap.dataTypeDataModel, headersMap, rowMap),
                                                tryHeader(LoincHeadersMap.measurementUnitName, headersMap, rowMap),
                                                tryHeader(LoincHeadersMap.measurementSymbol, headersMap, rowMap))
                                    }


                                    if(tryHeader(LoincHeadersMap.PROPERTY, headersMap, rowMap)){
                                        ext('PROPERTY', tryHeader(LoincHeadersMap.PROPERTY, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.TIME_ASPCT, headersMap, rowMap)){
                                        ext('TIME_ASPCT', tryHeader(LoincHeadersMap.TIME_ASPCT, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.SYSTEM, headersMap, rowMap)){
                                        ext('SYSTEM', tryHeader(LoincHeadersMap.SYSTEM, headersMap, rowMap).take(2000).toString())
                                    }

                                    if(tryHeader(LoincHeadersMap.METHOD_TYP, headersMap, rowMap)){
                                        ext('METHOD_TYP', tryHeader(LoincHeadersMap.METHOD_TYP, headersMap, rowMap).take(2000).toString())
                                    }

                                    if(tryHeader(LoincHeadersMap.VersionLastChanged, headersMap, rowMap)){
                                        ext('VersionLastChanged', tryHeader(LoincHeadersMap.VersionLastChanged, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.CHNG_TYPE, headersMap, rowMap)){
                                        ext('CHNG_TYPE', tryHeader(LoincHeadersMap.CHNG_TYPE, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.DefinitionDescription, headersMap, rowMap)){
                                        ext('DefinitionDescription', tryHeader(LoincHeadersMap.DefinitionDescription, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.STATUS, headersMap, rowMap)){
                                        ext('STATUS', tryHeader(LoincHeadersMap.STATUS, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.CONSUMER_NAME, headersMap, rowMap)){
                                        ext('CONSUMER_NAME', tryHeader(LoincHeadersMap.CONSUMER_NAME, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.CLASSTYPE, headersMap, rowMap)){
                                        ext('CLASSTYPE', tryHeader(LoincHeadersMap.CLASSTYPE, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.FORMULA, headersMap, rowMap)){
                                        ext('FORMULA', tryHeader(LoincHeadersMap.FORMULA, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.SPECIES, headersMap, rowMap)){
                                        ext('SPECIES', tryHeader(LoincHeadersMap.SPECIES, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.EXMPL_ANSWERS, headersMap, rowMap)){
                                        ext('EXMPL_ANSWERS', tryHeader(LoincHeadersMap.EXMPL_ANSWERS, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.SURVEY_QUEST_TEXT, headersMap, rowMap)){
                                        ext('SURVEY_QUEST_TEXT', tryHeader(LoincHeadersMap.SURVEY_QUEST_TEXT, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.SURVEY_QUEST_SRC, headersMap, rowMap)){
                                        ext('SURVEY_QUEST_SRC', tryHeader(LoincHeadersMap.SURVEY_QUEST_SRC, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.UNITSREQUIRED, headersMap, rowMap)){
                                        ext('UNITSREQUIRED', tryHeader(LoincHeadersMap.UNITSREQUIRED, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.SUBMITTED_UNITS, headersMap, rowMap)){
                                        ext('SUBMITTED_UNITS', tryHeader(LoincHeadersMap.SUBMITTED_UNITS, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.RELATEDNAMES2, headersMap, rowMap)){
                                        ext('RELATEDNAMES2', tryHeader(LoincHeadersMap.RELATEDNAMES2, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.SHORTNAME, headersMap, rowMap)){
                                        ext('SHORTNAME', tryHeader(LoincHeadersMap.SHORTNAME, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.ORDER_OBS, headersMap, rowMap)){
                                        ext('ORDER_OBS', tryHeader(LoincHeadersMap.ORDER_OBS, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.CDISC_COMMON_TESTS, headersMap, rowMap)){
                                        ext('CDISC_COMMON_TESTS', tryHeader(LoincHeadersMap.CDISC_COMMON_TESTS, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.HL7_FIELD_SUBFIELD_ID, headersMap, rowMap)){
                                        ext('HL7_FIELD_SUBFIELD_ID', tryHeader(LoincHeadersMap.HL7_FIELD_SUBFIELD_ID, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.EXTERNAL_COPYRIGHT_NOTICE, headersMap, rowMap)){
                                        ext('EXTERNAL_COPYRIGHT_NOTICE', tryHeader(LoincHeadersMap.EXTERNAL_COPYRIGHT_NOTICE, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.EXAMPLE_UNITS, headersMap, rowMap)){
                                        ext('EXAMPLE_UNITS', tryHeader(LoincHeadersMap.EXAMPLE_UNITS, headersMap, rowMap).take(2000).toString())
                                    }

                                    if(tryHeader(LoincHeadersMap.UnitsAndRange, headersMap, rowMap)){
                                        ext('UnitsAndRange', tryHeader(LoincHeadersMap.UnitsAndRange, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.DOCUMENT_SECTION, headersMap, rowMap)) {
                                        ext('DOCUMENT_SECTION', tryHeader(LoincHeadersMap.DOCUMENT_SECTION, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.EXAMPLE_SI_UCUM_UNITS, headersMap, rowMap)){
                                        ext('EXAMPLE_SI_UCUM_UNITS', tryHeader(LoincHeadersMap.EXAMPLE_SI_UCUM_UNITS, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.STATUS_REASON, headersMap, rowMap)){
                                        ext('STATUS_REASON', tryHeader(LoincHeadersMap.STATUS_REASON, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.STATUS_TEXT, headersMap, rowMap)){
                                        ext('STATUS_TEXT', tryHeader(LoincHeadersMap.STATUS_TEXT, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.CHANGE_REASON_PUBLIC, headersMap, rowMap)){
                                        ext('CHANGE_REASON_PUBLIC', tryHeader(LoincHeadersMap.CHANGE_REASON_PUBLIC, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.COMMON_TEST_RANK, headersMap, rowMap)){
                                        ext('COMMON_TEST_RANK', tryHeader(LoincHeadersMap.COMMON_TEST_RANK, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.COMMON_ORDER_RANK, headersMap, rowMap)){
                                        ext('COMMON_ORDER_RANK', tryHeader(LoincHeadersMap.COMMON_ORDER_RANK, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.COMMON_SI_TEST_RANK, headersMap, rowMap)){
                                        ext('COMMON_SI_TEST_RANK', tryHeader(LoincHeadersMap.COMMON_SI_TEST_RANK, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.HL7_ATTACHMENT_STRUCTURE, headersMap, rowMap)){
                                        ext('HL7_ATTACHMENT_STRUCTURE', tryHeader(LoincHeadersMap.HL7_ATTACHMENT_STRUCTURE, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.EXTERNAL_COPYRIGHT_LINK, headersMap, rowMap)){
                                        ext('EXTERNAL_COPYRIGHT_LINK', tryHeader(LoincHeadersMap.EXTERNAL_COPYRIGHT_LINK, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.PanelType, headersMap, rowMap)){
                                        ext('PanelType', tryHeader(LoincHeadersMap.PanelType, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.AskAtOrderEntry, headersMap, rowMap)){
                                        ext('AskAtOrderEntry', tryHeader(LoincHeadersMap.AskAtOrderEntry, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.AssociatedObservations, headersMap, rowMap)){
                                        ext('AssociatedObservations', tryHeader(LoincHeadersMap.AssociatedObservations, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.VersionFirstReleased, headersMap, rowMap)){
                                        ext('VersionFirstReleased', tryHeader(LoincHeadersMap.VersionFirstReleased, headersMap, rowMap).take(2000).toString())
                                    }
                                    if(tryHeader(LoincHeadersMap.ValidHL7AttachmentRequest, headersMap, rowMap)){
                                        ext('ValidHL7AttachmentRequest', tryHeader(LoincHeadersMap.ValidHL7AttachmentRequest, headersMap, rowMap).take(2000).toString())
                                    }

                                }
                            }
                        }


                        if (className) {
                            dataClass(name: className, createDataElement)
                        } else {
                            catalogueBuilder.with createDataElement
                        }
                    }

                    if (parentClassName) {
                        dataClass(name: parentClassName, createChildModel)
                    } else {
                        catalogueBuilder.with createChildModel
                    }

                }
            }
        }

    }


    String outOfBoundsWith(Closure<String> c, String s = '') {
        try {
            return c()
        }
        catch (ArrayIndexOutOfBoundsException a) {
            return s
        }
    }
    String tryHeader(String internalHeaderName, Map<String,String> headersMap, Map<String, String> rowMap) {
        // headersMap maps internal names of headers to what are hopefully the headers used in the actual spreadsheet.
        String entry = rowMap.get(headersMap.get(internalHeaderName))
        if (entry) return entry
        else {
            /*log.info("Trying to use internalHeaderName '$internalHeaderName', which headersMap corresponds to " +
                "header ${headersMap.get(internalHeaderName)}, from rowMap ${rowMap as String}, nothing found.")*/
            return null}
    }

    static String getRowValue(List<String> rowDataList, index){
        // was used in old way of using headersMap in buildModelFromWorkbookSheet
        (index!=-1)?rowDataList[index]:null
    }

    static void importDataTypesFromExcelExporterFormat(CatalogueBuilder catalogueBuilder,
            String dataTypeName, String dataTypeCode, String dataTypeEnumerations, String dataTypeRule, String measurementUnitName, String measurementUnitCode){
        String[] lines = dataTypeEnumerations?.split("\\r?\\n");
        Map<String,String> enumerations = dataTypeEnumerations ? parseMapStringLines(lines) : [:]
        if (enumerations) {
            catalogueBuilder.dataType(name: dataTypeName, enumerations: enumerations, rule: dataTypeRule, id: dataTypeCode) //TODO: get dataModel, which used to be dataTypeClassification, from data type code.
        }

        else if (measurementUnitName) {
            catalogueBuilder.dataType(name: dataTypeName, rule: dataTypeRule, id: dataTypeCode) {
                measurementUnit(name: measurementUnitName, id: measurementUnitCode)
            }
        }
        else {
            catalogueBuilder.dataType(name: dataTypeName, rule: dataTypeRule, id: dataTypeCode)
        }
    }

    /**
     *
     * @param dataElementName data element/item name
     * @param dataTypeNameOrEnum - Column F - content of - either blank or an enumeration or a named datatype.
     * @return
     */
    static importDataTypes(CatalogueBuilder catalogueBuilder, String dataElementName, dataTypeNameOrEnum, String dataTypeCode, String dataTypeClassification, String measurementUnitName, String measurementUnitSymbol) {
        if (!dataTypeNameOrEnum) {
            if (measurementUnitName) {
                return catalogueBuilder.dataType(id: dataTypeCode, dataModel: dataTypeClassification, name: 'String') {
                    measurementUnit name: measurementUnitName, symbol: measurementUnitSymbol
                }
            }
            return catalogueBuilder.dataType(id: dataTypeCode, dataModel: dataTypeClassification, name: 'String')
        }
        //default data type to return is the string data type
        String[] lines = dataTypeNameOrEnum.split("\\r?\\n");
        if (!(lines.size() > 0 && lines != null)) {
            if (measurementUnitName) {
                return catalogueBuilder.dataType(name: "String", dataModel: dataTypeClassification, id: dataTypeCode) {
                    measurementUnit name: measurementUnitName, symbol: measurementUnitSymbol
                }
            }
            return catalogueBuilder.dataType(name: "String", dataModel: dataTypeClassification, id: dataTypeCode)
        }

        def enumerations = lines.size() == 1 ? [:] : parseMapStringLines(lines)

        if(!enumerations){
            if (measurementUnitName) {
                return catalogueBuilder.dataType(name: dataTypeNameOrEnum, dataModel: dataTypeClassification, id: dataTypeCode) {
                    measurementUnit name: measurementUnitName, symbol: measurementUnitSymbol
                }
            }
            return catalogueBuilder.dataType(name: dataTypeNameOrEnum, dataModel: dataTypeClassification, id: dataTypeCode)
        }

        return catalogueBuilder.dataType(name: dataElementName, enumerations: enumerations, dataModel: dataTypeClassification, id: dataTypeCode)
    }

    static Map<String,String> parseMapString(String mapString) {
        return parseMapStringLines(mapString.split(/\r?\n/))
    }

    /**
    For colon-separated key-value lines which are usually enumerations or metadata.
     */
    static Map<String,String> parseMapStringLines(String[] lines){
        Map map = new HashMap()

        lines.each { enumeratedValues ->

            String[] EV = enumeratedValues.split(":")

            if (EV?.size() == 2) {
                String key = EV[0]
                String value = EV[1]

                if (value.size() > 244) {
                    value = value[0..244]
                }

                key = key.trim()
                value = value.trim()


                map.put(key, value)
            }
        }
        return map
    }
}
