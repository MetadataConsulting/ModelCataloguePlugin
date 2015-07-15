package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

class ClassificationServiceSpec extends IntegrationSpec {

    def classificationService
    def initCatalogueService
    def dataClassService

    Classification classification1
    Classification classification2

    DataClass model0
    DataClass model1
    DataClass model2

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
        classification1 = new Classification(name: "Test Classification 1 ${System.currentTimeMillis()}").save(failOnError: true)
        classification2 = new Classification(name: "Test Classification 2 ${System.currentTimeMillis()}").save(failOnError: true)
        model0 = new DataClass(name: "Not Classified", status: ElementStatus.FINALIZED).save(failOnError: true)
        model1 = new DataClass(name: "Classified 1", status: ElementStatus.FINALIZED).save(failOnError: true)
        model1.addToClassifications(classification1)
        model2 = new DataClass(name: "Classified 2 ", status: ElementStatus.FINALIZED).save(failOnError: true)
        model2.addToClassifications(classification2)

    }

    def "all the models are returned if no classification is selected"(){
        DetachedCriteria<DataClass> criteria = classificationService.classified(DataClass)

        expect:
        new DetachedCriteria<DataClass>(DataClass).count()
        criteria.count()
        criteria.count() == new DetachedCriteria<DataClass>(DataClass).count()
    }

    def "all the finalized models are returned when classification is not selected"() {
        DetachedCriteria<DataClass> finalized = new DetachedCriteria<DataClass>(DataClass).build {
            eq 'status', ElementStatus.FINALIZED
        }

        DetachedCriteria<DataClass> criteria = classificationService.classified(finalized)

        expect:
        finalized.count()
        criteria.count()
        criteria.count() == finalized.count()
    }

    def "is able to return only unclassified models"() {
        DetachedCriteria<DataClass> all = classificationService.classified(DataClass, ClassificationFilter.NO_FILTER)
        DetachedCriteria<DataClass> criteria = classificationService.classified(DataClass, ClassificationFilter.create(true))

        expect:
        all.count() == DataClass.count()
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
        DetachedCriteria<DataClass> criteria = classificationService.classified(DataClass, ClassificationFilter.create([classification1], []))

        expect:
        criteria.count() == 1
        model1 in criteria.list()
        !(model2 in criteria.list())
    }

    def "is able to return only models not classified by"() {
        DetachedCriteria<DataClass> criteria = classificationService.classified(DataClass, ClassificationFilter.create([], [classification2]))

        expect:
        criteria.count() == 1
        model1 in criteria.list()
        !(model2 in criteria.list())
    }

    def "get unclassified top level models"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(ClassificationFilter.create(true), [:])

        expect:
        models.total >= 1
        model0 in models.items
        !(model1 in models.items)
    }

    def "get top level models with include classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(ClassificationFilter.create([classification1], []), [:])

        expect:
        models.total >= 1
        !(model0 in models.items)
        model1 in models.items
        !(model2 in models.items)
    }


    def "get top level models with exclude classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(ClassificationFilter.create([], [classification2]), [:])

        expect:
        models.total >= 2
        model0 in models.items
        model1 in models.items
        !(model2 in models.items)
    }

    def "get top level models with include and exclude classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(ClassificationFilter.create([classification1], [classification2]), [:])

        expect:
        models.total >= 1
        !(model0 in models.items)
        model1 in models.items
        !(model2 in models.items)
    }



}
