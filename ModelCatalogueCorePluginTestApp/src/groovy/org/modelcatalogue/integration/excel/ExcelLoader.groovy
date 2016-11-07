package org.modelcatalogue.integration.excel

import org.apache.poi.ss.usermodel.*
import org.modelcatalogue.builder.api.CatalogueBuilder

class ExcelLoader {

    final CatalogueBuilder builder

    public ExcelLoader(CatalogueBuilder builder) {
        this.builder = builder
    }


	def static getRowData(Row row) {
		def data = []
		for (Cell cell : row) {
			getValue(cell, data)
		}
		data
	}

	static getValue(Cell cell, List data) {
		def colIndex = cell.getColumnIndex()
		data[colIndex] = valueHelper(cell)
		data
	}

    static valueHelper(Cell cell){
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

	void importData(HeadersMap headersMap, InputStream stream) {
		Workbook wb = WorkbookFactory.create(stream);
		if(!wb) {
			throw new IllegalArgumentException("Excel file contains no worksheet!")
		}
		Sheet sheet = wb.getSheetAt(0);

		Iterator<Row> rowIt = sheet.rowIterator()
		Row row = rowIt.next()
		def headers = getRowData(row)

		def rows = []
		while(rowIt.hasNext()) {
			row = rowIt.next()
			def data =getRowData(row)

			def canBeInserted = false;
			data.eachWithIndex { def entry, int i ->
				if(entry!=null && entry!="")
					canBeInserted= true;
			}
			if(canBeInserted)
				rows << data
		}

		//get indexes of the appropriate sections
		def dataItemNameIndex = headers.indexOf(headersMap.dataElementName)
		def dataItemCodeIndex = headers.indexOf(headersMap.dataElementCode)
		def dataItemDescriptionIndex = headers.indexOf(headersMap.dataElementDescription)
		def parentModelIndex = Math.max(headers.indexOf(headersMap.parentModelName), headers.indexOf(headersMap.parentDataClassName))
		def modelIndex = Math.max(headers.indexOf(headersMap.containingModelName), headers.indexOf(headersMap.containingDataClassName))
		def parentModelCodeIndex = Math.max(headers.indexOf(headersMap.parentModelCode), headers.indexOf(headersMap.parentDataClassCode))
		def modelCodeIndex = Math.max(headers.indexOf(headersMap.containingModelCode), headers.indexOf(headersMap.containingDataClassCode))
		def unitsIndex = headers.indexOf(headersMap.measurementUnitName)
		def symbolsIndex = headers.indexOf(headersMap.measurementSymbol)
		def classificationsIndex = Math.max(headers.indexOf(headersMap.classification), headers.indexOf(headersMap.dataModel))
		def dataTypeNameIndex = headers.indexOf(headersMap.dataTypeName)
		def dataTypeClassificationIndex = Math.max(headers.indexOf(headersMap.dataTypeClassification), headers.indexOf(headersMap.dataTypeDataModel))
		def dataTypeCodeIndex = headers.indexOf(headersMap.dataTypeCode)
		def valueDomainNameIndex = headers.indexOf(headersMap.valueDomainName)
		def valueDomainClassificationIndex = Math.max(headers.indexOf(headersMap.valueDomainClassification), headers.indexOf(headersMap.valueDomainDataModel))
		def valueDomainCodeIndex = headers.indexOf(headersMap.valueDomainCode)
		def metadataStartIndex = headers.indexOf(headersMap.metadata) + 1
		def metadataEndIndex = headers.size() - 1

		if (dataItemNameIndex == -1) throw new Exception("Can not find '${headersMap.dataElementName}' column")
		//iterate through the rows and import each line
		builder.build {
			copy relationships
			rows.eachWithIndex { def aRow, int i ->
				dataModel(name: getRowValue(aRow,classificationsIndex)) {
					globalSearchFor dataType

					def createChildModel = {
						def createDataElement = {
							if(getRowValue(aRow,dataItemNameIndex)) {
								dataElement(name: getRowValue(aRow, dataItemNameIndex), description: getRowValue(aRow, dataItemDescriptionIndex), id: getRowValue(aRow, dataItemCodeIndex)) {
									if (getRowValue(aRow, unitsIndex) || getRowValue(aRow, dataTypeNameIndex)) {
										if (getRowValue(aRow, dataTypeNameIndex) || getRowValue(aRow, unitsIndex))
											importDataTypes(builder, getRowValue(aRow, dataItemNameIndex), getRowValue(aRow, dataTypeNameIndex), getRowValue(aRow, dataTypeCodeIndex), getRowValue(aRow, dataTypeClassificationIndex), getRowValue(aRow, unitsIndex), getRowValue(aRow, symbolsIndex))
									}

									int counter = metadataStartIndex
									while (counter <= metadataEndIndex) {
										String key = headers[counter].toString()
										String value = (aRow[counter] != null) ? aRow[counter].toString() : ""
										if (key != "" && key != "null") {
											ext(key, value?.take(2000)?.toString() ?: '')
										}
										counter++
									}
								}
							}
						}


						def modelName = getRowValue(aRow, modelIndex)
						def modelId = getRowValue(aRow, modelCodeIndex)

						if (modelName || modelId) {
							dataClass(name: modelName, id: modelId, createDataElement)
						} else {
							builder.with createDataElement
						}
					}

					def parentModelName = getRowValue(aRow, parentModelIndex)
					def parentModelCode = getRowValue(aRow, parentModelCodeIndex)
					if (parentModelName || parentModelCode) {
						dataClass(name: parentModelName, id: parentModelCode, createChildModel)
					} else {
						builder.with createChildModel
					}

				}
			}
		}
	}

	def static getRowValue(row, index){
		(index!=-1)?row[index]:null
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