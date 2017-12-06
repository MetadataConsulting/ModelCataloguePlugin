package org.modelcatalogue.core.dataimport.excel

import groovy.util.logging.Log
import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.Row
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.DataModel

/**
 * This used to be a class for one purpose ("importData", now called "buildXmlFromStandardWorkbookSheet"), but now we have made it a parent class of
 * future NT Excel Loaders, so that they can access similar methods.
 * This may not be the best way
 */
@Log
class ExcelLoader {

    def dataModelService, elementService

    static String getOwnerAndGelModelFromFileName(String sampleFile, String bitInBetween) {
        sampleFile.find(/(.*)$bitInBetween.*/){ match, firstcapture ->
            firstcapture
        }.toUpperCase()
    }
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
     * In the future other ExcelLoaders which inherit from this one will override this method and not the "standard" one.
     * @param headers
     * @param workbook
     * @param catalogueBuilder
     * @param index
     */
    Pair<String, List<String>> buildXmlFromWorkbookSheet(Workbook workbook, int index=0, String owner='') {}

    /**
     * Add relationships from sourceDataModel to models with destinationModelNames
     * via some metadata in the destination model elements that indicates which source element to relate
     * @param sourceDataModel
     * @param destinationModelNames
     */
    void addRelationshipsToModels(DataModel sourceDataModel, List<String> destinationModelNames) {}
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
    String buildXmlFromStandardWorkbookSheet(Map<String, String> headersMap, Workbook workbook, int index=0) {

        Writer stringWriter = new StringWriter()
        CatalogueBuilder catalogueBuilder = new XmlCatalogueBuilder(stringWriter, true)

        buildModelFromWorkbookSheet(headersMap, workbook, index, catalogueBuilder)

        return stringWriter.toString()
    }


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
                dataModel(name: tryHeader(HeadersMap.classification, headersMap, rowMap) ?: tryHeader(HeadersMap.dataModel, headersMap, rowMap)) {
                    globalSearchFor dataType

                    def createChildModel = {
                        def createDataElement = {
                            if(tryHeader(HeadersMap.dataElementName, headersMap, rowMap)) {
                                dataElement(name: tryHeader(HeadersMap.dataElementName, headersMap, rowMap),
                                        description: tryHeader(HeadersMap.dataElementDescription, headersMap, rowMap),
                                        id: tryHeader(HeadersMap.dataElementCode, headersMap, rowMap)) {
                                    if (tryHeader(HeadersMap.measurementUnitName, headersMap, rowMap) ||
                                            tryHeader(HeadersMap.dataTypeName, headersMap, rowMap)) {
                                        importDataTypes(catalogueBuilder,
                                                tryHeader(HeadersMap.dataElementName, headersMap, rowMap),
                                                tryHeader(HeadersMap.dataTypeName, headersMap, rowMap),
                                                tryHeader(HeadersMap.dataTypeCode, headersMap, rowMap),
                                                tryHeader(HeadersMap.dataTypeClassification, headersMap, rowMap) ?: tryHeader(HeadersMap.dataTypeDataModel, headersMap, rowMap),
                                                tryHeader(HeadersMap.measurementUnitName, headersMap, rowMap),
                                                tryHeader(HeadersMap.measurementSymbol, headersMap, rowMap))
                                    }
                                    List<String> metadataKeys = rowMap.keySet().toList().dropWhile({header ->
                                        header != headersMap.get(HeadersMap.metadata)
                                    })
                                    for (String key: metadataKeys) {
                                        if (key != "" && key != "null") {
                                            ext(key, rowMap.get(key).take(2000).toString())
                                        }
                                    }
                                }
                            }
                        }


                        def modelName = tryHeader(HeadersMap.containingModelName, headersMap, rowMap) ?: tryHeader(HeadersMap.containingDataClassName, headersMap, rowMap)
                        def modelId = tryHeader(HeadersMap.containingModelCode, headersMap, rowMap) ?: tryHeader(HeadersMap.containingDataClassCode, headersMap, rowMap)

                        if (modelName || modelId) {
                            dataClass(name: modelName, id: modelId, createDataElement)
                        } else {
                            catalogueBuilder.with createDataElement
                        }
                    }

                    def parentModelName = tryHeader(HeadersMap.parentModelName, headersMap, rowMap) ?: tryHeader(HeadersMap.parentDataClassName, headersMap, rowMap)
                    def parentModelCode = tryHeader(HeadersMap.parentModelCode, headersMap, rowMap) ?: tryHeader(HeadersMap.parentDataClassCode, headersMap, rowMap)

                    if (parentModelName || parentModelCode) {
                        dataClass(name: parentModelName, id: parentModelCode, createChildModel)
                    } else {
                        catalogueBuilder.with createChildModel
                    }

                }
            }
        }
        // old way of using headerMaps:
        Closure oldWay = {
            /*
        Iterator<Row> rowIt = sheet.rowIterator()
        List<String> headers = getRowData(rowIt.next())

        List<List<String>> rowDataLists = []
        while(rowIt.hasNext()) {
            List<String> rowDataList =getRowData(rowIt.next())
            rowDataLists << rowDataList
        }*/

/*
            //get indexes of the appropriate sections
        def dataItemNameIndex = headers.indexOf(headersMap.get('dataElementName'))
        def dataItemCodeIndex = headers.indexOf(headersMap.get('dataElementCode'))
        def dataItemDescriptionIndex = headers.indexOf(headersMap.get('dataElementDescription'))
        def parentModelIndex = Math.max(headers.indexOf(headersMap.get('parentModelName')), headers.indexOf(headersMap.get('parentDataClassName')))
        def modelIndex = Math.max(headers.indexOf(headersMap.get('containingModelName')), headers.indexOf(headersMap.get('containingDataClassName')))
        def parentModelCodeIndex = Math.max(headers.indexOf(headersMap.get('parentModelCode')), headers.indexOf(headersMap.get('parentDataClassCode')))
        def modelCodeIndex = Math.max(headers.indexOf(headersMap.get('containingModelCode')), headers.indexOf(headersMap.get('containingDataClassCode')))
        def unitsIndex = headers.indexOf(headersMap.get('measurementUnitName'))
        def symbolsIndex = headers.indexOf(headersMap.get('measurementSymbol'))
        def classificationsIndex = Math.max(headers.indexOf(headersMap.get('classification')), headers.indexOf(headersMap.get('dataModel')))
        def dataTypeNameIndex = headers.indexOf(headersMap.get('dataTypeName'))
        def dataTypeClassificationIndex = Math.max(headers.indexOf(headersMap.get('dataTypeClassification')), headers.indexOf(headersMap.get('dataTypeDataModel')))
        def dataTypeCodeIndex = headers.indexOf(headersMap.get('dataTypeCode'))
        def valueDomainNameIndex = headers.indexOf(headersMap.get('valueDomainName'))
        def valueDomainClassificationIndex = Math.max(headers.indexOf(headersMap.get('valueDomainClassification')), headers.indexOf(headersMap.get('valueDomainDataModel')))
        def valueDomainCodeIndex = headers.indexOf(headersMap.get('valueDomainCode'))
        def metadataStartIndex = headers.indexOf(headersMap.get('metadata')) + 1
        def metadataEndIndex = headers.size() - 1

        if (dataItemNameIndex == -1) throw new Exception("Can not find '${headersMap.get('dataElementName')}' column")
        //iterate through the rows and import each line
        catalogueBuilder.build {
            copy relationships
            rowDataLists.eachWithIndex { List<String> rowDataList, int i ->
                dataModel(name: getRowValue(rowDataList,classificationsIndex)) {
                    globalSearchFor dataType

                    def createChildModel = {
                        def createDataElement = {
                            if(getRowValue(rowDataList,dataItemNameIndex)) {
                                dataElement(name: getRowValue(rowDataList, dataItemNameIndex), description: getRowValue(rowDataList, dataItemDescriptionIndex), id: getRowValue(rowDataList, dataItemCodeIndex)) {
                                    if (getRowValue(rowDataList, unitsIndex) || getRowValue(rowDataList, dataTypeNameIndex)) {
                                        if (getRowValue(rowDataList, dataTypeNameIndex) || getRowValue(rowDataList, unitsIndex))
                                            importDataTypes(catalogueBuilder, getRowValue(rowDataList, dataItemNameIndex), getRowValue(rowDataList, dataTypeNameIndex), getRowValue(rowDataList, dataTypeCodeIndex), getRowValue(rowDataList, dataTypeClassificationIndex), getRowValue(rowDataList, unitsIndex), getRowValue(rowDataList, symbolsIndex))
                                    }

                                    int counter = metadataStartIndex
                                    while (counter <= metadataEndIndex) {
                                        String key = headers[counter].toString()
                                        String value = (rowDataList[counter] != null) ? rowDataList[counter].toString() : ""
                                        if (key != "" && key != "null") {
                                            ext(key, value?.take(2000)?.toString() ?: '')
                                        }
                                        counter++
                                    }
                                }
                            }
                        }


                        def modelName = getRowValue(rowDataList, modelIndex)
                        def modelId = getRowValue(rowDataList, modelCodeIndex)

                        if (modelName || modelId) {
                            dataClass(name: modelName, id: modelId, createDataElement)
                        } else {
                            catalogueBuilder.with createDataElement
                        }
                    }

                    def parentModelName = getRowValue(rowDataList, parentModelIndex)
                    def parentModelCode = getRowValue(rowDataList, parentModelCodeIndex)
                    if (parentModelName || parentModelCode) {
                        dataClass(name: parentModelName, id: parentModelCode, createChildModel)
                    } else {
                        catalogueBuilder.with createChildModel
                    }

                }
            }
        }*/
        }
    }


    String buildXmlFromSpreadsheetFromExcelExporter(Map<String, String> headersMap = HeadersMap.createForSpreadsheetFromExcelExporter(), Workbook workbook, int index=0, String modelName = '') {

        Writer stringWriter = new StringWriter()
        CatalogueBuilder catalogueBuilder = new XmlCatalogueBuilder(stringWriter, true)

        buildModelFromSpreadsheetFromExcelExporter(headersMap, workbook, index, catalogueBuilder, modelName)

        return stringWriter.toString()
    }
    // Based on buildModelFromWorkbookSheet
    String buildModelFromSpreadsheetFromExcelExporter(Map<String, String> headersMap = HeadersMap.createForSpreadsheetFromExcelExporter(), Workbook workbook, int index = 0, CatalogueBuilder catalogueBuilder, String modelName = ''){

        log.info("Using headersMap ${headersMap as String}")
        // headersMap maps internal names of headers to what are hopefully the headers used in the actual spreadsheet.
        if(!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        if (modelName == '') {modelName = workbook.getSheetName(index)}
        Sheet sheet = workbook.getSheetAt(index)

        List<Map<String,String>> rowMaps = getRowMaps(sheet)


        // for the spreadsheet exported by ExcelExporter
        catalogueBuilder.build {
            copy relationships
            // data model name provided rather than gotten from sheet
            dataModel(name: modelName) {
                rowMaps.each { Map<String, String> rowMap ->
                    globalSearchFor dataType

                    // TODO: IDs might need some processing to be usable? Like stripping off the first bit?
                    Closure createChildDataClass = {
                        def createDataElement = {
                            if(tryHeader(HeadersMap.dataElementName, headersMap, rowMap)) {
                                dataElement(name: tryHeader(HeadersMap.dataElementName, headersMap, rowMap),
                                        description: tryHeader(HeadersMap.dataElementDescription, headersMap, rowMap),
                                        id: tryHeader(HeadersMap.dataElementCode, headersMap, rowMap)) {
                                    // Relationship metadata: Multiplicity
                                    String[] multiplicity = tryHeader(HeadersMap.multiplicity, headersMap, rowMap).split(/\.\./)
                                    relationship {
                                        ext "Min Occurs", outOfBoundsWith({multiplicity[0]})
                                        ext "Max Occurs", outOfBoundsWith({multiplicity[1]})
                                    }
                                    // Data Type
                                    if (tryHeader(HeadersMap.measurementUnitName, headersMap, rowMap) ||
                                            tryHeader(HeadersMap.dataTypeName, headersMap, rowMap)) {
                                        importDataTypesFromExcelExporterFormat(
                                                catalogueBuilder,
                                                tryHeader(HeadersMap.dataTypeName, headersMap, rowMap),
                                                tryHeader(HeadersMap.dataTypeCode, headersMap, rowMap),
                                                tryHeader(HeadersMap.dataTypeEnumerations, headersMap, rowMap),
                                                tryHeader(HeadersMap.dataTypeRule, headersMap, rowMap),
                                                tryHeader(HeadersMap.measurementUnitName, headersMap, rowMap),
                                                tryHeader(HeadersMap.measurementUnitCode, headersMap, rowMap)
                                        )
                                    }/*
                                    List<String> metadataKeys = rowMap.keySet().toList().dropWhile({header ->
                                        header != headersMap.get(HeadersMap.metadata)
                                    })
                                    for (String key: metadataKeys) {
                                        if (key != "" && key != "null") {
                                            //ext(key, rowMap.get(key).take(2000).toString())
                                        }
                                    }*/
                                    // metadata:
                                    Map<String,String> metadata = parseMapString(tryHeader(HeadersMap.metadata, headersMap, rowMap) ?: '')
                                    metadata.each {k,v ->
                                        ext(k,v)
                                    }
                                }
                            }
                        }


                        def containingDataClassName = tryHeader(HeadersMap.containingModelName, headersMap, rowMap) ?: tryHeader(HeadersMap.containingDataClassName, headersMap, rowMap)
                        def containingDataClassID = tryHeader(HeadersMap.containingModelCode, headersMap, rowMap) ?: tryHeader(HeadersMap.containingDataClassCode, headersMap, rowMap)

                        if (containingDataClassName || containingDataClassID) {
                            dataClass(name: containingDataClassName, id: containingDataClassID, createDataElement)
                        } else {
                            catalogueBuilder.with createDataElement
                        }
                    }

                    def parentDataClassName = tryHeader(HeadersMap.parentModelName, headersMap, rowMap) ?: tryHeader(HeadersMap.parentDataClassName, headersMap, rowMap)
                    def parentDataClassID = tryHeader(HeadersMap.parentModelCode, headersMap, rowMap) ?: tryHeader(HeadersMap.parentDataClassCode, headersMap, rowMap)

                    if (parentDataClassName || parentDataClassID) {
                        dataClass(name: parentDataClassName, id: parentDataClassID, createChildDataClass)
                    } else {
                        catalogueBuilder.with createChildDataClass
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
