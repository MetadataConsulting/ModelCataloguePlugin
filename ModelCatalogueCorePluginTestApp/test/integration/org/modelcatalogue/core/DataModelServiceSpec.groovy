package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import spock.lang.Ignore

class DataModelServiceSpec extends AbstractIntegrationSpec {

    def dataModelService
    def dataClassService

    CatalogueBuilder catalogueBuilder

    DataModel model1
    DataModel model2
    DataModel model3

    DataClass class0
    DataClass class1
    DataClass class2
    DataClass class3

    def setup() {
        initRelationshipTypes()
        model1 = new DataModel(name: "Test Classification 1 ${System.currentTimeMillis()}").save(failOnError: true)
        model2 = new DataModel(name: "Test Classification 2 ${System.currentTimeMillis()}").save(failOnError: true)
        model3 = new DataModel(name: "Test Classification 3 ${System.currentTimeMillis()}").save(failOnError: true)
        class0 = new DataClass(name: "Not Classified", status: ElementStatus.FINALIZED).save(failOnError: true)
        class1 = new DataClass(dataModel: model1, name: "Classified 1", status: ElementStatus.FINALIZED).save(failOnError: true)
        class2 = new DataClass(dataModel: model2, name: "Classified 2 ", status: ElementStatus.FINALIZED).save(failOnError: true)
        class3 = new DataClass(dataModel: model3, name: "Classified 3 ", status: ElementStatus.FINALIZED).save(failOnError: true)
        model2.addToImports(model3)
    }

    def "all the models are returned if no classification is selected"() {
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
        new DataType(name: "Test Domain", dataModel: model1).save(failOnError: true)
        DetachedCriteria<DataType> criteria = dataModelService.classified(DataType, DataModelFilter.create(true))

        expect:
        criteria.count() == DataType.list().count { !it.dataModel }

        Lists.fromCriteria([:], criteria).items.size() == criteria.count()
    }


    def "is able to return only models classified by"() {
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass, DataModelFilter.create([model1], []))

        expect:
        criteria.count() == 1
        class1 in criteria.list()
        !(class2 in criteria.list())
    }

    def "is able to return only models not classified by"() {
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass, DataModelFilter.create([], [model2]))

        expect:
        criteria.count() == 2
        class1 in criteria.list()
        !(class2 in criteria.list())
        class3 in criteria.list()
    }

    def "get unclassified top level models"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelDataClasses(DataModelFilter.create(true), [:])

        expect:
        models.total >= 1
        class0 in models.items
        !(class1 in models.items)
    }

    @Ignore
    def "get top level models with include classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelDataClasses(DataModelFilter.create([model1], []), [:])

        expect:
        models.total >= 1
        !(class0 in models.items)
        class1 in models.items
        !(class2 in models.items)
    }


    def "get top level models with exclude classification filter"() {
        ListWithTotalAndType<DataClass> models = dataClassService.getTopLevelDataClasses(DataModelFilter.create([], [model2]), [:])

        expect:
        models.total >= 2
        class0 in models.items
        class1 in models.items
        !(class2 in models.items)
    }

    @Ignore
    def "get top level models with include and exclude classification filter"() {
        when:
        dataClassService.getTopLevelDataClasses(DataModelFilter.create([model1], [model2]), [:])

        then:
        thrown(IllegalStateException)
    }

    def "is able to return classes classified by or are imported"() {
        DetachedCriteria<DataClass> criteria = dataModelService.classified(DataClass, DataModelFilter.create([model2], []).withImports([model3]))

        expect:
        criteria.count() == 2
        !(class1 in criteria.list())
        class2 in criteria.list()
        class3 in criteria.list()
    }

    def "find dependents"() {
        given:
            catalogueBuilder.build {
                skip draft
                dataModel name: 'DMFD 1', {
                    dataType name: 'DMFD 1 DT 1'
                }

                dataModel name: 'DMFD 2', {
                    dataType name: 'DMFD 2 DT 1'
                }

                dataModel name: 'DMFD 3', {
                    dataElement name: 'DMFD 3 DE 1', {
                        dataType name: 'DMFD 1 DT 1', dataModel: 'DMFD 1'
                    }
                    dataElement name: 'DMFD 3 DE 2', {
                        dataType name: 'DMFD 2 DT 1', dataModel: 'DMFD 2'
                    }
                }
            }

        expect:
            dataModelService.findDependents(DataModel.findByName('DMFD 3'))*.name.contains('DMFD 1')
            dataModelService.findDependents(DataModel.findByName('DMFD 3'))*.name.contains('DMFD 2')
    }



}
