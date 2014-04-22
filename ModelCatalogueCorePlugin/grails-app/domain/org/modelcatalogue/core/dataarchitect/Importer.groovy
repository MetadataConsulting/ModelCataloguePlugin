package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement

class Importer {

    //TODO replace this with a call to the published element domain class constraint for model catalogue id directly
    private static final REGEX = 'MC_\\d+_\\d+'

    Collection<ImportRow> pendingAction = []
    Collection<ImportRow> importQueue = []
    Collection<ImportRow> imported = []

    static constraints = {
        imported nullable:true
        pendingAction nullable:true
        importQueue nullable:true
    }

    def importAll(Collection<ImportRow> rows){
        rows.each{ ImportRow row ->
            importRow(row)
        }
    }

    def ingestImportQueue(){
        importQueue.each{ ImportRow row ->
            if(!row.rowActions){
                ingestRow(row)
            }
        }
    }

    private void ingestRow(ImportRow row){
        def conceptualDomain, valueDomain, model, dataElement, dataType

        //IMPORT conceptual domain
        //TODO handle possible conceptual domains i.e. do a like search and have some row actions returned
        conceptualDomain = importConceptualDomain(row.conceptualDomainName, row.conceptualDomainDescription)

        //IMPORT models/parent models etc.
        model = importModels(row.parentModelCode, row.parentModelName, row.containingModelCode, row.containingModelName, conceptualDomain)

        //IMPORT data type
        dataType = importDataType()

        //IMPORT value domain
        //IMPORT data element
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

        if (!dataTypeReturn) {
            dataTypeReturn = (DataType.findByNameLike(name))
        }

        return dataTypeReturn
    }


    def importConceptualDomain(String name, String description) {
        name = name.trim()
        ConceptualDomain cd = ConceptualDomain.findByName(name)
        Collection<ConceptualDomain> possibleConceptualDomains

        //TODO possible conceptualDomains
//        if (!cd) {
//            possibleConceptualDomains = ConceptualDomain.findAllByNameLike(name)
//            return possibleConceptualDomains
//        }

        if(!cd && !possibleConceptualDomains){ cd = new ConceptualDomain(name: name, description: description).save() }

        return cd
    }



    def importModels(String parentCode, String parentName, String modelCode, String modelName, ConceptualDomain conceptualDomain) {

        //TODO if there is no model catalogue id do a like search and return a row action
        if (parentCode) { parentCode = parentCode.trim()}
        if (modelCode) { modelCode = modelCode.trim()}
        Model model = Model.findByModelCatalogueId(modelCode)
        Model parentModel = Model.findByModelCatalogueId(parentCode)
        if(!model){ model = new Model(name: modelName, modelCatalogueId: modelCode).save()}
        if(!parentModel && parentName){ parentModel = new Model(name: parentName, modelCatalogueId: parentCode).save()}
        if(model){model.addToHasContextOf(conceptualDomain)}
        if(parentModel){parentModel.addToHasContextOf(parentModel)}
        if(parentModel && model){ model.addToChildOf(parentModel)}

        return model

    }


    //TODO make private?
    void importRow(ImportRow row){
        row = validateRow(row)
        (row.rowActions)? pendingAction.add(row) : importQueue.add(row)
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

}
