package org.modelcatalogue.core

import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotal
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import spock.lang.Ignore

class DataClassServiceIntegrationSpec extends AbstractIntegrationSpec {

    CatalogueBuilder catalogueBuilder

    DataClass parent1
    DataClass parent2
    DataClass child1
    DataClass child2
    DataClass grandChild
    DataClassService dataClassService
    DataElement de1
    DataElement de2
    DataElement de3

    def setup() {
        loadFixtures()
        parent1 = new DataClass(name: 'book').save(failOnError: true)
        parent2 = new DataClass(name: 'chapter1').save(failOnError: true)
        child1 = new DataClass(name: 'chapter2').save(failOnError: true)
        child2 = new DataClass(name: 'mTest1').save(failOnError: true)
        grandChild = new DataClass(name: 'mTest2').save(failOnError: true)
        parent1.addToParentOf child1
        parent2.addToParentOf child2
        child1.addToParentOf grandChild
        de1 = DataElement.findByName("DE_author1")
        de2 = DataElement.findByName("AUTHOR")
        de3 = DataElement.findByName("auth")
    }

    def "get top level elements"() {
        ListWithTotal topLevel = dataClassService.getTopLevelDataClasses([:])

        expect:
        DataClass.count()       >= 5
        topLevel.total          >= 2
        topLevel.items.size()   == topLevel.total
        topLevel.items.each {
            assert !it.childOf
        }

    }

    def "get subModels"() {
        when:
        ListWithTotal subModels = dataClassService.getInnerClasses(parent1)

        then:
        subModels.total == 3L
        subModels.items.contains(parent1)
        subModels.items.contains(child1)
        subModels.items.contains(grandChild)
    }


    def "get data elements from multiple models"() {
        parent1.addToContains(de1)
        child1.addToContains(de2)
        grandChild.addToContains(de3)

        when:
        ListWithTotal dataElements = dataClassService.getDataElementsFromClasses([parent1, child1, grandChild])

        then:
        dataElements.total == 3L
        dataElements.items.contains(de1)
        dataElements.items.contains(de2)
        dataElements.items.contains(de3)
    }

    def "test infinite recursion"() {
        when:
        ListWithTotal subModels = dataClassService.getInnerClasses(parent1)

        then:
        subModels.total == 3L
    }

    @Ignore
    def "data model filter where data classes has parents from other data models"() {
        catalogueBuilder.build {
            skip draft
            dataModel name: 'DM1', {
                dataClass name: 'DC1', {
                    child 'DM2', 'DC2'
                }
            }

            dataModel name: 'DM2', {
                dataClass name: 'DC2'
            }

            dataClass name: 'DC3', {
                rel 'hierarchy' from 'DM1', 'DC1'
            }

            dataModel name: 'DM4', {
                dataClass name: 'DC4'
            }
        }

        DataModel DM1 = DataModel.findByName('DM1')
        DataModel DM2 = DataModel.findByName('DM2')
        DataModel DM4 = DataModel.findByName('DM4')

        DataClass DC1 = DataClass.findByName('DC1')
        DataClass DC2 = DataClass.findByName('DC2')
        DataClass DC3 = DataClass.findByName('DC3')
        DataClass DC4 = DataClass.findByName('DC4')

        expect:
        DM1
        DM2
        DM4
        DC1
        DC2
        DC3
        DC4
        DC2 in DC1.parentOf
        DC3 in DC1.parentOf
        DM1 == DC1.dataModel
        DM2 == DC2.dataModel
        DM4 == DC4.dataModel
        DC3.dataModel == null

        when:
        ListWithTotalAndType<DataClass> includedClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(DM2), [status: 'DRAFT'])

        then:
        !(DC1 in includedClasses.items)
        DC2 in includedClasses.items
        !(DC3 in includedClasses.items)
        !(DC4 in includedClasses.items)
        includedClasses.total == 1L

        when:
        ListWithTotalAndType<DataClass> excludedClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.excludes(DM1), [status: 'DRAFT'])

        then:
        !(DC1 in excludedClasses.items)
        DC2 in excludedClasses.items
        DC3 in excludedClasses.items
        DC4 in excludedClasses.items
        excludedClasses.total >= 12L

        when:
        ListWithTotalAndType<DataClass> unclassifiedClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.create(true), [status: 'DRAFT'])

        then:
        DC3 in unclassifiedClasses.items
        !(DC1 in unclassifiedClasses.items)
        !(DC2 in unclassifiedClasses.items)
        unclassifiedClasses.total >= 10L

    }
}
