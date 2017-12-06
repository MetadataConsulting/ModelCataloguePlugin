package org.modelcatalogue.core.util

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataType

abstract class RelationshipTypeRuleScript extends Script {

    CatalogueElement getDestination() {
        return binding.getProperty('destination') as CatalogueElement
    }

    void setDestination(CatalogueElement destination) {
        binding.setProperty 'destination', destination
    }

    CatalogueElement getSource() {
        return binding.getProperty('source') as CatalogueElement
    }

    void setSource(CatalogueElement source) {
        binding.setProperty 'source', source
    }

    def isSameClass() {
        Class sourceClass = HibernateHelper.getEntityClass(source)
        Class destinationClass = HibernateHelper.getEntityClass(destination)
        if (sourceClass == destinationClass) {
            return true
        }
        if (DataType.isAssignableFrom(sourceClass) &&  DataType.isAssignableFrom(destinationClass)) {
            return true
        }
        return ["relationshipType.containment.not.same.type", "The destination must be of the same type as the source!"]
    }
}
