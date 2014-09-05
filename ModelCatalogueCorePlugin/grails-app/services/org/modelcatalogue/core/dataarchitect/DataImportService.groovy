package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtendibleElement
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.xsd.*

class DataImportService {
    static transactional = false
    def publishedElementService, sessionFactory, relationshipService
    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
    private static final REGEX = '(?i)MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})_\\d+'

    @Transactional
    def importData(ArrayList headers, ArrayList rows, String name, String conceptualDomain, String conceptualDomainDescription, HeadersMap headersMap) {
        //get indexes of the appropriate sections
        DataImport newImporter = new DataImport(name: name)
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
        def conceptualDomainIndex = headers.indexOf(headersMap.conceptualDomainName)
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
            importRow.conceptualDomainName = (conceptualDomainIndex!=-1)?row[conceptualDomainIndex]:conceptualDomain
            importRow.conceptualDomainDescription = (conceptualDomainIndex!=-1)?row[conceptualDomainIndex]:conceptualDomain
            importRow.classification = (classificationsIndex!=-1)?row[classificationsIndex]:null
            importRow.measurementSymbol = (symbolsIndex!=-1)?row[symbolsIndex]:null
            def counter = metadataStartIndex
            def metadataColumns = [:]
            while (counter <= metadataEndIndex) {
                String key = headers[counter].toString()
                String value = (row[counter]!=null) ? row[counter].toString() : ""
                if (key!="" && key!="null") metadataColumns.put(key, value)
                counter++
            }
            importRow.metadata = (metadataColumns)?metadataColumns:null
            importRow.save()
            addRow(newImporter, importRow)
        }

