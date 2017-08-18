package org.modelcatalogue.core.dataimport.excel

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
class ExcelLoader {

    static String getOwnerFromFileName(String sampleFile, String bitInBetween) {
        sampleFile.find(/(.*)$bitInBetween.*/){ match, firstcapture ->
            firstcapture
        }.toUpperCase()
    }
    static protected Map<String, String> createRowMap(Row row, List<String> headers) {
        Map<String, String> rowMap = [:]
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

        List<Map<String, String>> rowMaps = []
        while (rowIt.hasNext()) {
            row = rowIt.next()
            Map<String, String> rowMap = createRowMap(row, headers)


            rowMaps << rowMap
        }
        return rowMaps
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
	private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
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
		if(!workbook) {
			throw new IllegalArgumentException("Excel file contains no worksheet!")
		}
		Sheet sheet = workbook.getSheetAt(index);

		Iterator<Row> rowIt = sheet.rowIterator()
		List<String> headers = getRowData(rowIt.next())

		List<List<String>> rowDataLists = []
		while(rowIt.hasNext()) {
			List<String> rowDataList =getRowData(rowIt.next())

			/*boolean canBeInserted = rowDataList.inject(true) {acc, entry ->
                acc && (entry != null) && (entry != '')
            }
			if(canBeInserted)*/
            rowDataLists << rowDataList
		}
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
		}
        return stringWriter.toString()
	}

	static String getRowValue(List<String> rowDataList, index){
		(index!=-1)?rowDataList[index]:null
	}


	/**
	 *
	 * @param dataElementName data element/item name
	 * @param dataTypeNameOrEnum - Column F - content of - either blank or an enumeration or a named datatype.
	 * @return
	 */
	static importDataTypes(CatalogueBuilder catalogueBuilder, dataElementName, dataTypeNameOrEnum, dataTypeCode, dataTypeClassification, measurementUnitName, measurementUnitSymbol) {
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

		def enumerations = lines.size() == 1 ? [:] : parseEnumeration(lines)

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

	static Map<String,String> parseEnumeration(String[] lines){
		Map enumerations = new HashMap()

		lines.each { enumeratedValues ->

			def EV = enumeratedValues.split(":")

			if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
				def key = EV[0]
				def value = EV[1]

				if (value.size() > 244) {
					value = value[0..244]
				}

				key = key.trim()
				value = value.trim()


				enumerations.put(key, value)
			}
		}
		return enumerations
	}
}
