package org.modelcatalogue.core

import org.modelcatalogue.core.util.ListWithTotal

class DataClassServiceIntegrationSpec extends AbstractIntegrationSpec {

    DataClass parent1
    DataClass parent2
    DataClass child1
    DataClass child2
    DataClass grandChild
    DataClassService dataClassService
    DataElement de1
    DataElement de2
    DataElement de3

    def setup(){
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
        DataClass.count()           >= 5
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
        subModels.count == 3
        subModels.list.contains(parent1)
        subModels.list.contains(child1)
        subModels.list.contains(grandChild)
    }


    def "get data elements from multiple models"() {
        parent1.addToContains(de1)
        child1.addToContains(de2)
        grandChild.addToContains(de3)

        when:
        ListWithTotal dataElements = dataClassService.getDataElementsFromClasses([parent1, child1, grandChild])

        then:
        dataElements.count ==3
        dataElements.list.contains(de1)
        dataElements.list.contains(de2)
        dataElements.list.contains(de3)
    }

    def "test infinite recursion"(){
        when:
        ListWithTotal subModels = dataClassService.getInnerClasses(parent1)

        then:
        subModels.count ==3
    }

}
