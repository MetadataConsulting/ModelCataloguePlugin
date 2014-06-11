package org.modelcatalogue.core.dataarchitect

class DataImportService {

    static transactional = true

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
    //the import script accepts and array of headers these should include the following:
    //Data Item Name, Data Item Description, Parent Section, Section, Measurement Unit, Data type
    //these will allow the import script to identify the rows

    def importData(ArrayList headers, ArrayList rows, String conceptualDomain, String conceptualDomainDescription, HeadersMap headersMap) {
        //get indexes of the appropriate sections
        Import newImporter = new Import()
        def dataItemNameIndex = headers.indexOf(headersMap.dataElementNameRow)
        def dataItemCodeIndex = headers.indexOf(headersMap.dataElementCodeRow)
        def dataItemDescriptionIndex = headers.indexOf(headersMap.dataElementDescriptionRow)
        def parentModelIndex = headers.indexOf(headersMap.parentModelNameRow)
        def modelIndex = headers.indexOf(headersMap.containingModelNameRow)
        def parentModelCodeIndex = headers.indexOf(headersMap.parentModelCodeRow)
        def modelCodeIndex = headers.indexOf(headersMap.containingModelCodeRow)
        def unitsIndex = headers.indexOf(headersMap.measurementUnitNameRow)
        def dataTypeIndex = headers.indexOf(headersMap.dataTypeRow)
        def metadataStartIndex = headers.indexOf(headersMap.metadataRow) + 1
        def metadataEndIndex = headers.size() - 1
        def elements = []
        if (dataItemNameIndex == -1) throw new Exception("Can not find 'Data Item Name' column")
        //iterate through the rows and import each line
        rows.eachWithIndex { def row, int i ->
            ImportRow importRow = new ImportRow()
            importRow.dataElementName = (dataItemNameIndex!=-1)?row[dataItemNameIndex]:null
            importRow.dataElementCode = (dataItemCodeIndex!=-1)?row[dataItemCodeIndex]:null
            importRow.parentModelName = (parentModelIndex!=-1)?row[parentModelIndex]:null
            importRow.parentModelCode = (parentModelCodeIndex!=-1)?row[parentModelCodeIndex]:null
            importRow.containingModelName = (modelIndex!=-1)?row[modelIndex]:null
            importRow.containingModelCode = (modelCodeIndex!=-1)?row[modelCodeIndex]:null
            importRow.dataType =   (dataTypeIndex!=-1)?row[dataTypeIndex]:null
            importRow.dataElementDescription =   (dataItemDescriptionIndex!=-1)?row[dataItemDescriptionIndex]:null
            importRow.measurementUnitName =   (unitsIndex!=-1)?row[unitsIndex]:null
            importRow.conceptualDomainName = conceptualDomain
            importRow.conceptualDomainDescription = conceptualDomainDescription
            def counter = metadataStartIndex
            def metadataColumns = [:]
            while (counter <= metadataEndIndex) {
                metadataColumns.put(headers[counter], row[counter])
                counter++
            }
            importRow.metadata = (metadataColumns)?metadataColumns:null
            importRow.save()
            newImporter.addRow(importRow)
        }
        return newImporter.save()
    }

    def resolveAll(Import importer){
       importer.resolveAllPendingRows()
       importer.ingestImportQueue()
    }

}
