package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.util.builder.CatalogueBuilder

class DataImportService {
    static transactional = false
    def elementService
    def relationshipService
    def classificationService

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]

    @Transactional
    Collection<CatalogueElement> importData(ArrayList headers, ArrayList rows, HeadersMap headersMap) {
        //get indexes of the appropriate sections
        def dataItemNameIndex = headers.indexOf(headersMap.dataElementName)
        def dataItemCodeIndex = headers.indexOf(headersMap.dataElementCode)
        def dataItemDescriptionIndex = headers.indexOf(headersMap.dataElementDescription)
        def parentModelIndex = headers.indexOf(headersMap.parentModelName)
        def modelIndex = headers.indexOf(headersMap.containingModelName)
        def parentModelCodeIndex = headers.indexOf(headersMap.parentModelCode)
        def modelCodeIndex = headers.indexOf(headersMap.containingModelCode)
        def unitsIndex = headers.indexOf(headersMap.measurementUnitName)
        def symbolsIndex = headers.indexOf(headersMap.measurementSymbol)
        def classificationsIndex = headers.indexOf(headersMap.classification)
        def dataTypeNameIndex = headers.indexOf(headersMap.dataTypeName)
        def dataTypeClassificationIndex = headers.indexOf(headersMap.dataTypeClassification)
        def dataTypeCodeIndex = headers.indexOf(headersMap.dataTypeCode)
        def valueDomainNameIndex = headers.indexOf(headersMap.valueDomainName)
        def valueDomainClassificationIndex = headers.indexOf(headersMap.valueDomainClassification)
        def valueDomainCodeIndex = headers.indexOf(headersMap.valueDomainCode)
        def metadataStartIndex = headers.indexOf(headersMap.metadata) + 1
        def metadataEndIndex = headers.size() - 1

        if (dataItemNameIndex == -1) throw new Exception("Can not find '${headersMap.dataElementName}' column")
        //iterate through the rows and import each line
        CatalogueBuilder builder = new CatalogueBuilder(classificationService, elementService)
        builder.build {
            copy relationships
            rows.eachWithIndex { def row, int i ->
                classification(name: getRowValue(row,classificationsIndex)) {
                    globalSearchFor dataType

                    def createChildModel = {
                        def createDataElement = {
                            if(getRowValue(row,dataItemNameIndex)) {
                                dataElement(name: getRowValue(row, dataItemNameIndex), description: getRowValue(row, dataItemDescriptionIndex), id: getRowValue(row, dataItemCodeIndex)) {
                                    if (getRowValue(row, unitsIndex) || getRowValue(row, dataTypeNameIndex)) {
                                        def createDataTypeAndMeasurementUnits = {
                                            if (getRowValue(row, dataTypeNameIndex))
                                                importDataTypes(builder, getRowValue(row, dataItemNameIndex), getRowValue(row, dataTypeNameIndex), getRowValue(row, dataTypeCodeIndex), getRowValue(row, dataTypeClassificationIndex))
                                            if (getRowValue(row, unitsIndex))
                                                measurementUnit(name: getRowValue(row, unitsIndex), symbol: getRowValue(row, symbolsIndex))
                                        }
                                        def valueDomainName = getRowValue(row, valueDomainNameIndex)
                                        def valueDomainCode = getRowValue(row, valueDomainCodeIndex)
                                        def valueDomainClassification = getRowValue(row, valueDomainClassificationIndex)

                                        if (!(valueDomainNameIndex || valueDomainCode || valueDomainClassification)) {
                                            valueDomain(name: getRowValue(row, dataItemNameIndex), classification: getRowValue(row, dataTypeClassificationIndex), createDataTypeAndMeasurementUnits)
                                        } else {
                                            valueDomain(name: valueDomainName, id: valueDomainCode, classification: valueDomainClassification, createDataTypeAndMeasurementUnits)
                                        }
                                    }

                                    int counter = metadataStartIndex
                                    while (counter <= metadataEndIndex) {
                                        String key = headers[counter].toString()
                                        String value = (row[counter] != null) ? row[counter].toString() : ""
                                        if (key != "" && key != "null") {
                                            ext(key, value?.take(2000)?.toString() ?: '')
                                        }
                                        counter++
                                    }
                                }
                            }
                        }


                        def modelName = getRowValue(row, modelIndex)
                        def modelId = getRowValue(row, modelCodeIndex)

                        if (modelName || modelId) {
                            model(name: modelName, id: modelId, createDataElement)
                        } else {
                            builder.with createDataElement
                        }
                    }

                    def parentModelName = getRowValue(row, parentModelIndex)
                    def parentModelCode = getRowValue(row, parentModelCodeIndex)
                    if (parentModelName || parentModelCode) {
                        model(name: parentModelName, id: parentModelCode, createChildModel)
                    } else {
                        builder.with createChildModel
                    }

                }
            }
        }
    }

    def getRowValue(row, index){
        (index!=-1)?row[index]:null
    }


    /**
     *
     * @param dataElementName data element/item name
     * @param dataTypeNameOrEnum - Column F - content of - either blank or an enumeration or a named datatype.
     * @return
     */
     static importDataTypes(CatalogueBuilder catalogueBuilder, dataElementName, dataTypeNameOrEnum, dataTypeCode, dataTypeClassification) {
         if (!dataTypeNameOrEnum) {
             return catalogueBuilder.dataType(id: dataTypeCode, classification: dataTypeClassification, name: 'String')
         }
        //default data type to return is the string data type
        String[] lines = dataTypeNameOrEnum.split("\\r?\\n");
        if (!(lines.size() > 0 && lines != null)) {
            return catalogueBuilder.dataType(name: "String", classification: dataTypeClassification, id: dataTypeCode)
        }

        def enumerations = parseEnumeration(lines)

        if(!enumerations){
            return catalogueBuilder.dataType(name: dataTypeNameOrEnum, classification: dataTypeClassification, id: dataTypeCode)
        }
        String enumString = enumerations.sort().collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|')

        def dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)

        if (dataTypeReturn) {
            return catalogueBuilder.dataType(name: dataTypeReturn.name, id: dataTypeReturn.modelCatalogueId ?: dataTypeReturn.getDefaultModelCatalogueId(true))
        }
        return catalogueBuilder.dataType(name: dataElementName.replaceAll("\\s", "_"), enumerations: enumerations, classification: dataTypeClassification, id: dataTypeCode)
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


    protected static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

}
