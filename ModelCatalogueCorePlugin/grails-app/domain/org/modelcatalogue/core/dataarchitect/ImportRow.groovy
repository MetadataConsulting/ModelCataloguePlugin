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
        dataElementCode nullable: true, maxSize: 255
        dataElementName nullable: true, maxSize: 255
        dataElementDescription nullable: true, maxSize: 2000
        conceptualDomainName nullable: true, maxSize: 255
        conceptualDomainDescription  nullable: true, maxSize: 2000
        dataType nullable: true, maxSize: 255
        parentModelName nullable: true, maxSize: 255
        parentModelCode nullable: true, maxSize: 255
        containingModelName nullable: true, maxSize: 255
        containingModelCode nullable: true, maxSize: 255
        measurementUnitName nullable: true, maxSize: 255
        measurementSymbol nullable: true, maxSize: 255
        metadata nullable: true
        rowActions  nullable: true
    }

    def resolveAction(String field, ActionType actionType){
        def actionsToResolve = rowActions.findAll{it.field == field && it.actionType==actionType}
        actionsToResolve.each { RowAction actionToResolve ->
            if (actionToResolve) {
                rowActions.remove(actionToResolve)
            }
        }
    }

    def resolveAll(){
        def errors = rowActions.find{it.actionType==ActionType.RESOLVE_ERROR}
        if(!errors) {
            rowActions = []
        }
    }
}
