package org.modelcatalogue.core
/*
* Measurement units are units such as MPH, cm3, cm2, m etc.
* They are used by value domains to instantiate a data element
*
* */

class MeasurementUnit extends CatalogueElement {

    String symbol

    static constraints = {
        name unique: 'versionNumber'
        symbol nullable: true, size: 1..100
    }

    static transients = ['primitiveTypes']

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

    @Override
    Long getFirstParentId() {
        return getPrimitiveTypes().find { it.getDataModelId() == getDataModelId() }?.getId() ?: super.getFirstParentId()
    }



}
