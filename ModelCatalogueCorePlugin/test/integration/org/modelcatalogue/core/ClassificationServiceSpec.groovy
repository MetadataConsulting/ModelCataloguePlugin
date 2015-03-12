package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec

class ClassificationServiceSpec extends IntegrationSpec {

    def classificationService
    def initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
        new Model(name: "Not Classified", status: ElementStatus.FINALIZED).save(failOnError: true)
        new Model(name: "Classified", status: ElementStatus.FINALIZED).save(failOnError: true).addToClassifications(new Classification(name: "Test Classification ${System.currentTimeMillis()}").save(failOnError: true))

    }

    def "all the models are returned if no classification is selected"(){
        DetachedCriteria<Model> criteria = classificationService.classified(Model)

        expect:
        new DetachedCriteria<Model>(Model).count()
        criteria.count()
        criteria.count() == new DetachedCriteria<Model>(Model).count()
    }

    def "all the finalized models are returned when classification is not selected"() {
        DetachedCriteria<Model> finalized = new DetachedCriteria<Model>(Model).build {
            eq 'status', ElementStatus.FINALIZED
        }

        DetachedCriteria<Model> criteria = classificationService.classified(finalized)

        expect:
        finalized.count()
        criteria.count()
        criteria.count() == finalized.count()
    }



}
