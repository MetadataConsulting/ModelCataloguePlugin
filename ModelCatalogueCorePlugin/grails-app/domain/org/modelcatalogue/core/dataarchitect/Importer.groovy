package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.ValueDomain

class Importer {

    def publishedElementService

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]

    //TODO replace this with a call to the published element domain class constraint for model catalogue id directly
    private static final REGEX = 'MC_\\d+_\\d+'

    Collection<ImportRow> pendingAction = []
    Collection<ImportRow> importQueue = []
    Collection<ImportRow> imported = []
    Map importInfo

    static constraints = {
        imported nullable:true
        pendingAction nullable:true
        importQueue nullable:true
    }

    def void importAll(Collection<ImportRow> rows){
        rows.each{ ImportRow row ->
            importRow(row)
        }
    }

    def void importRow(ImportRow row){
        row = validateRow(row)
        (row.rowActions)? pendingAction.add(row) : importQueue.add(row)
    }

    def void ingestImportQueue(){

        def queue = importQueue.iterator()
        while (queue.hasNext()) {
            ImportRow row = queue.next()
            if (!row.rowActions) {
                ingestRow(row)
                queue.remove()
            }
        }

    }


    def void ingestRow(ImportRow row){
        def conceptualDomain, valueDomain, model, dataElement, dataType, measurementUnit

        //IMPORT data type
        dataType = importDataType(row.dataElementName, row.dataType)

        //IMPORT conceptual domain
        //TODO handle possible conceptual domains i.e. do a like search and have decision actions returned
        conceptualDomain = importConceptualDomain(row.conceptualDomainName, row.conceptualDomainDescription)

        //IMPORT models/parent models etc.
        model = importModels(row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, row.parentModels, conceptualDomain)

        measurementUnit = importMeasurementUnit([name: row.measurementUnitName, symbol: row.measurementSymbol])

        //IMPORT data element
        dataElement = importDataElement([name: row.dataElementName, description: row.dataElementDescription, modelCatalogueId: row.dataElementCode], row.metadata, model)

        //IMPORT value domain
        if(dataType) {
            def description = row.dataType.take(2000)
            valueDomain = importValueDomain([name: row.dataElementName.replaceAll("\\s", "_"), description: description, dataType: dataType, measurementUnit: measurementUnit], conceptualDomain, dataElement)
        }

    }


    def importDataType(String name, String data) {

        //default data type to return is the string data type
        def dataTypeReturn

        data.each { line ->
            String[] lines = line.split("\\r?\\n");

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
            }
        }

        if (!dataTypeReturn) { dataTypeReturn = DataType.findByNameIlike(data) }

        return dataTypeReturn
    }

    def importMeasurementUnit(Map params){
        MeasurementUnit mu = MeasurementUnit.findByNameIlike(params.name)
        if(!mu){ mu = new MeasurementUnit(params).save()}
        return mu
    }


    def importConceptualDomain(String name, String description) {
        name = name.trim()
        ConceptualDomain cd = ConceptualDomain.findByName(name)
        //Collection<ConceptualDomain> possibleConceptualDomains

        //TODO possible conceptualDomains

        if(!cd){ cd = new ConceptualDomain(name: name, description: description).save() }

        return cd
    }



    def importModels(String parentCode, String parentName, String modelCode, String modelName, ArrayList modelPath, ConceptualDomain conceptualDomain) {

        if (parentCode) { parentCode = parentCode.trim()}
        if (modelCode) { modelCode = modelCode.trim()}
        if (parentName) { parentName = parentName.trim()}
        if (modelName) { modelName = modelName.trim()}

        Model model = Model.findByModelCatalogueId(modelCode)
        Model parentModel = Model.findByModelCatalogueId(parentCode)

        //TODO return a row action
        //if there are no models then try to match the model using the parentChild path to the model and the name of the model
        if(!model || !parentModel){
            if(!modelPath){modelPath = []}
            modelPath.add(parentName)
            modelPath.add(modelName)
            model = matchOrCreateModel(modelPath, [name: parentName, modelCatalogueId: (parentCode)?parentCode:null], [name: modelName, modelCatalogueId: (modelCode)?modelCode:null], conceptualDomain)
        }else{
            model.addToChildOf(parentModel)
        }

        return model

    }

    protected matchOrCreateModel(ArrayList modelPath, Map parentParams, Map modelParams, ConceptualDomain conceptualDomain){

        //the final model we want to return i.e. the containing model
        Model modelToReturn

        //iterate through the model path i.e. ANIMAL - MAMMAL - DOG - POODLE and create models for each of these if they don't exist,
        // otherwise find them and create a parentChild relationship
        modelPath.inject { parentName, childName ->
            Collection namedChildren
            Model match

            //if there isn't a name for the child return the parentName
            if (childName.equals("") || childName == null) { return parentName }

            //see if there are any models with the name of the child
            namedChildren = Model.findAllWhere("name": childName)

            //see if there are any models with this name that have the same parentName
            if (namedChildren.size() > 0) {
                namedChildren.each { Model childModel ->
                    if (childModel.childOf.collect { it.name }.contains(parentName)) { match = childModel }
                }
            }

            //if there isn't a matching model with the same name and parentName
            if (!match) {
                //new Model('name': name, 'parentName': parentName).save()
                Model child
                Model parent

                //create the child model
                if(modelParams.name == childName){
                    child = new Model(modelParams).save()
                }else{
                    child = new Model('name': childName).save()
                }

                child.addToHasContextOf(conceptualDomain)

                modelToReturn = child

                //see if the parent model exists
                parent = Model.findWhere("name": parentName)

                //FIXME we should probably have unique names for models (or codes)
                // or at least within conceptual domains
                // or we need to have a way of choosing the model parent to use
                // at the moment it just uses the first one Model that is returned

                if (!parent) {
                    if(parentParams.name == parentName){
                        parent = new Model(parentParams).save()
                    }else{
                        parent = new Model('name': parentName).save()
                    }
                    parent.addToHasContextOf(conceptualDomain)
                }

                child.addToChildOf(parent)
                child.name

                //add the parent child relationship between models

            } else {
                modelToReturn = match
                match.name
            }
        }

        modelToReturn

    }

    protected ValueDomain importValueDomain(Map params, ConceptualDomain cd){
        def vd = new ValueDomain(params).save(failOnError: true);

        vd.addToIncludedIn(cd)
        return vd
    }

    protected ValueDomain importValueDomain(Map params, ConceptualDomain cd, DataElement de){
        def vd = new ValueDomain(params).save(failOnError: true);

        vd.addToIncludedIn(cd)
        de.addToInstantiatedBy(vd)

        return vd
    }