        newImporter.save(flush:true, failOnError:true)

    }

    def resolveRow(DataImport importer, ImportRow importRow) {
        importRow.resolveAll()
        if (!importRow.rowActions) {
            importer.removeFromPendingAction(importRow)
            importer.addToImportQueue(importRow)
        }
    }

    def resolveAll(DataImport importer){
       resolveAllPendingRows(importer)
       ingestImportQueue(importer)
    }

    def void addRow(DataImport importer, ImportRow row) {
        if(!importer.pendingAction.contains(row) && !importer.importQueue.contains(row))
            row = validateAndActionRow(row)
        importer.addToPendingAction(row)
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
        } else {
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

        if (!row.parentModelCode) {
            if (row.parentModelName) {
                RowAction action = new RowAction(field: "parentModelCode", action: "Parent model does not have model catalogue code. New model will be created.", actionType: ActionType.CREATE_PARENT_MODEL).save()
                row.addToRowActions(action)
            }
        } else {
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
        } else {
            //we are importing a data element so we need to do these additional checks
            if (!row.dataElementCode) {
                RowAction action = new RowAction(field: "dataElementCode", action: "Data element does not have model catalogue code. New data element will be created.", actionType: ActionType.CREATE_DATA_ELEMENT).save()
                row.addToRowActions(action)
            } else {
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
            if (row.dataType.contains("|")) {
                try {
                    row.dataType = sortEnumAsString(row.dataType)
                } catch (Exception e) {
                    RowAction action = new RowAction(field: "dataType", action: "the row has an invalid enumerated data type. Please action to import row", actionType: ActionType.RESOLVE_ERROR).save()
                    row.addToRowActions(action)
                }
            }
        }
        return row
    }

    @Transactional
    def void ingestImportQueue(DataImport importer) {
        def queue = importer.importQueue.iterator()
        def it = 0
        while (queue.hasNext()) {
            ImportRow row = queue.next()
            if (!row.rowActions) {
                if(it<60) {
                    ingestRow(importer, row, true)
                    queue.remove()
                }else{
                    it=0
                    cleanUpGorm()
                }
            }
        }
        actionPendingModels(importer)
    }

    /**
     * Clean up the session to speed up the import.
     * @see http://naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql
     */
    def cleanUpGorm() {
        def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

    def void ingestRow(DataImport importer, ImportRow row, Boolean bulkIngest = false) {
        if(row.rowActions.isEmpty()) {
            def conceptualDomain, model, dataType, measurementUnit, classification
            conceptualDomain = importConceptualDomain(row.conceptualDomainName, row.conceptualDomainDescription)
            classification = importClassification(row.classification)
            if(!row.dataElementName){
                //only need to import the model information
                importModels(importer, row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, conceptualDomain, classification)
            }else {
                dataType = (row.dataType) ? importDataType(row.dataElementName, row.dataType) : null
                model = importModels(importer, row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, conceptualDomain, classification)
                measurementUnit = importMeasurementUnit([name: row.measurementUnitName, symbol: row.measurementSymbol])
                if (dataType || measurementUnit) {
                    //need to import value domain stuff as well
                    importDataElement(importer, [name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model, [name: row.dataElementName.replaceAll("\\s", "_"), description: row.dataType.toString().take(2000), dataType: dataType, unitOfMeasure: measurementUnit], conceptualDomain, classification)
                } else {
                    //doesn't have a value domain so easy
                    importDataElement(importer, [name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model, conceptualDomain, classification)
                }
            }
            if(!bulkIngest) importer.removeFromImportQueue(row)
            row.imported = true
            importer.addToImported(row)
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
            //if(model.status != PublishedElementStatus.UPDATED && model.status != PublishedElementStatus.PENDING){model.status = PublishedElementStatus.DRAFT}
//            model.save()
            importer.models.add(model)
        }
        return model
    }

    protected void addUpdatedDataElements(DataImport importer, DataElement dataElement, Model model, ConceptualDomain conceptualDomain ){
        if(model.status==PublishedElementStatus.FINALIZED){
            model.status = PublishedElementStatus.UPDATED
            model.save()
        }
        importer.updatedDataElements.add([dataElement, model, conceptualDomain])
    }

    def void actionPendingModels(DataImport importer) {
        importer.models.each { model ->
            def pendingDataElements = importer.updatedDataElements.findAll { it[1] == model }

            if(model.status == PublishedElementStatus.UPDATED) {
                def archivedModel = publishedElementService.archiveAndIncreaseVersion(model)
                model.refresh()
            }

            if (pendingDataElements) {
                pendingDataElements.each { it ->
                    def dataElement = it[0]
                    def relationship = model.addToContains(dataElement)
//                    relationship.ext.put("Context" , it[2].name)
                    dataElement.status = PublishedElementStatus.FINALIZED
                    dataElement.save(flush:true, failOnError:true)
                }
            }
            model.refresh()
            model.status = PublishedElementStatus.PENDING
            model.save(flush:true, failOnError:true)
        }

        importer.models.each{ Model model->
            publishedElementService.finalizeTree(model)
        }


    }

    def importModels(DataImport importer, String parentCode, String parentName, String modelCode, String modelName, ConceptualDomain conceptualDomain, Classification classification) {
        if (parentCode) { parentCode = parentCode.trim() }
        if (modelCode) { modelCode = modelCode.trim() }
        if (parentName) { parentName = parentName.trim() }
        if (modelName) { modelName = modelName.trim() }
        Model model = Model.findByModelCatalogueId(modelCode)
        Model parentModel = Model.findByModelCatalogueId(parentCode)

        //if there are no models or an id hasn't been specified then try to match the model
        if (!model) { model = matchOrCreateModel(importer, [name: modelName, modelCatalogueId: (modelCode) ? modelCode : null], conceptualDomain, classification)
        }else{ updateModel(model, modelName) }
        if(!parentModel){ parentModel = matchOrCreateModel(importer, [name: parentName, modelCatalogueId: (parentCode) ? parentCode : null], conceptualDomain, classification)
        }else{updateModel( parentModel, parentName) }

        if(model){ model = addModelToImport(importer, model) }
        if(parentModel){ parentModel = addModelToImport(importer, parentModel) }
        if(model && parentModel) {model.addToChildOf(parentModel) }
        return model
    }

    protected Model matchOrCreateModel(DataImport importer, Map modelParams, ConceptualDomain conceptualDomain, Classification classification) {
        //check cache of models to see if it has already been created
        Model model = importer.models.find{it.name == modelParams.name}
        if(!model && modelParams.name){
            model = createModel(modelParams, classification, conceptualDomain)
        }else{
            model.addToClassifications(classification)
        }
        return model
    }

    protected createModel(Map modelParams, Classification classification, ConceptualDomain conceptualDomain){
        Model model = new Model(modelParams)
        model.modelCatalogueId = modelParams.modelCatalogueId
        model.save()
        model.addToClassifications(classification)
        model.addToHasContextOf(conceptualDomain)
        return model
    }


    def importConceptualDomain(String name, String description) {
        name = name.trim()
        ConceptualDomain conceptualDomain = ConceptualDomain.findByName(name)
        if (!conceptualDomain) { conceptualDomain = new ConceptualDomain(name: name, description: description).save() }
        return conceptualDomain
    }

    def importClassification(String name) {
        name = name.trim()
        Classification classification = Classification.findByName(name)
        if (!classification) { classification = new Classification(name: name).save() }
        return classification
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

            if(pendingRow.rowActions.size()==0){
                resolveQueue.remove()
                importer.addToImportQueue(pendingRow)
            }

        }
    }

    protected ExtendibleElement updateMetadata(Map metadata, ExtendibleElement instance) {
        metadata.each { key, value ->
            if (key) { key = key.toString().take(255) }
            if (value) { value = value.toString().take(255) }
            instance.ext.put(key, value)
        }
        instance.save()
        instance
    }

    protected Boolean checkValueDomainForChanges(Map params, ValueDomain valueDomain, ConceptualDomain cd){
        if(valueDomain) {
            if (!valueDomain.conceptualDomains.contains(cd)) { return true }
            if (valueDomain.unitOfMeasure != params.unitOfMeasure) { return true }
            if (params.dataType instanceof EnumeratedType && valueDomain.dataType instanceof EnumeratedType) {
                if (valueDomain.dataType.enumAsString != params.dataType.enumAsString) { return true }
            } else if (valueDomain.dataType != params.dataType) { return true }
        }else{
            if(params.dataType||params.unitOfMeasure){return true}
        }
        return false
    }

    protected Boolean checkValueDomainForChanges(ValueDomain newVD, ValueDomain valueDomain, ConceptualDomain cd){
        if(valueDomain) {
            if (!valueDomain.conceptualDomains.contains(cd)) { return true }
            if (valueDomain.unitOfMeasure != newVD.unitOfMeasure) { return true }
            if (newVD.dataType instanceof EnumeratedType && valueDomain.dataType instanceof EnumeratedType) {
                if (valueDomain.dataType.enumAsString != newVD.dataType.enumAsString) { return true }
            } else if (valueDomain.dataType != newVD.dataType) { return true }
        }else{
            if(newVD.dataType||newVD.unitOfMeasure){return true}
        }
        return false
    }


    protected Boolean checkDataElementForChanges(Map params, Map metadata, DataElement dataElement, Classification classification) {
        Boolean hasDataElementChanged = false
        if (dataElement.name != params.name || dataElement.description != params.description || !dataElement.classifications.contains(classification)) { return true }

        metadata.each { key, value ->
            if (key) { key = key.toString().take(255) }
            value = (value && value != "") ? value.toString().take(255) : null
            if (dataElement.ext.get(key) != value) { hasDataElementChanged = true}
        }
        return hasDataElementChanged
    }

    //update data element without value domain info
    protected DataElement updateDataElement(DataImport importer, Map params, DataElement dataElement, Map metadata, Model model, ConceptualDomain conceptualDomain, Classification classification) {
        if (checkDataElementForChanges(params, metadata, dataElement, classification)) {
            if(model.status!=PublishedElementStatus.UPDATED && model.status!=PublishedElementStatus.DRAFT){
                model.status = PublishedElementStatus.UPDATED
                model.save()
            }
            addModelToImport(importer, model)
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.refresh()
            dataElement.name = params.name
            dataElement.description = params.description
            dataElement.status = PublishedElementStatus.UPDATED
            dataElement.save()
            dataElement.addToClassifications(classification)
            dataElement = updateMetadata(metadata, dataElement)
            addUpdatedDataElements(importer, dataElement, model, conceptualDomain)
        }

        return dataElement
    }

    //update data element given value domain info
    protected DataElement updateDataElement(DataImport importer, Map params, DataElement dataElement, Map vdParams, ConceptualDomain cd, Map metadata, Model model, ConceptualDomain conceptualDomain, Classification classification) {
        Boolean dataElementChanged = checkDataElementForChanges(params, metadata, dataElement, classification)
        ValueDomain vd = dataElement.valueDomain
        Boolean valueDomainChanged = checkValueDomainForChanges(vdParams, vd, cd)

        if (dataElementChanged || valueDomainChanged) {

            if(model.status!=PublishedElementStatus.UPDATED && model.status!=PublishedElementStatus.DRAFT){
                model.status = PublishedElementStatus.UPDATED
                model.save()
            }

            addModelToImport(importer, model)

            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.refresh()

            if(dataElementChanged) {
                dataElement.name = params.name
                dataElement.description = params.description
                dataElement.status = PublishedElementStatus.UPDATED
                dataElement.save()
                dataElement.addToClassifications(classification)
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

            addUpdatedDataElements(importer, dataElement, model, conceptualDomain)

        }

        return dataElement
    }

    //update data element given value domain info
    protected DataElement updateDataElement(DataImport importer, element, DataElement dataElement, ValueDomain valueDomain, ConceptualDomain cd, Model model, Classification classification) {

        def params = [name: element.name, description: element.description]
        Map metadata = [:]
        Boolean dataElementChanged = checkDataElementForChanges(params, metadata, dataElement, classification)
        ValueDomain vd = dataElement.valueDomain
        Boolean valueDomainChanged = checkValueDomainForChanges(valueDomain, vd, cd)

        if (dataElementChanged || valueDomainChanged) {

            if(model.status!=PublishedElementStatus.UPDATED && model.status!=PublishedElementStatus.DRAFT){
                model.status = PublishedElementStatus.UPDATED
                model.save()
            }

            addModelToImport(importer, model)

            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.refresh()

            if(dataElementChanged) {
                dataElement.name = element.name
                dataElement.description = element.description
                dataElement.status = PublishedElementStatus.UPDATED
                dataElement.save()
                dataElement.addToClassifications(classification)
                dataElement = updateMetadata(metadata, dataElement)
            }

            if (valueDomainChanged) {
                if(dataElement.status != PublishedElementStatus.UPDATED) {
                    dataElement.status = PublishedElementStatus.UPDATED
                    dataElement.valueDomain = valueDomain
                    dataElement.save()
                }

            }

            addUpdatedDataElements(importer, dataElement, model, conceptualDomain)

        }

        return dataElement
    }

    protected ValueDomain importValueDomain(Map vdParams, DataElement dataElement, ConceptualDomain cd) {
        ValueDomain vd = ValueDomain.findByDataTypeAndUnitOfMeasure(vdParams.dataType, vdParams.unitOfMeasure)
        if (!vd) { vd = new ValueDomain(vdParams).save() }
        vd.addToConceptualDomains(cd)
        dataElement.valueDomain = vd
        vd.save()
    }


    protected  importValueDomain(String name, String description, DataType dataType, String regexDef, ConceptualDomain cd, Boolean multiple=Boolean.FALSE) {
        ValueDomain vd = ValueDomain.findByDataTypeAndName(dataType, name)
        if (!vd) {
            vd = new ValueDomain(name: name, description: description, dataType: dataType, multiple: multiple).save()
            if (regexDef!=""){
                vd.setRegexDef(regexDef)
                vd.save()
            }
        }
        if (vd) {
            vd.addToConceptualDomains(cd)
            vd.save()
        }
        else {
            println("Value Domain: " + name + " not imported.")
        }
    }


    protected DataElement importDataElement(DataImport importer, Map params, Map metadata, Model model, Map vdParams, ConceptualDomain cd, Classification classification) {

        //find out if data element exists using unique code
        DataElement de = DataElement.findByModelCatalogueId(params.modelCatalogueId)
        if (de) {
            de = updateDataElement(importer, params, de, vdParams, cd, metadata, model, cd, classification)

        }

        //find if data element exists using name and containing model
        if (!de) {
            def nameDE = DataElement.findByName(params.name)
            if (nameDE && nameDE.containedIn.contains(model)) {
                de = nameDE
                if (de) { de = updateDataElement(importer, params, de, vdParams, cd, metadata, model, cd, classification)  }
            }
        }

        if (!de) {
            params.put('status', PublishedElementStatus.FINALIZED)
            de = new DataElement(params)
            de.modelCatalogueId = params.modelCatalogueId
            de.save()
            de = updateMetadata(metadata, de)
            de.addToClassifications(classification)
//            Relationship containedIn = de.addToContainedIn(model)
//            containedIn.ext.put("Context" , cd.name)
            addModelToImport(importer, model)
            addUpdatedDataElements(importer, de, model, cd)
        }

        importValueDomain(vdParams, de, cd)

        return de
    }


    protected DataElement importDataElement(DataImport importer, Map params, Map metadata, Model model, ConceptualDomain cd, Classification classification) {

        //find out if data element exists using unique code
        DataElement de = DataElement.findByModelCatalogueId(params.modelCatalogueId)
        if (de) { de = updateDataElement(importer, params, de, metadata, model, cd, classification) }

        //find if data element exists using name and containing model
        if (!de && params.name) {
            def nameDE = DataElement.findByName(params.name)
            if (nameDE && nameDE.containedIn.contains(model)) {
                de = nameDE
                if (de) { de = updateDataElement(importer, params, de, metadata, model, cd, classification) }
            }
        }

        if (!de && params.name) {
            params.put('status', PublishedElementStatus.FINALIZED)
            de = new DataElement(params)
            de.modelCatalogueId = params.modelCatalogueId
            de.save()
            de.addToClassifications(classification)
            de = updateMetadata(metadata, de)
//            Relationship  containedIn = de.addToContainedIn(model)
//            containedIn.ext.put("Context" , cd.name)
            addModelToImport(importer, model)
            addUpdatedDataElements(importer, de, model, cd)
        }
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
        if(!mu && params.name) mu = new MeasurementUnit(name: params.name, symbol: params.symbol).save()
        return mu
    }


    def importDataType(String name, String data) {
        DataType dataTypeReturn = importEnumeratedType(data, name)
        if(!dataTypeReturn) dataTypeReturn = DataType.findByName(data)
        if(!dataTypeReturn) dataTypeReturn = DataType.findByNameIlike(data)
        return dataTypeReturn
    }

    protected EnumeratedType importEnumeratedType(String data, String name){
        def dataTypeReturn
        if (data.contains("|")) {
            dataTypeReturn = EnumeratedType.findByEnumAsString(data)
            if (!dataTypeReturn) {
                try {
                    dataTypeReturn = new EnumeratedType(name:  name, enumAsString: data).save()
                }
                catch (Exception e) {
                    log.error "Error: ${e.message}", e
                }
            }
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
                        try {
                            dataTypeReturn = new EnumeratedType(name:  name, enumerations: enumerations).save()
                        }
                        catch (Exception e) {
                            log.error "Error: ${e.message}", e
                        }
                    }
                }
            }
        }
        dataTypeReturn
    }



    protected String sortEnumAsString(String inputString) {
        if (inputString == null) return null
        String sortedString
        Map<String, String> ret = [:]
        inputString.split(/\|/).each { String part ->
            if (!part) return
            String[] pair = part.split("(?<!\\\\):")
            if (pair.length != 2) throw new IllegalArgumentException("Wrong enumerated value '$part' in encoded enumeration '$s'")
            ret[unquote(pair[0])] = unquote(pair[1])
        }
        sortedString = ret.sort() collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|').trim()
        sortedString
    }

    protected String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

    protected String unquote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.reverseEach { original, pattern ->
            ret = ret.replace(pattern, original)
        }
        ret
    }


    def createDataTypesAndValueDomains(DataImport importer, ConceptualDomain cd, ArrayList<XsdSimpleType> simpleDataTypes){
        DataType dataType
        ValueDomain vd
        // Add all the simple data types
        simpleDataTypes.each { XsdSimpleType simpleType ->
            (vd, dataType) = createSimpleType(importer, cd, simpleType, simpleDataTypes)
            addRulesToSimpleType(simpleType, vd)
            if (vd!=null && dataType!=null) println("SimpleType: " + simpleType.name)
        }
    }


    private addRulesToSimpleType( XsdSimpleType simpleType, ValueDomain vd){
        //Check the rules/patterns that apply to this type
        String rule = ""
        simpleType.restriction?.patterns?.each { XsdPattern pattern ->
            def patternLength = pattern.value.length()
            if (simpleType.restriction.minLength != "" && simpleType.restriction.maxLength == "" && pattern.value.charAt(patternLength - 1) == "]") {
                if (rule =="") rule += pattern.value ("{" + simpleType.restriction.minLength + "," + simpleType.restriction.maxLength + "}")
                else rule += ("|" + pattern.value ("{" + simpleType.restriction.minLength + "," + simpleType.restriction.maxLength + "}"))
            }
        }
        vd.setRegexDef(rule)
        vd.save()
    }

    private createSimpleType(DataImport importer, ConceptualDomain cd, XsdSimpleType simpleType, ArrayList<XsdSimpleType> simpleDataTypes){
        String type
        String description= simpleType?.description
        ValueDomain vd
        def dataType
        String name = simpleType.name
        if (simpleType.restriction!= null && simpleType.restriction.base != null) {
            type = simpleType.restriction.base
            if (simpleType.restriction.base.contains("xs:")) {
                dataType = DataType.findByName(simpleType.restriction.base)

                if (dataType == null) {
                    dataType = simpleDataTypes.find{it.name==simpleType.restriction.base}
                    if(dataType){
                        createSimpleType(importer, cd, dataType, simpleDataTypes)
                        dataType = DataType.findByName(simpleType.restriction.base)
                    }else {
                        dataType = new DataType(name: simpleType.restriction.base).save()
                    }
                }

                vd = importValueDomain(name, description, dataType, "", cd)
                addMetadataToValueDomain(vd, simpleType)
                vd.save()
                return [vd, dataType]
            }
            else {

                //Check if the value domain already exists
                vd = ValueDomain.findByName(name)
                if(vd && !vd.conceptualDomains.contains(cd)) vd = null
                if (vd == null) {
                    vd = ValueDomain.findByName(type)
                    if(!vd) {
                        XsdSimpleType simpleDataType = simpleDataTypes.find { it.name == type }
                        (vd, dataType) = createSimpleType(importer, cd, simpleDataType, simpleDataTypes)
                    }
                    // Check enumerated elements
//                    if (simpleType.restriction.enumeration != "") {
                        DataType enumeratedDataType = importDataType(simpleType.name, simpleType.restriction.enumeration)
                        ValueDomain enumeratedVD = importValueDomain(simpleType.name, simpleType.description, enumeratedDataType, "", cd)
                        addMetadataToValueDomain(enumeratedVD, simpleType)
                        enumeratedVD.save()
                        enumeratedVD.addToBasedOn(vd)
                        vd.save()
                        enumeratedVD.save()
                        return [enumeratedVD, enumeratedDataType]
//                    }
//                    else return [vd,dataType]
                } else {
                    return [vd, vd.dataType]
                }
            }
        }
        else
        {
            //Check for union
            if (simpleType.union!=null)
            {
                //get datatypes of union
                String [] dataTypes =  simpleType.union.memberTypes.split(" ")
                if (dataTypes.size()>0)
                {
                    ArrayList<ValueDomain> valueDomains = []
                    dataTypes.each {String base ->
                        dataType = DataType.findByName(base)

                        if (dataType == null) {
                            dataType = simpleDataTypes.find{it.name==base}
                            if(dataType){
                                createSimpleType(importer, cd, dataType, simpleDataTypes)
                                dataType = DataType.findByName(base)
                            }else {
                                dataType = new DataType(name: base).save()
                            }
                        }

                        ValueDomain valueDomain = importValueDomain(base, base, dataType, "", cd)
                        valueDomain.save()
                        valueDomains << valueDomain
                    }

                    if (valueDomains.size()>0)
                    {
                        //Create the root value domain
                        vd = importValueDomain(name, description, null, "", cd)
                        //Add union of relationships
                        valueDomains.each {ValueDomain valueDomain ->
                            vd.addToUnionOf(valueDomain)
                            valueDomain.addToUnionOf(vd)
                            vd.save()
                            valueDomain.save()
                        }
                    }
                }
                return [vd, null]
            }
            else
            {
                //Check for list
                if ( simpleType.list != null)
                {
                    String base = simpleType.list.itemType
                    dataType = DataType.findByName(base)
                    if (dataType == null) {
                        dataType = simpleDataTypes.find{it.name==base}
                        if(dataType){
                            createSimpleType(importer, cd, dataType, simpleDataTypes)
                            dataType = DataType.findByName(base)
                        }else {
                            dataType = new DataType(name: base).save()
                        }
                    }
                    vd = importValueDomain(name, description, dataType, "", cd, Boolean.TRUE)
                    vd.save()
                    return [vd, dataType]
                }
            }
        }

    }
    private addMetadataToValueDomain (vd, XsdSimpleType simpleType){

        String pattern=""
        simpleType.restriction?.patterns?.each { XsdPattern xsdPattern ->
            if (pattern == "") {
                pattern += xsdPattern.value
            } else {
                pattern += ("|" + xsdPattern.value)

            }
        }
        def metadata = [minLength: simpleType.restriction?.minLength,
                        maxLength: simpleType.restriction?.maxLength,
                        lenght: simpleType.restriction?.length,
                        minInclusive: simpleType.restriction?.minInclusive,
                        maxInclusive: simpleType.restriction?.maxInclusive,
                        minExclusive: simpleType.restriction?.minExclusive,
                        maxExclusive: simpleType.restriction?.maxExclusive,
                        pattern: pattern
        ]


        vd = updateMetadata(metadata, vd)


    }



    def createModels(DataImport importer, ConceptualDomain conceptualDomain, ArrayList<XsdComplexType> complexDataTypes, ArrayList<XsdGroup> groups, Classification classification){

        complexDataTypes.each { XsdComplexType complexDataType ->
            //Create Model for each Group, Choice and Sequence.
            //Model model = matchOrCreateModel(importer, [name:complexDataType.name, description: complexDataType.description], cd, classification).save()
            def model = createModel([name:complexDataType.name, description: complexDataType.description], classification, conceptualDomain)
            if (model != null) println("Model: " + complexDataType.name)
            addModelToImport(importer, model)
        }

        groups.each{ XsdGroup group ->
            //Model model = matchOrCreateModel(importer, [name:group.name, description: group.description], cd, classification).save()
            def model = createModel([name:group.name, description: group.description], classification, conceptualDomain)
            addModelToImport(importer, model)
        }

    }

    def createCatalogueElements(DataImport importer, ArrayList<XsdElement> elements, ConceptualDomain conceptualDomain, Classification classification){


        // Extract all the DataElements
        elements.each { XsdElement element ->

            Model complexTypeModel = Model.findByName(element.type)
            if  (complexTypeModel == null) {

                //find or create the data element
                ValueDomain valueDomain = ValueDomain.findByName(element.type)
                def containingModels = Model.findAllWhere(name:element.section)
                def containingModel = containingModels.find{it.classifications.contains(classification)}

                def dataElements = DataElement.findByName(element.name)
                def dataElement
                dataElements.each{ DataElement de->
                    def classifications = de.classifications
                    if(classifications.find{it.id==classification.id} && de.valueDomain.id == valueDomain.id) dataElement = de
                }


                if (dataElement){
                    if(checkDataElementForChanges([name: element.name, description: element.description], [:],dataElement, classification)) {
                        dataElement = null
                    }else{
                        dataElement.addToClassifications(classification)
                        Relationship containedIn = dataElement.addToContainedIn(containingModel)
                        containedIn.ext.put("Min Occurs", element.minOccurs)
                        containedIn.ext.put("Max Occurs", element.maxOccurs)
                        containedIn.ext.put("type", "xs:element")
                        addModelToImport(importer, containingModel)
                    }
                }

                if (!dataElement) {
                    dataElement = new DataElement(name: element.name, description: element.description).save()
                    dataElement.addToClassifications(classification)
                    Relationship containedIn = dataElement.addToContainedIn(containingModel)
                    containedIn.ext.put("Min Occurs", element.minOccurs)
                    containedIn.ext.put("Max Occurs", element.maxOccurs)
                    containedIn.ext.put("type", "xs:element")
                    addModelToImport(importer, containingModel)
                    addUpdatedDataElements(importer, dataElement, containingModel, conceptualDomain)
                }

                dataElement.valueDomain = valueDomain
                dataElement.save()

                return dataElement

            }
            else
            {
                //create the model
                Model model = Model.findByName(element.name)
                if (model ==null) model = matchOrCreateModel(importer, [name:element.name, description: element.description], conceptualDomain, classification)
                Model parentNode = Model.findByName(element.section)
                if (parentNode!=null && model !=null) {
                    parentNode.addToParentOf(model)
                    model.addToParentOf(complexTypeModel)
                    model.save(flush:true)
                    println("ParentModel: " + element.section +  " Model: " + element.name + " ComplexModel: " + element.type)
                }
            }
        }

    }

    def createCatalogueAttributes(DataImport importer, ArrayList<XsdAttribute> attributes, ConceptualDomain conceptualDomain, Classification classification){

        // Extract all the DataElements
        attributes.each { XsdAttribute attribute ->

            //find or create the data attribute
            ValueDomain valueDomain = ValueDomain.findByName(attribute.type)
            def containingModels = Model.findAllWhere(name: attribute.section)
            def containingModel = containingModels.find { it.classifications.contains(classification) }

            def dataElements = DataElement.findByNameAndValueDomain(attribute.name, valueDomain)
            def dataElement
            dataElements.each{ DataElement de->
                def classifications = de.classifications
                if(classifications.find{it.id==classification.id}) dataElement = de
            }


            def metaDataParams = [:]
            if(attribute?.defaultValue) metaDataParams.put("defaultValue", attribute?.defaultValue)
            if(attribute?.fixed) metaDataParams.put("fixed", attribute?.fixed)
            if(attribute?.form) metaDataParams.put("form", attribute?.form)
            if(attribute?.id) metaDataParams.put("id", attribute?.id)
            if(attribute?.ref) metaDataParams.put("ref", attribute?.ref)

            if (dataElement){
                if(checkDataElementForChanges([name: attribute.name, description: attribute.description], metaDataParams,dataElement, classification)) {
                    dataElement = null
                }else{
                    dataElement.addToClassifications(classification)
                    Relationship containedIn = dataElement.addToContainedIn(containingModel)
                    containedIn.ext.put("type", "xs:attribute")
                    if(attribute?.use) containedIn.ext.put("use", attribute?.use)
                }
            }

            if (!dataElement) {
                dataElement = new DataElement(name: attribute.name, description: attribute.description).save()
                if(attribute?.defaultValue) dataElement.ext.put("defaultValue", attribute?.defaultValue)
                if(attribute?.fixed) dataElement.ext.put("fixed", attribute?.fixed)
                if(attribute?.form) dataElement.ext.put("form", attribute?.form)
                if(attribute?.id) dataElement.ext.put("id", attribute?.id)
                if(attribute?.ref) dataElement.ext.put("ref", attribute?.ref)
                dataElement.save()
                dataElement.addToClassifications(classification)
                Relationship containedIn = dataElement.addToContainedIn(containingModel)
                containedIn.ext.put("type", "xs:attribute")
                if(attribute?.use) containedIn.ext.put("use", attribute?.use)
                addModelToImport(importer, containingModel)
                addUpdatedDataElements(importer, dataElement, containingModel, conceptualDomain)
            }

            dataElement.valueDomain = valueDomain
            dataElement.save()

            return dataElement


        }
    }


}
