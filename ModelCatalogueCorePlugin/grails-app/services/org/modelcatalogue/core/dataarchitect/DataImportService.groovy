package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.CatalogueBuilder

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
        def dataTypeIndex = headers.indexOf(headersMap.dataType)
        def metadataStartIndex = headers.indexOf(headersMap.metadata) + 1
        def metadataEndIndex = headers.size() - 1

        if (dataItemNameIndex == -1) throw new Exception("Can not find 'Data Item Name' column")
        //iterate through the rows and import each line
        CatalogueBuilder builder = new CatalogueBuilder(classificationService)
        builder.build {
            rows.eachWithIndex { def row, int i ->
                classification(name: getRowValue(row,classificationsIndex)) {
                    globalSearchFor dataType

                    model(name:getRowValue(row,parentModelIndex), id:getRowValue(row,parentModelCodeIndex)){
                        model(name:getRowValue(row,modelIndex),id: getRowValue(row,modelCodeIndex) ){
                            if(getRowValue(row,dataItemNameIndex)) {
                                dataElement(name: getRowValue(row, dataItemNameIndex), description: getRowValue(row, dataItemDescriptionIndex), id: getRowValue(row, dataItemCodeIndex)) {
                                    if (getRowValue(row, unitsIndex) || getRowValue(row, dataTypeIndex)) {
                                        valueDomain(name: getRowValue(row, dataItemNameIndex)) {
                                            if (getRowValue(row, dataTypeIndex))
                                                importDataTypes(builder, getRowValue(row, dataItemNameIndex), getRowValue(row, dataTypeIndex))
                                            if (getRowValue(row, unitsIndex))
                                                measurementUnit(name: getRowValue(row, unitsIndex), symbol: getRowValue(row, symbolsIndex))
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
     * @param name data element/item name
     * @param dataType - Column F - content of - either blank or an enumeration or a named datatype.
     * @return
     */
     static importDataTypes(CatalogueBuilder catalogueBuilder, name, dataType) {

        //default data type to return is the string data type
        for (String line in dataType) {
            String[] lines = line.split("\\r?\\n");
            if (!(lines.size() > 0 && lines != null)) {
                return catalogueBuilder.dataType(name: "String")
            }

            def enumerations = parseEnumeration(lines)

            if(!enumerations){
                return catalogueBuilder.dataType(name: name) ?: catalogueBuilder.dataType(name: "String")
            }
            String enumString = enumerations.sort() collect { key, val ->
                "${quote(key)}:${quote(val)}"
            }.join('|')

            def dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)

            if (dataTypeReturn) {
                return catalogueBuilder.dataType(name: dataTypeReturn.name)
            }
            return catalogueBuilder.dataType(name: name.replaceAll("\\s", "_"), enumerations: enumerations)
        }
        catalogueBuilder.dataType(name: "String")
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
