package org.modelcatalogue.core

import grails.transaction.Transactional

@Transactional
class InitCatalogueService {

    def grailsApplication

    def initDefaultDataTypes() {

       def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.datatypes

       for (definition in defaultDataTypes) {
           DataType existing = DataType.findByName(definition.name)
           if (!existing) {
             DataType type = new DataType(definition)
             type.save()

             if (type.hasErrors()) {
                 log.error("Cannot create data type $definition.name. $type.errors")
             }
           }
       }
    }


    def initDefaultMeasurementUnits() {

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.measurementunits

        for (definition in defaultDataTypes) {
            MeasurementUnit existing = MeasurementUnit.findByName(definition.name)
            if (!existing) {
                MeasurementUnit unit = new MeasurementUnit(definition)
                unit.save()

                if (unit.hasErrors()) {
                    log.error("Cannot create measurement unit $definition.name. $unit.errors")
                }
            }
        }
    }

    def initDefaultRelationshipTypes() {

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.relationshiptypes

        for (definition in defaultDataTypes) {
            RelationshipType existing = RelationshipType.findByName(definition.name)
            if (!existing) {
                RelationshipType type = new RelationshipType(definition)
                type.save()

                if (type.hasErrors()) {
                    log.error("Cannot create relationship type $definition.name. $type.errors")
                }
            }
        }
    }


}
