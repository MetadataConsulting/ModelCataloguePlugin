package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.util.ClassificationFilter

class ClassificationServiceSpec extends IntegrationSpec {

    def classificationService
    def initCatalogueService

    Classification classification1
    Classification classification2

    Model model1
    Model model2

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
        classification1 = new Classification(name: "Test Classification 1 ${System.currentTimeMillis()}").save(failOnError: true)
        classification2 = new Classification(name: "Test Classification 2 ${System.currentTimeMillis()}").save(failOnError: true)
        new Model(name: "Not Classified", status: ElementStatus.FINALIZED).save(failOnError: true)
        model1 = new Model(name: "Classified 1", status: ElementStatus.FINALIZED).save(failOnError: true)
        model1.addToClassifications(classification1).save()
        model2 = new Model(name: "Classified 2 ", status: ElementStatus.FINALIZED).save(failOnError: true)
        model2.addToClassifications(classification2).save()

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

    def "is able to return only unclassified models"() {
        DetachedCriteria<Model> all = classificationService.classified(Model, ClassificationFilter.NO_FILTER)
        DetachedCriteria<Model> criteria = classificationService.classified(Model, ClassificationFilter.create(true))

        expect:
        all.count() == 3
        criteria.count() == 1
    }


    def "is able to return only models classified by"() {
        DetachedCriteria<Model> criteria = classificationService.classified(Model, ClassificationFilter.create([classification1], []))

        expect:
        criteria.count() == 1
        model1 in criteria.list()
        !(model2 in criteria.list())
    }

    def "is able to return only models not classified by"() {
        DetachedCriteria<Model> criteria = classificationService.classified(Model, ClassificationFilter.create([], [classification2]))

        expect:
        criteria.count() == 1
        model1 in criteria.list()
        !(model2 in criteria.list())
    }



}
