package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.ValueDomain

class Importer {

    def publishedElementService

    private static final QUOTED_CHARS = ["\\": "&#92;", ":": "&#58;", "|": "&#124;", "%": "&#37;"]

    //TODO replace this with a call to the published element domain class constraint for model catalogue id directly
    private static final REGEX = '(?i)MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})_\\d+'

    Collection<ImportRow> pendingAction = []
    Collection<ImportRow> importQueue = []
    Collection<ImportRow> imported = []
    Collection<Integer> modelIds = []
    ArrayList parentModels

    static constraints = {
        imported nullable: true
        pendingAction nullable: true
        importQueue nullable: true
    }

    def void importAll(Collection<ImportRow> rows) {
        rows.each { ImportRow row ->
            importRow(row)
        }
    }

    def void importRow(ImportRow row) {
        row = validateRow(row)
        (row.rowActions) ? pendingAction.add(row) : importQueue.add(row)
    }

    def void ingestImportQueue() {
        def queue = importQueue.iterator()
        while (queue.hasNext()) {
            ImportRow row = queue.next()
            if (!row.rowActions) {
                ingestRow(row)
                queue.remove()
            }
        }
    }

    def void actionPendingModels() {
        modelIds.each { modelId ->
            def model = Model.get(modelId)
            def pendingDataElements = model.contains.findAll { it.status == PublishedElementStatus.PENDING }
            if (pendingDataElements) {
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
            model.save(failOnError:true)
        }
    }


    def void ingestRow(ImportRow row) {
        def conceptualDomain, model, dataType, measurementUnit
        dataType = (row.dataType)?importDataType(row.dataElementName, row.dataType):null
        conceptualDomain = importConceptualDomain(row.conceptualDomainName, row.conceptualDomainDescription)
        model = importModels(row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, conceptualDomain)
        measurementUnit = importMeasurementUnit([name: row.measurementUnitName, symbol: row.measurementSymbol])

        if (dataType) {
            importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model, [name: row.dataElementName.replaceAll("\\s", "_"), description: row.dataType.take(2000), dataType: dataType, measurementUnit: measurementUnit], conceptualDomain)
        } else {
            importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model)
        }
    }


    def importDataType(String name, String data) {

        def dataTypeReturn
        String[] lines = data.split("\\r?\\n");

//          if there is more than one line assume that the data type is enumerated and parse enumerations
//          the script accepts enumerations in the format
//          01=theatre and recovery
//          02=recovery only (usrd as a temporary ccu)
//          03=other ward

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
                    dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
                }

            }
            //}
        }

        if (!dataTypeReturn) {
            dataTypeReturn = DataType.findByNameIlike(data)
        }

        return dataTypeReturn
    }

    def importMeasurementUnit(Map params) {
        MeasurementUnit mu = MeasurementUnit.findByNameIlike(params.name)
        if (!mu) { mu = new MeasurementUnit(params).save() }
        return mu
    }


    def importConceptualDomain(String name, String description) {
        name = name.trim()
        ConceptualDomain cd = ConceptualDomain.findByName(name)
        if (!cd) { cd = new ConceptualDomain(name: name, description: description).save() }
        return cd
    }


    def importModels(String parentCode, String parentName, String modelCode, String modelName, ConceptualDomain conceptualDomain) {

        if (parentCode) { parentCode = parentCode.trim() }
        if (modelCode) { modelCode = modelCode.trim() }
        if (parentName) { parentName = parentName.trim() }
        if (modelName) { modelName = modelName.trim() }

        Model model = Model.findByModelCatalogueId(modelCode)
        Model parentModel = Model.findByModelCatalogueId(parentCode)

        ArrayList modelPath = []
        modelPath.addAll(parentModels)
        modelPath.add(parentName)
        modelPath.add(modelName)
        //TODO return a row action
        //if there are no models then try to match the model using the parentChild path to the model and the name of the model
        if (!model || !parentModel) {
            model = matchOrCreateModel(modelPath, [name: parentName, modelCatalogueId: (parentCode) ? parentCode : null], [name: modelName, modelCatalogueId: (modelCode) ? modelCode : null], conceptualDomain)
        } else {
            model = addModelToImport(model)
            parentModel = addModelToImport(parentModel)
            model.addToChildOf(parentModel)
        }

        return model

    }


    protected Model addModelToImport(Model model) {
        if (!modelIds.contains(model.id)) {
            model.status = PublishedElementStatus.PENDING
            model.save()
            modelIds.add(model.id)
        }
        return model
    }

    protected matchOrCreateModel(ArrayList modelPath, Map parentParams, Map modelParams, ConceptualDomain conceptualDomain) {

        //the final model we want to return i.e. the containing model
        Model modelToReturn

        //iterate through the model path i.e. ANIMAL - MAMMAL - DOG - POODLE and create models for each of these if they don't exist,
        // otherwise find them and create a parentChild relationship
        modelPath.inject { String parentName, String childName ->
            def namedChildren = []
            def match

            //if there isn't a name for the child return the parentName
            if (!childName) { return parentName}
            parentName = parentName.trim()
            childName = childName.trim()

            namedChildren = Model.findAllByName(childName)
            namedChildren.each { Model childModel ->
                if (childModel.childOf.collect { it.name }.contains(parentName)) {
                    match = childModel
                }
            }

            //if there isn't a matching model with the same name and parentName
            if (!match) {
                def child, parent
                //create the child model
                if (modelParams.name == childName) {
                    child = new Model(modelParams).save(failOnError: true)
                } else if (parentParams.name == childName) {
                    child = new Model(parentParams).save(failOnError: true)
                } else {
                    child = new Model('name': childName).save(failOnError: true)
                }
                child.addToHasContextOf(conceptualDomain)
                child = addModelToImport(child)
                modelToReturn = child

                //see if the parent model exists
                parent = Model.findWhere("name": parentName)
                //create the parent model
                if (!parent) {
                    if (parentParams.name == parentName) {
                        parent = new Model(parentParams).save()
                    } else {
                        parent = new Model('name': parentName).save()
                    }
                    parent.addToHasContextOf(conceptualDomain)
                }
                child.addToChildOf(parent)
                parent = addModelToImport(parent)
                return child.name

                //add the parent child relationship between models

            } else {
                match = addModelToImport(match)
                modelToReturn = match
                return match.name
            }
        }

        modelToReturn

    }


    protected Boolean checkDataElementForChanges(Map params, Map metadata, DataElement dataElement) {
        Boolean hasDataElementChanged = false
        if (dataElement.name != params.name || dataElement.description != params.description) {
            return true
        }
        metadata.each { key, value ->
            if (key) {
                key = key.take(255)
            }
            value = (value && value != "") ? value.take(255) : null
            if (dataElement.ext.get(key) != value) {
                hasDataElementChanged = true
            }
        }
        return hasDataElementChanged
    }

    protected DataElement updateMetadata(Map metadata, DataElement dataElement) {
        metadata.each { key, value ->
            if (key) {
                key = key.take(255)
            }
            if (value) {
                value = value.take(255)
            }
            dataElement.ext.put(key, value)
        }
        dataElement.save()
        dataElement
    }

    //update data element without value domain info
    protected DataElement updateDataElement(Map params, DataElement dataElement, Map metadata) {
        if (checkDataElementForChanges(params, metadata, dataElement)) {
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.name = params.name
            dataElement.description = params.description
            dataElement.save()
            dataElement = updateMetadata(metadata, dataElement)
        }
        return dataElement
    }

    //update data element given value domain info
    protected DataElement updateDataElement(Map params, DataElement dataElement, Map vdParams, ConceptualDomain cd, Map metadata) {
        if (checkDataElementForChanges(params, metadata, dataElement)) {
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.refresh()
            dataElement.name = params.name
            dataElement.description = params.description
            dataElement.save()
            dataElement = updateMetadata(metadata, dataElement)
            ArrayList instantiatedBy = dataElement.instantiatedBy.findAll { it.includedIn.contains(cd) }

            if (!instantiatedBy) {
                importValueDomain(vdParams, dataElement, cd)
            } else {
                ValueDomain vd = instantiatedBy.first()
                if (vd.name != vdParams.name || vd.dataType != vdParams.dataType || vd.unitOfMeasure != vdParams.unitOfMeasure) {
                    //remove the old one (will still be in the archived one)
                    vd.removeFromInstantiates(dataElement)
                    //see if there is one that matches or create a new one
                    importValueDomain(vdParams, dataElement, cd)
                }
            }
        }
        return dataElement
    }

    protected ValueDomain importValueDomain(Map vdParams, DataElement dataElement, ConceptualDomain cd) {
        ValueDomain vd = ValueDomain.findByDataTypeAndUnitOfMeasure(vdParams.dataType, vdParams.unitOfMeasure)
        if (!vd) {
            vd = new ValueDomain(vdParams).save()
        }
        vd.addToIncludedIn(cd)
        vd.addToInstantiates(dataElement)
        vd.save()
    }

    protected DataElement importDataElement(Map params, Map metadata, Model model, Map vdParams, ConceptualDomain cd) {

        //find out if data element exists using unique code
        DataElement de = DataElement.findByModelCatalogueId(params.modelCatalogueId)
        if (de) {
            de = updateDataElement(params, de, vdParams, cd, metadata)
        }

        //find if data element exists using name and containing model
        if (!de) {
            def nameDE = DataElement.findByName(params.name)
            if (nameDE && nameDE.containedIn.contains(model)) {
                de = nameDE
                if (de) { de = updateDataElement(params, de, vdParams, cd, metadata)  }
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
        if (de) { de = updateDataElement(params, de, metadata) }

        //find if data element exists using name and containing model
        if (!de) {
            def nameDE = DataElement.findByName(params.name)
            if (nameDE && nameDE.containedIn.contains(model)) {
                de = nameDE
                if (de) {
                    de = updateDataElement(params, de, metadata)
                }
            }
        }

        if (!de) {
            params.put('status', PublishedElementStatus.FINALIZED)
            de = new DataElement(params).save()
            de = updateMetadata(metadata, de)
        }

        de.addToContainedIn(model)

        return de
    }


    private ImportRow validateRow(ImportRow row) {

        if (!row.dataElementName) {
            RowAction action = new RowAction(field: "dataElementName", action: "please enter data element name to import row", actionType: ActionType.RESOLVE_ERROR)
            row.rowActions.add(action)
        }

        if (!row.conceptualDomainName) {
            RowAction action = new RowAction(field: "conceptualDomainName", action: "please enter conceptual domain name to import row", actionType: ActionType.RESOLVE_ERROR)
            row.rowActions.add(action)
        }

        if (!row.containingModelName) {
            RowAction action = new RowAction(field: "containingModelName", action: "please complete the containing model name to import row", actionType: ActionType.RESOLVE_ERROR)
            row.rowActions.add(action)
        }

        if (row.dataElementCode) {
            if (!row.dataElementCode.matches(REGEX)) {
                RowAction action = new RowAction(field: "dataElementCode", action: "the model catalogue code for the data element is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR)
                row.rowActions.add(action)
            }
        }

        if (row.containingModelCode) {
            if (!row.containingModelCode.matches(REGEX)) {
                RowAction action = new RowAction(field: "containingModelCode", action: "the model catalogue code for the containing model is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR)
                row.rowActions.add(action)
            }
        }

        if (row.parentModelCode) {
            if (!row.parentModelCode.matches(REGEX)) {
                RowAction action = new RowAction(field: "parentModelCode", action: "the model catalogue code for the parent model is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR)
                row.rowActions.add(action)
            }
        }

        if (!row.dataType) {
            RowAction action = new RowAction(field: "dataType", action: "the row does not contain a data type therefore will not be associated with a value domain, is this the expected outcome?", actionType: ActionType.DECISION)
            row.rowActions.add(action)
        }

        return row
    }

    protected String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

}
