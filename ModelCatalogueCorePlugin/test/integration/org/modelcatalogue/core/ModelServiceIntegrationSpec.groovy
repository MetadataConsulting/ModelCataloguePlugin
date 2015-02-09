package org.modelcatalogue.core

import org.modelcatalogue.core.util.ListWithTotal

class ModelServiceIntegrationSpec extends AbstractIntegrationSpec {

    Model parent1
    Model parent2
    Model child1
    Model child2
    Model grandChild
    ModelService modelService
    DataElement de1
    DataElement de2
    DataElement de3

    def setup(){
        loadFixtures()
        parent1 = new Model(name: 'book').save(failOnError: true)
        parent2 = new Model(name: 'chapter1').save(failOnError: true)
        child1 = new Model(name: 'chapter2').save(failOnError: true)
        child2 = new Model(name: 'mTest1').save(failOnError: true)
        grandChild = new Model(name: 'mTest2').save(failOnError: true)
        parent1.addToParentOf child1
        parent2.addToParentOf child2
        child1.addToParentOf grandChild
        de1 = DataElement.findByName("DE_author1")
        de2 = DataElement.findByName("AUTHOR")
        de3 = DataElement.findByName("auth")
    }

    def "get top level elements"() {
        ListWithTotal topLevel = modelService.getTopLevelModels([:])

        expect:
        Model.count()           >= 5
        topLevel.total          >= 2
        topLevel.items.size()   == topLevel.total
        topLevel.items.each {
            assert !it.childOf
        }

    }

    def "get subModels"() {
        when:
        ListWithTotal subModels = modelService.getSubModels(parent1)

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
        ListWithTotal dataElements = modelService.getDataElementsFromModels([parent1, child1, grandChild])

        then:
        dataElements.count ==3
        dataElements.list.contains(de1)
        dataElements.list.contains(de2)
        dataElements.list.contains(de3)
    }

    def "test infinite recursion"(){
        when:
        ListWithTotal subModels = modelService.getSubModels(parent1)

        then:
        subModels.count ==3
    }

}
