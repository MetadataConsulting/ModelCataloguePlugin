package org.modelcatalogue.discourse

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.MeasurementUnit

class DiscourseServiceSpec extends IntegrationSpec {

    DiscourseService discourseService
    InitCatalogueService initCatalogueService

    void "create topic for measurement unit"() {
        initCatalogueService.initDefaultRelationshipTypes()

        Long time = System.currentTimeMillis()

        DataModel classification = new DataModel(name: "Discourse Test").save(failOnError: true)
        MeasurementUnit mu = new MeasurementUnit(dataModel: classification, name: "Degree of Gray $time", description: "There are fifty of these...").save(failOnError: true)

        Long id = discourseService.findOrCreateDiscourseTopic(mu.id)

        expect:
        id

    }
}
