package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables

class PrimitiveType extends DataType {

    MeasurementUnit measurementUnit

    static constraints = {
        measurementUnit nullable: true
    }

    static mapping = {
        measurementUnit fetch: 'join'
    }

    Iterable<String> getInheritedAssociationsNames() { Iterables.concat(super.inheritedAssociationsNames, ImmutableSet.of('measurementUnit')) }

    List<CatalogueElement> collectExternalDependencies() {
        if (measurementUnit && measurementUnit.dataModel != dataModel) {
            return [measurementUnit]
        }
        return super.collectExternalDependencies()
    }
}
