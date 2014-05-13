package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.marshalling.EnumeratedTypeMarshaller

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

    def void addAll(Collection<ImportRow> rows) {
        rows.each { ImportRow row ->
            addRow(row)
        }
    }

    def void addRow(ImportRow row) {
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
            importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model, [name: row.dataElementName.replaceAll("\\s", "_"), description: row.dataType.toString().take(2000), dataType: dataType, measurementUnit: measurementUnit], conceptualDomain)
        } else {
            importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model)
        }
    }


    def importDataType(String name, String data) {


        def dataTypeReturn = DataType.findByName(data)
        //TODO: create action: do you want to use name similar
        if(!dataTypeReturn){
            dataTypeReturn = DataType.findByNameIlike(data)
        }
        if (!dataTypeReturn) {
            if (data.contains("|")) {
                try {
                    data = sortEnumAsString(data)
                } catch (Exception e) {
                    return null
                }
                dataTypeReturn = EnumeratedType.findWhere(enumAsString: data)
                if (!dataTypeReturn) {
                    //TODO: create action: do you want to create enum
                    dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumAsString: data).save()
                }
            } else if (data.contains("\\n") || data.contains("\\r")) {
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
                            //TODO: create action: do you want to create enum
                            dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
                        }
                    }
                }
            }

        }

        //TODO: action do you want to create a new data type
//        if (!dataTypeReturn) { dataTypeReturn = new DataType(data).save() }

        return dataTypeReturn
    }

    def importMeasurementUnit(Map params) {
        MeasurementUnit mu
        if(params.name && params.symbol) {
            mu = MeasurementUnit.findByNameAndSymbol(params.name, params.symbol)
        }else {
            if (!mu) {
                //TODO: need to create an action i.e. is this the unit?
                mu = MeasurementUnit.findBySymbol(params.symbol)
            }
            if (!mu) {
                //TODO: need to create an action i.e. is this the unit?
                mu = MeasurementUnit.findByNameIlike(params.name)
            }
        }
//TODO: do you want to create a new measurement unit?
//        if (!mu) { mu = new MeasurementUnit(params).save() }

        return mu
    }


    def importConceptualDomain(String name, String description) {
        name = name.trim()
        ConceptualDomain conceptualDomain = ConceptualDomain.findByName(name)
        //TODO: is this the conceptual domain you want to use
        //if (!conceptualDomain) { conceptualDomain = ConceptualDomain.findByNameIlike(name)}

        //TODO: do you want to create a new conceptual domain
        if (!conceptualDomain) { conceptualDomain = new ConceptualDomain(name: name, description: description).save() }
        return conceptualDomain
    }


    def importModels(String parentCode, String parentName, String modelCode, String modelName, ConceptualDomain conceptualDomain) {
        if (parentCode) { parentCode = parentCode.trim() }
        if (modelCode) { modelCode = modelCode.trim() }
        if (parentName) { parentName = parentName.trim() }
        if (modelName) { modelName = modelName.trim() }
        Model model = Model.findByModelCatalogueId(modelCode)
        Model parentModel = Model.findByModelCatalogueId(parentCode)
        ArrayList modelPath = []
        if(parentModels){modelPath.addAll(parentModels)}
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
            def match = null
            //if there isn't a name for the child return the parentName
            if (!childName) { return parentName}
            if(parentName){ parentName = parentName.trim()}
            if(childName){ childName = childName.trim() }
            namedChildren = Model.findAllByName(childName)
            namedChildren.each { Model childModel ->
                if (childModel.childOf.collect { it.name }.contains(parentName) && childModel.hasContextOf.contains(conceptualDomain)) {
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
                //TODO I'm sure we can clean this up
                //it would be nice to create methods that allowed you to do a findAllByNameAndContext etc.
                def namedParents = Model.findAllByName(parentName)
                namedParents.each{ Model p ->
                    if(p.hasContextOf.contains(conceptualDomain)) {parent = p}
                }

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
            } else {
                match = addModelToImport(match)
                modelToReturn = match
                return match.name
            }
        }

        modelToReturn

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


    protected Boolean checkValueDomainForChanges(Map params, ValueDomain valueDomain, ConceptualDomain cd){
        //ValueDomain vd = ValueDomain.findByDataTypeAndUnitOfMeasure(vdParams.dataType, params.unitOfMeasure)
        if(valueDomain) {
            if (!valueDomain.includedIn.contains(cd)) { return true }
            if (valueDomain.unitOfMeasure != params.unitOfMeasure) { return true }
            if (params.dataType instanceof EnumeratedType) {
                if (valueDomain.dataType.enumAsString != params.dataType.enumAsString) { return true }
            } else if (valueDomain.dataType != params.dataType) { return true }
        }else{
            if(params.dataType||params.measurementUnit){return true}
        }
        return false
    }


    //update data element given value domain info
    protected DataElement updateDataElement(Map params, DataElement dataElement, Map vdParams, ConceptualDomain cd, Map metadata) {
        Boolean dataElementChanged = checkDataElementForChanges(params, metadata, dataElement)
        ValueDomain vd = dataElement.instantiatedBy.find { it.includedIn.contains(cd) }
        Boolean valueDomainChanged = checkValueDomainForChanges(vdParams, vd, cd)

        if (dataElementChanged || valueDomainChanged) {
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.refresh()

            if(dataElementChanged) {
                dataElement.name = params.name
                dataElement.description = params.description
                dataElement.save()
                dataElement = updateMetadata(metadata, dataElement)
            }

            if (valueDomainChanged) {
                //remove the old one (will still be in the archived one)
                if (vd) { vd.removeFromInstantiates(dataElement) }
                //see if there is one that matches or create a new one
                importValueDomain(vdParams, dataElement, cd)
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
        if (de) { de = updateDataElement(params, de, vdParams, cd, metadata) }

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
                if (de) { de = updateDataElement(params, de, metadata) }
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

    protected static String sortEnumAsString(String s) {
            if (s == null) return null
            Map<String, String> ret = [:]
            s.split(/\|/).each { String part ->
                if (!part) return
                String[] pair = part.split(/:/)
                if (pair.length != 2) throw new IllegalArgumentException("Wrong enumerated value '$part' in encoded enumeration '$s'")
                ret[unquote(pair[0])] = unquote(pair[1])
            }
            s = ret.sort() collect { key, val ->
                "${quote(key)}:${quote(val)}"
            }.join('|')
        s
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
