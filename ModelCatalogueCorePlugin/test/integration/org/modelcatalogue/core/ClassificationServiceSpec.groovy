package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.Lists

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
        model1.addToClassifications(classification1)
        model2 = new Model(name: "Classified 2 ", status: ElementStatus.FINALIZED).save(failOnError: true)
        model2.addToClassifications(classification2)

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
        all.count() == Model.count()
        criteria.count() == Lists.fromCriteria([:], criteria).items.size()
    }

    def "does not fail when there are no results"() {
        ValueDomain domain = new ValueDomain(name: "Test Domain").save(failOnError: true)
        domain.addToClassifications classification1
        DetachedCriteria<ValueDomain> criteria = classificationService.classified(ValueDomain, ClassificationFilter.create(true))

        expect:
        criteria.count() == ValueDomain.list().count { !it.classifications }

        Lists.fromCriteria([:], criteria).items.size() == criteria.count()
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
