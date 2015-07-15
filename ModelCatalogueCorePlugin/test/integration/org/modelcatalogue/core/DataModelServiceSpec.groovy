package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

class DataModelServiceSpec extends IntegrationSpec {

    def dataModelService
    def initCatalogueService
    def dataClassService

    DataModel classification1
    DataModel classification2

    DataClass model0
    DataClass model1
    DataClass model2

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
        classification1 = new DataModel(name: "Test Classification 1 ${System.currentTimeMillis()}").save(failOnError: true)
        classification2 = new DataModel(name: "Test Classification 2 ${System.currentTimeMillis()}").save(failOnError: true)
        model0 = new DataClass(name: "Not Classified", status: ElementStatus.FINALIZED).save(failOnError: true)
        model1 = new DataClass(name: "Classified 1", status: ElementStatus.FINALIZED).save(failOnError: true)
        model1.addToDeclaredWithin(classification1)
        model2 = new DataClass(name: "Classified 2 ", status: ElementStatus.FINALIZED).save(failOnError: true)
        model2.addToDeclaredWithin(classification2)

    }

    def "all the models are returned if no classification is selected"(){
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass)

        expect:
        new DetachedCriteria<DataClass>(DataClass).count()
        criteria.count()
        criteria.count() == new DetachedCriteria<DataClass>(DataClass).count()
    }

    def "all the finalized models are returned when classification is not selected"() {
        DetachedCriteria<DataClass> finalized = new DetachedCriteria<DataClass>(DataClass).build {
            eq 'status', ElementStatus.FINALIZED
        }

        DetachedCriteria<DataClass> criteria = dataModelService.classified(finalized)

        expect:
        finalized.count()
        criteria.count()
        criteria.count() == finalized.count()
    }

    def "is able to return only unclassified models"() {
        DetachedCriteria<DataClass> all = dataModelService.classified(DataClass, DataModelFilter.NO_FILTER)
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass, DataModelFilter.create(true))

        expect:
        all.count() == DataClass.count()
        criteria.count() == Lists.fromCriteria([:], criteria).items.size()
    }

    def "does not fail when there are no results"() {
        ValueDomain domain = new ValueDomain(name: "Test Domain").save(failOnError: true)
        domain.addToDeclaredWithin classification1
        DetachedCriteria<ValueDomain> criteria = dataModelService.classified(ValueDomain, DataModelFilter.create(true))

        expect:
        criteria.count() == ValueDomain.list().count { !it.classifications }

        Lists.fromCriteria([:], criteria).items.size() == criteria.count()
    }


    def "is able to return only models classified by"() {
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass, DataModelFilter.create([classification1], []))

        expect:
        criteria.count() == 1
        model1 in criteria.list()
        !(model2 in criteria.list())
    }

    def "is able to return only models not classified by"() {
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass, DataModelFilter.create([], [classification2]))

        expect:
        criteria.count() == 1
        model1 in criteria.list()
        !(model2 in criteria.list())
    }

    def "get unclassified top level models"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(DataModelFilter.create(true), [:])

        expect:
        models.total >= 1
        model0 in models.items
        !(model1 in models.items)
    }

    def "get top level models with include classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(DataModelFilter.create([classification1], []), [:])

        expect:
        models.total >= 1
        !(model0 in models.items)
        model1 in models.items
        !(model2 in models.items)
    }


    def "get top level models with exclude classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(DataModelFilter.create([], [classification2]), [:])

        expect:
        models.total >= 2
        model0 in models.items
        model1 in models.items
        !(model2 in models.items)
    }

    def "get top level models with include and exclude classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelModels(DataModelFilter.create([classification1], [classification2]), [:])

        expect:
        models.total >= 1
        !(model0 in models.items)
        model1 in models.items
        !(model2 in models.items)
    }



}
