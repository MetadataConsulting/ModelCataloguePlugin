package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtendibleElement
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain


class DataImportService {

    def sessionFactory, dataArchitectService
    static transactional = true

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
    //the import script accepts and array of headers these should include the following:
    //Data Item Name, Data Item Description, Parent Section, Section, Measurement Unit, Data type
    //these will allow the import script to identify the rows

    def importData(ArrayList headers, ArrayList rows, String conceptualDomain, String conceptualDomainDescription, ArrayList parentModels, HeadersMap headersMap) {
        //get indexes of the appropriate sections
        def totalCounter = 0
        def newImporter = new Importer(parentModels:parentModels)
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
            importRow.parentModelCode = (parentModelCodeIndex!=-1)?row[parentModelCodeIndex]:null

            def counter = metadataStartIndex
            def metadataColumns = [:]
            while (counter <= metadataEndIndex) {
                metadataColumns.put(headers[counter], row[counter])
                counter++
            }
            importRow.metadata = (metadataColumns)?metadataColumns:null

            if (totalCounter > 40) {
                sessionFactory.currentSession.flush()
                sessionFactory.currentSession.clear()
                counter = 0
            } else {
                totalCounter++
            }

            newImporter.ingestRow(importRow)
        }

        newImporter.actionPendingModels()
        sessionFactory.currentSession.flush()
        sessionFactory.currentSession.clear()

        dataArchitectService.indexAll()

    }

}
