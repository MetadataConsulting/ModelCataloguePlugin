package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.transaction.Transactional

@Transactional
class InitCatalogueService {

    def grailsApplication

    def initDefaultDataTypes() {

       def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.datatypes

       for (definition in defaultDataTypes) {
           DataType existing = DataType.findByName(definition.name)
           if (!existing) {
              new DataType(definition).save()
           }
       }
    }


    def initDefaultMeasurementUnits() {

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.measurementunits

        for (definition in defaultDataTypes) {
            MeasurementUnit existing = MeasurementUnit.findByName(definition.name)
            if (!existing) {
                new MeasurementUnit(definition).save()
            }
        }
    }

    def initDefaultRelationshipTypes() {

        def defaultDataTypes = grailsApplication.config.modelcatalogue.defaults.relationshiptypes

        for (definition in defaultDataTypes) {
            RelationshipType existing = RelationshipType.findByName(definition.name)
            if (!existing) {
                new RelationshipType(definition).save()
            }
        }
    }


}
