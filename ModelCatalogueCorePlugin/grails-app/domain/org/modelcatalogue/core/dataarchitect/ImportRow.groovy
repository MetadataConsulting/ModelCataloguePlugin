package org.modelcatalogue.core.dataarchitect

/**
 * Created by adammilward on 17/04/2014.
 */
class ImportRow {
    String dataElementCode
    String dataElementName
    String dataElementDescription
    String conceptualDomainName
    String conceptualDomainDescription
    String dataType
    String parentModelName
    String parentModelCode
    String containingModelName
    String containingModelCode
    String measurementUnitName
    String measurementSymbol
    Map metadata

    static hasMany = [rowActions: RowAction]

    static constraints = {
        dataElementCode nullable: true
        dataElementName nullable: true
        dataElementDescription nullable: true
        conceptualDomainName nullable: true
        conceptualDomainDescription  nullable: true
        dataType nullable: true
        parentModelName nullable: true
        parentModelCode nullable: true
        containingModelName nullable: true
        containingModelCode nullable: true
        measurementUnitName nullable: true
        measurementSymbol nullable: true
        metadata nullable: true
        rowActions  nullable: true
    }

    def resolveAction(String field, ActionType actionType){
        RowAction actionToResolve = rowActions.find{it.field == field && it.actionType==actionType}
        if(actionToResolve){
            rowActions.remove(actionToResolve)
        }
    }

    def resolveAll(){
        def errors = rowActions.find{it.actionType==ActionType.RESOLVE_ERROR}
        if(!errors) {
            rowActions = []
        }
    }
}
