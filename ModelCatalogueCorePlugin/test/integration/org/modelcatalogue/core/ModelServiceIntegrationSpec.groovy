package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.util.ListWithTotal
import spock.lang.Shared

class ModelServiceIntegrationSpec extends AbstractIntegrationSpec {

    @Shared
    parent1, parent2, child1, child2, grandChild, modelService, de1, de2, de3

    def setupSpec(){
        loadFixtures()
        parent1 = Model.findByName('book')
        parent2 = Model.findByName('chapter1')
        child1 = Model.findByName('chapter2')
        child2 = Model.findByName('mTest1')
        grandChild = Model.findByName('mTest2')
        parent1.addToParentOf child1
        parent2.addToParentOf child2
        child1.addToParentOf grandChild
    }

    def cleanupSpec() {
       parent1 = Model.findByName('book')
       parent2 = Model.findByName('chapter1')
       child1 = Model.findByName('chapter2')
       child2 = Model.findByName('mTest1')
       grandChild = Model.findByName('mTest2')
       parent1.removeFromParentOf child1
       parent2.removeFromParentOf child2
       child1.removeFromParentOf grandChild
    }

    def "get top level elements"() {

        parent1 = Model.findByName('book')
        parent2 = Model.findByName('chapter1')
        child1 = Model.findByName('chapter2')
        child2 = Model.findByName('mTest1')
        grandChild = Model.findByName('mTest2')
        ListWithTotal topLevel = modelService.getTopLevelModels([:])

        expect:
        Model.count()           >= 5
        topLevel.count          >= 2
        topLevel.list.size()    == topLevel.count
        topLevel.list.each {
            assert !it.childOf
        }

    }

    def "get subModels"() {
        parent1 = Model.findByName('book')
        parent2 = Model.findByName('chapter1')
        child1 = Model.findByName('chapter2')
        child2 = Model.findByName('mTest1')
        grandChild = Model.findByName('mTest2')
        when:
        ListWithTotal subModels = modelService.getSubModels(parent1)

        then:
        subModels.count ==3
        subModels.list.contains(parent1)
        subModels.list.contains(child1)
        subModels.list.contains(grandChild)

    }


    def "get data elements from multiple models"() {
        parent1 = Model.findByName('book')
        parent2 = Model.findByName('chapter1')
        child1 = Model.findByName('chapter2')
        child2 = Model.findByName('mTest1')
        grandChild = Model.findByName('mTest2')
        de1 = DataElement.findByName("DE_author1")
        de2 = DataElement.findByName("AUTHOR")
        de3 = DataElement.findByName("auth")

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

        cleanup:

        parent1.removeFromContains(de1)
        child1.removeFromContains(de2)
        grandChild.removeFromContains(de3)


    }

    def "test infinite recursion"(){
        setup:
        parent1 = Model.findByName('book')
        parent2 = Model.findByName('chapter1')
        child1 = Model.findByName('chapter2')
        child2 = Model.findByName('mTest1')
        grandChild = Model.findByName('mTest2')
        grandChild.addToParentOf parent1

        when:
        ListWithTotal subModels = modelService.getSubModels(parent1)

        then:
        subModels.count ==3

        cleanup:
        grandChild.removeFromParentOf parent1
    }

}
