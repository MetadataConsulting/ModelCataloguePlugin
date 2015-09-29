package org.modelcatalogue.discourse

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.MeasurementUnit

class DiscourseServiceSpec extends IntegrationSpec {

    DiscourseService discourseService
    InitCatalogueService initCatalogueService

    void "create topic for measurement unit"() {
        initCatalogueService.initDefaultRelationshipTypes()

        Long time = System.currentTimeMillis()

        MeasurementUnit mu = new MeasurementUnit(name: "Degree of Gray $time", description: "There are fifty of these...").save(failOnError: true)
        Classification classification = new Classification(name: "Discourse Test").save(failOnError: true)

        mu.addToClassifications classification

        Long id = discourseService.findOrCreateDiscourseTopic(mu.id)

        expect:
        id

    }
}