//    protected DataElement importDataElement(Map params){
//        def de = new DataElement(params).save()
//        return de
//    }
//
//    protected DataElement importDataElement(Map params, Map metadata){
//        def de = new DataElement(params).save()
//
//        metadata.each { key, value ->
//            if (key) { key = key.take(255)}
//            if (value) {value = value.take(255)}
//            de.ext.put(key, value)
//        }
//
//        return de
//    }


    protected DataElement updateDataElement(Map params, DataElement dataElement){
        if(dataElement.name!=params.name || dataElement.description!=params.desription){
            publishedElementService.archiveAndIncreaseVersion(dataElement)
            dataElement.name = params.name
            dataElement.description = params.description
            dataElement.save()
        }
        return dataElement
    }


    protected DataElement importDataElement(Map params, Map metadata, Model model){

        //find out if data element exists using unique code
        DataElement de = DataElement.findByModelCatalogueId(params.modelCatalogueId)
        if(de){ de = updateDataElement(params, de) }

        //find if data element exists using name and containing model
        if(!de){
            def nameDE = DataElement.findByName(params.name)
            if(nameDE && nameDE.containedIn.contains(model)){
                de = nameDE
                if(de){ de = updateDataElement(params, de) }
            }
        }


        if(!de) {
            params.put('status', PublishedElementStatus.FINALIZED)
            de = new DataElement(params).save()
        }

        metadata.each { key, value ->
            if (key) { key = key.take(255)}
            if (value) {value = value.take(255)}
            de.ext.put(key, value)
        }

        //TODO check whether already contained and then if it isn't update the model (i.e. update version etc.)

        de.addToContainedIn(model)

        return de
    }


    private ImportRow validateRow(ImportRow row){

        if(!row.dataElementName){
            RowAction action = new RowAction(field: "dataElementName", action: "please enter data element name to import row", actionType: ActionType.RESOLVE_ERROR)
            row.rowActions.add(action)
        }

        if(!row.conceptualDomainName){
            RowAction action = new RowAction(field: "conceptualDomainName", action: "please enter conceptual domain name to import row", actionType: ActionType.RESOLVE_ERROR)
            row.rowActions.add(action)
        }

        if(!row.containingModelName){
            RowAction action = new RowAction(field: "containingModelName", action: "please complete the containing model name to import row", actionType: ActionType.RESOLVE_ERROR)
            row.rowActions.add(action)
        }

        if(row.dataElementCode){
            if(!row.dataElementCode.matches(REGEX)){
                RowAction action = new RowAction(field: "dataElementCode", action: "the model catalogue code for the data element is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR)
                row.rowActions.add(action)
            }
        }

        if(row.containingModelCode){
            if(!row.containingModelCode.matches(REGEX)){
                RowAction action = new RowAction(field: "containingModelCode", action: "the model catalogue code for the containing model is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR)
                row.rowActions.add(action)
            }
        }

        if(row.parentModelCode){
            if(!row.parentModelCode.matches(REGEX)){
                RowAction action = new RowAction(field: "parentModelCode", action: "the model catalogue code for the parent model is invalid, please action to import row", actionType: ActionType.RESOLVE_ERROR)
                row.rowActions.add(action)
            }
        }

        if(!row.dataType){
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
