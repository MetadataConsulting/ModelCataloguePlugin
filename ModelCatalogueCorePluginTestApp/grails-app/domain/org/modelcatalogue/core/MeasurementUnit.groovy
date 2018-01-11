package org.modelcatalogue.core

/**
 * Measurement units are units such as MPH, cm3, cm2, m etc.
 * They are used by value domains to instantiate a data element
 */
class MeasurementUnit extends CatalogueElement {

    String symbol

    static constraints = {
        symbol nullable: true, size: 1..100
    }

    static transients = ['primitiveTypes']

    @Override
    Map<CatalogueElement, Object> manualDeleteRelationships(DataModel toBeDeleted) {
        primitiveTypes.collectEntries {
            if (toBeDeleted) {
                // if DataModel is going to be deleted, then MeasurementUnit needs to be from same DataModel
                if (it.dataModel != this.dataModel) {
                    return [(it): it.dataModel]
                } else {
                    return [:]
                }
            } else {
                // if deletes MeasurementUnit, it should not be used anywhere
                return [(it): null]
            }
        }
    }

    @Override
    Long getFirstParentId() {
        return getPrimitiveTypes().find { it.getDataModelId() == getDataModelId() }?.getId() ?: super.getFirstParentId()
    }

    List<PrimitiveType> getPrimitiveTypes() {
        if (!readyForQueries) {
            return []
        }
        return PrimitiveType.findAllByMeasurementUnit(this)
    }

    Long countPrimitiveTypes() {
        if (!readyForQueries) {
            return 0
        }
        return PrimitiveType.countByMeasurementUnit(this)
    }
}
