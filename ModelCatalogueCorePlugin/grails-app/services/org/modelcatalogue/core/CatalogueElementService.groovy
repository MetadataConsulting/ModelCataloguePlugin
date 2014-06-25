package org.modelcatalogue.core

class CatalogueElementService {

    static transactional = false

    def getContainingModel(DataElement dataElement){
        if(dataElement.containedIn) {
            return dataElement.containedIn.first()
        }
        return
    }

    def getParentModel(DataElement dataElement){
        Model containingModel = getContainingModel(dataElement)
        if(containingModel.childOf) {
            return containingModel.childOf.first()
        }
        return
    }

    def getValueDomain(DataElement dataElement){
        if(dataElement.instantiatedBy) {
            return dataElement.instantiatedBy.first()
        }
        return
    }

    def getDataType(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            DataType dataType = valueDomain.dataType
            if (dataType instanceof EnumeratedType) {
                return dataType.enumAsString
            }
            return dataType.name
        }
        return
    }

    def getUnitOfMeasure(DataElement dataElement){
        ValueDomain valueDomain = getValueDomain(dataElement)
        if(valueDomain) {
            MeasurementUnit unitOfMeasure = valueDomain?.unitOfMeasure
            return unitOfMeasure?.name
        }
        return
    }
}
