package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.ValueDomain

class DataImportService {
    static transactional = false
    def publishedElementService, sessionFactory
    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
    private static final REGEX = '(?i)MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})_\\d+'

    def importData(ArrayList headers, ArrayList rows, String conceptualDomain, String conceptualDomainDescription, HeadersMap headersMap) {
        //get indexes of the appropriate sections
        DataImport newImporter = new DataImport()
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

    def resolveAll(DataImport importer){
       resolveAllPendingRows(importer)
       ingestImportQueue(importer)
    }

    def void addRow(DataImport importer, ImportRow row) {
        if(!importer.pendingAction.contains(row) && !importer.importQueue.contains(row))
            row = validateAndActionRow(row)
        (row.rowActions) ? importer.addToPendingAction(row) : importer.addToPendingAction(row)
    }

    def ImportRow validateAndActionRow(ImportRow row){
        if (!row.conceptualDomainName) {
            RowAction action = new RowAction(field: "conceptualDomainName", action: "please enter conceptual domain name to import row", actionType: ActionType.RESOLVE_ERROR).save()
            row.addToRowActions(action)
        }
        if (!row.containingModelName) {
            RowAction action = new RowAction(field: "containingModelName", action: "please complete the containing model name to import row", actionType: ActionType.RESOLVE_ERROR).save()
            row.addToRowActions(action)
        }
        if (!row.containingModelCode) {
            RowAction action = new RowAction(field: "containingModelCode", action: "Containing model does not have model catalogue code. New model will be created.", actionType: ActionType.CREATE_CONTAINING_MODEL).save()
            row.addToRowActions(action)
        }
        if (row.containingModelCode) {
            def md = Model.findByModelCatalogueId(row.containingModelCode)
            if(!md){
                RowAction action = new RowAction(field: "containingModelCode", action: "Containing Model Id does not match an existing element. New model will be created.", actionType: ActionType.CREATE_CONTAINING_MODEL).save()
                row.addToRowActions(action)
            }

            if (!row.containingModelCode.matches(REGEX)) {
                RowAction action = new RowAction(field: "containingModelCode", action: "the model catalogue code for the containing model is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR).save()
                row.addToRowActions(action)
            }
        }
        if (!row.parentModelCode && row.parentModelName) {
            RowAction action = new RowAction(field: "parentModelCode", action: "Parent model does not have model catalogue code. New model will be created.", actionType: ActionType.CREATE_PARENT_MODEL).save()
            row.addToRowActions(action)
        }
        if (row.parentModelCode) {
            def md = Model.findByModelCatalogueId(row.parentModelCode)
            if(!md){
                RowAction action = new RowAction(field: "parentModelCode", action: "Parent Model Id does not match an existing element. New model will be created.", actionType: ActionType.CREATE_PARENT_MODEL).save()
                row.addToRowActions(action)
            }
            if (!row.parentModelCode.matches(REGEX)) {
                RowAction action = new RowAction(field: "parentModelCode", action: "the model catalogue code for the parent model is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR).save()
                row.addToRowActions(action)
            }
        }
        if (!row.dataElementName) {
            RowAction action = new RowAction(field: "dataElementName", action: "No data element in row. Only Model information imported", actionType: ActionType.MODEL_ONLY_ROW).save()
            row.addToRowActions(action)
        }else{
            //we are importing a data element so we need to do these additional checks
            if (!row.dataElementCode) {
                RowAction action = new RowAction(field: "dataElementCode", action: "Data element does not have model catalogue code. New data element will be created.", actionType: ActionType.CREATE_DATA_ELEMENT).save()
                row.addToRowActions(action)
            }
            if (row.dataElementCode) {
                def de = DataElement.findByModelCatalogueId(row.dataElementCode)
                if (!row.dataElementCode.matches(REGEX)) {
                    RowAction action = new RowAction(field: "dataElementCode", action: "the model catalogue code for the data element is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR).save()
                    row.addToRowActions(action)
                }
                if (!de) {
                    RowAction action = new RowAction(field: "dataElementCode", action: "Data Element Id does not match an existing element. New data element will be created.", actionType: ActionType.CREATE_DATA_ELEMENT).save()
                    row.addToRowActions(action)
                }
            }
            if (!row.dataType) {
                RowAction action = new RowAction(field: "dataType", action: "the row does not contain a data type therefore will not be associated with a value domain, is this the expected outcome?", actionType: ActionType.DECISION).save()
                row.addToRowActions(action)
            }
        }
        return row
    }

    def void ingestImportQueue(DataImport importer) {
        def queue = importer.importQueue.iterator()
        def it = 0
        while (queue.hasNext()) {
            ImportRow row = queue.next()
            if (!row.rowActions) {
                if(it<60) {
                    ingestRow(importer, row)
                    queue.remove()
                }else{
                    it=0
                    cleanUpGorm()
                }
            }
        }
        actionPendingModels(importer)
    }


    def cleanUpGorm() {
        def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

    def void ingestRow(DataImport importer, ImportRow row) {
        if(row.rowActions.isEmpty()) {
            def conceptualDomain, model, dataType, measurementUnit
            conceptualDomain = importConceptualDomain(row.conceptualDomainName, row.conceptualDomainDescription)
            if(!row.dataElementName){
                //only need to import the model information
                importModels(importer, row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, conceptualDomain)
            }else {
                dataType = (row.dataType) ? importDataType(row.dataElementName, row.dataType) : null
                model = importModels(importer, row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, conceptualDomain)
                measurementUnit = importMeasurementUnit([name: row.measurementUnitName, symbol: row.measurementSymbol])
                if (dataType || measurementUnit) {
                    //need to import value domain stuff as well
                    importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model, [name: row.dataElementName.replaceAll("\\s", "_"), description: row.dataType.toString().take(2000), dataType: dataType, measurementUnit: measurementUnit], conceptualDomain)
                } else {
                    //doesn't have a value domain so easy
                    importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model)
                }
            }
        }
    }



    def updateModel(Model model, String name){
        if(name && name!=model.name){
            model.name = name
            model.status = PublishedElementStatus.UPDATED
            model.save()
        }
    }


    protected Model addModelToImport(DataImport importer, Model model) {
        if (!importer.models.find{it.id == model.id}) {
            if(model.status != PublishedElementStatus.UPDATED){model.status = PublishedElementStatus.PENDING}
            model.save()
            importer.models.add(model)
        }
        return model
    }

    def void actionPendingModels(DataImport importer) {
        importer.models.each { model ->
            def pendingDataElements = model.contains.findAll { it.status == PublishedElementStatus.UPDATED }
            if (pendingDataElements|| model.status == PublishedElementStatus.UPDATED ) {
                def archivedModel = publishedElementService.archiveAndIncreaseVersion(model)
                model.refresh()
                pendingDataElements.each { DataElement dataElement ->
                    archivedModel.removeFromContains(dataElement)
                    archivedModel.addToContains(dataElement.supersedes.first())
                    archivedModel.save()
                    model.removeFromContains(dataElement.supersedes.first())
                    model.save()
                    dataElement.status = PublishedElementStatus.FINALIZED
                    dataElement.save()
                }
            }
            model.status = PublishedElementStatus.FINALIZED
            model.save(flush:true)
        }
    }

    def importModels(DataImport importer, String parentCode, String parentName, String modelCode, String modelName, ConceptualDomain conceptualDomain) {
        if (parentCode) { parentCode = parentCode.trim() }
        if (modelCode) { modelCode = modelCode.trim() }
        if (parentName) { parentName = parentName.trim() }
        if (modelName) { modelName = modelName.trim() }
        Model model = Model.findByModelCatalogueId(modelCode)
        Model parentModel = Model.findByModelCatalogueId(parentCode)

        //if there are no models or an id hasn't been specified then try to match the model
        if (!model) { model = matchOrCreateModel(importer, [name: modelName, modelCatalogueId: (modelCode) ? modelCode : null], conceptualDomain)
        }else{ updateModel(model, modelName) }
        if(!parentModel){ parentModel = matchOrCreateModel(importer, [name: parentName, modelCatalogueId: (parentCode) ? parentCode : null], conceptualDomain)
        }else{updateModel( parentModel, parentName) }

        if(model){ model = addModelToImport(importer, model) }
        if(parentModel){ parentModel = addModelToImport(importer, parentModel) }
        if(model && parentModel) {model.addToChildOf(parentModel) }
        return model
    }

    protected Model matchOrCreateModel(DataImport importer, Map modelParams, ConceptualDomain conceptualDomain) {
        //check cache of models to see if it has already been created
        Model model = importer.models.find{it.name == modelParams.name}
        if(!model && modelParams.name){
            model = new Model(modelParams).save()
            model.addToHasContextOf(conceptualDomain)
        }
        return model
    }


    def importConceptualDomain(String name, String description) {
        name = name.trim()
        ConceptualDomain conceptualDomain = ConceptualDomain.findByName(name)
        if (!conceptualDomain) { conceptualDomain = new ConceptualDomain(name: name, description: description).save() }
        return conceptualDomain
    }


    def void resolveImportRowPendingAction(DataImport importer, ImportRow row, String field, ActionType actionType){
        def pendingRow = importer.pendingAction.find{ it == row}
        pendingRow.resolveAction(field, actionType)
        if(pendingRow.rowActions.size()==0){
            importer.removeFromPendingAction(pendingRow)
            importer.addToImportQueue(row)
        }
    }

    def void resolveAllPendingRows(DataImport importer){
        def resolveQueue = importer.pendingAction.iterator()
        while (resolveQueue.hasNext()) {
            ImportRow pendingRow = resolveQueue.next()
            pendingRow.resolveAll()
            importer.addToImportQueue(pendingRow)
        }
        importer.pendingAction.clear()
    }

    protected DataElement updateMetadata(Map metadata, DataElement dataElement) {
        metadata.each { key, value ->
            if (key) { key = key.toString().take(255) }
            if (value) { value = value.toString().take(255) }
            dataElement.ext.put(key, value)
        }
        dataElement.save()
        dataElement
    }

    protected Boolean checkValueDomainForChanges(Map params, ValueDomain valueDomain, ConceptualDomain cd){
        //ValueDomain vd = ValueDomain.findByDataTypeAndUnitOfMeasure(vdParams.dataType, params.unitOfMeasure)
        if(valueDomain) {
            if (!valueDomain.includedIn.contains(cd)) { return true }
            if (valueDomain.unitOfMeasure != params.unitOfMeasure) { return true }
            if (params.dataType instanceof EnumeratedType && valueDomain.dataType instanceof EnumeratedType) {
                if (valueDomain.dataType.enumAsString != params.dataType.enumAsString) { return true }
            } else if (valueDomain.dataType != params.dataType) { return true }
        }else{
            if(params.dataType||params.measurementUnit){return true}
        }
        return false
    }

    protected Boolean checkDataElementForChanges(Map params, Map metadata, DataElement dataElement) {
        Boolean hasDataElementChanged = false
        if (dataElement.name != params.name || dataElement.description != params.description) { return true }
        metadata.each { key, value ->
            if (key) { key = key.toString().take(255) }
            value = (value && value != "") ? value.toString().take(255) : null
            if (dataElement.ext.get(key) != value) { hasDataElementChanged = true}
        }
        return hasDataElementChanged
    }

    //update data element without value domain info
    protected DataElement updateDataElement(Map params, DataElement dataElement, Map metadata, Model model) {
        if (checkDataElementForChanges(params, metadata, dataElement)) {
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.name = params.name
            dataElement.description = params.description
            dataElement.status = PublishedElementStatus.UPDATED
            dataElement.save()
            dataElement = updateMetadata(metadata, dataElement)
            if(model.status!=PublishedElementStatus.UPDATED && model.status!=PublishedElementStatus.PENDING){
                model.status = PublishedElementStatus.PENDING
                model.save()
            }
        }
        return dataElement
    }

    //update data element given value domain info
    protected DataElement updateDataElement(Map params, DataElement dataElement, Map vdParams, ConceptualDomain cd, Map metadata, Model model) {
        Boolean dataElementChanged = checkDataElementForChanges(params, metadata, dataElement)
        ValueDomain vd = dataElement.instantiatedBy.find { it.includedIn.contains(cd) }
        Boolean valueDomainChanged = checkValueDomainForChanges(vdParams, vd, cd)

        if (dataElementChanged || valueDomainChanged) {
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.refresh()

            if(dataElementChanged) {
                dataElement.name = params.name
                dataElement.description = params.description
                dataElement.status = PublishedElementStatus.UPDATED
                dataElement.save()
                dataElement = updateMetadata(metadata, dataElement)
            }

            if (valueDomainChanged) {
                if(dataElement.status != PublishedElementStatus.UPDATED) {
                    dataElement.status = PublishedElementStatus.UPDATED
                    dataElement.save()
                }
                //remove the old one (will still be in the archived one)
                if (vd) { vd.removeFromInstantiates(dataElement) }
                //see if there is one that matches or create a new one
                importValueDomain(vdParams, dataElement, cd)

            }

            if(model.status!=PublishedElementStatus.UPDATED && model.status!=PublishedElementStatus.PENDING){
                model.status = PublishedElementStatus.PENDING
                model.save()
            }
        }
        return dataElement
    }

    protected ValueDomain importValueDomain(Map vdParams, DataElement dataElement, ConceptualDomain cd) {
        ValueDomain vd = ValueDomain.findByDataTypeAndUnitOfMeasure(vdParams.dataType, vdParams.unitOfMeasure)
        if (!vd) { vd = new ValueDomain(vdParams).save() }
        vd.addToIncludedIn(cd)
        vd.addToInstantiates(dataElement)
        vd.save()
    }

    protected DataElement importDataElement(Map params, Map metadata, Model model, Map vdParams, ConceptualDomain cd) {

        //find out if data element exists using unique code
        DataElement de = DataElement.findByModelCatalogueId(params.modelCatalogueId)
        if (de) { de = updateDataElement(params, de, vdParams, cd, metadata, model) }

        //find if data element exists using name and containing model
        if (!de) {
            def nameDE = DataElement.findByName(params.name)
            if (nameDE && nameDE.containedIn.contains(model)) {
                de = nameDE
                if (de) { de = updateDataElement(params, de, vdParams, cd, metadata, model)  }
            }
        }

        if (!de) {
            params.put('status', PublishedElementStatus.FINALIZED)
            de = new DataElement(params).save()
            de = updateMetadata(metadata, de)
        }

        importValueDomain(vdParams, de, cd)
        de.addToContainedIn(model)

        return de
    }


    protected DataElement importDataElement(Map params, Map metadata, Model model) {

        //find out if data element exists using unique code
        DataElement de = DataElement.findByModelCatalogueId(params.modelCatalogueId)
        if (de) { de = updateDataElement(params, de, metadata, model) }

        //find if data element exists using name and containing model
        if (!de && params.name) {
            def nameDE = DataElement.findByName(params.name)
            if (nameDE && nameDE.containedIn.contains(model)) {
                de = nameDE
                if (de) { de = updateDataElement(params, de, metadata, model) }
            }
        }

        if (!de && params.name) {
            params.put('status', PublishedElementStatus.FINALIZED)
            de = new DataElement(params).save()
            de = updateMetadata(metadata, de)
        }

        if(de){de.addToContainedIn(model)}
        return de
    }


    def importMeasurementUnit(Map params) {
        MeasurementUnit mu
        if(params.name && params.symbol) {
            mu = MeasurementUnit.findByNameAndSymbol(params.name, params.symbol)
        }else {
            if (!mu) {
                mu = MeasurementUnit.findBySymbol(params.symbol)
            }
            if (!mu) {
                mu = MeasurementUnit.findByNameIlike(params.name)
            }
        }
        return mu
    }


    def importDataType(String name, String data) {
        DataType dataTypeReturn = importEnumeratedType(data)
        if(!dataTypeReturn) dataTypeReturn = DataType.findByName(data)
        if(!dataTypeReturn) dataTypeReturn = DataType.findByNameIlike(data)
        return dataTypeReturn
    }

    protected static EnumeratedType importEnumeratedType(String data){
        def dataTypeReturn, sortedData
        if (data.contains("|")) {
            try { sortedData = sortEnumAsString(data) } catch (Exception e) { return null }
            dataTypeReturn = EnumeratedType.findByEnumAsString(sortedData)
            if (!dataTypeReturn) { dataTypeReturn = new EnumeratedType(name:  ( (sortedData.size()>20) ? sortedData[0..20] : sortedData ) + "..", enumAsString: sortedData).save() }
        } else if (data.contains("\n") || data.contains("\r")) {
            String[] lines = data.split("\\r?\\n")
            if (lines.size() > 0 && lines[] != null) {
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
                        if (value.isEmpty()) {
                            value = "_"
                        }
                        enumerations.put(key, value)
                    }
                }
                if (!enumerations.isEmpty()) {
                    String enumString = enumerations.sort() collect { key, val ->
                        "${this.quote(key)}:${this.quote(val)}"
                    }.join('|')
                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)
                    if (!dataTypeReturn) {
                        dataTypeReturn = new EnumeratedType(name:  ( (enumString.size()>20) ? enumString[0..20] : enumString ) + "..", enumerations: enumerations).save()
                    }
                }
            }
        }
        dataTypeReturn
    }



    protected static String sortEnumAsString(String inputString) {
        if (inputString == null) return null
        String sortedString
        Map<String, String> ret = [:]
        inputString.split(/\|/).each { String part ->
            if (!part) return
            String[] pair = part.split(/:/)
            if (pair.length != 2) throw new IllegalArgumentException("Wrong enumerated value '$part' in encoded enumeration '$s'")
            ret[unquote(pair[0])] = unquote(pair[1])
        }
        sortedString = ret.sort() collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|').trim()
        sortedString
    }

    protected static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

    protected static String unquote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.reverseEach { original, pattern ->
            ret = ret.replace(pattern, original)
        }
        ret
    }




}
