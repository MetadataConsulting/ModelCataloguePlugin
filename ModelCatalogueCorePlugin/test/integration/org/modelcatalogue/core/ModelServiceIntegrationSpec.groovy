package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.util.ListAndCount
import spock.lang.Shared

class ModelServiceIntegrationSpec extends AbstractIntegrationSpec {

    @Shared
    parent1, parent2, child1, child2, grandChild, modelService, de1, de2, de3

    def setupSpec(){
        loadFixtures()
        parent1 = new Model(name: 'First Parent')
        parent2 = new Model(name: 'Second Parent')
        child1 = new Model(name: 'Child 1')
        child2 = new Model(name: 'Child 2')
        grandChild = new Model(name: 'Grand Child')
        de1 = DataElement.findByName("DE_author1")
        de2 = DataElement.findByName("AUTHOR")
        de3 = DataElement.findByName("auth")

        [parent1, parent2, child1, child2, grandChild].each {
            it.status = PublishedElementStatus.FINALIZED
            assert it.save()
        }

        parent1.addToParentOf child1
        parent2.addToParentOf child2
        child1.addToParentOf grandChild
        parent1.addToContains(de1)
        child1.addToContains(de2)
        grandChild.addToContains(de3)

    }

    def cleanupSpec() {

        [parent1.refresh(), parent2.refresh(), child1.refresh(), child2.refresh(), grandChild.refresh()].each {
            it.delete()
        }
    }

    def "get top level elements"() {

        ListAndCount topLevel = modelService.getTopLevelModels([:])

        expect:
        Model.count()           >= 5
        topLevel.count          >= 2
        topLevel.list.size()    == topLevel.count
        topLevel.list.each {
            assert !it.childOf
        }

        topLevel.list.count {
            it.name.contains 'Parent'
        } >= 2

    }

    def "get subModels"() {

        parent1.refresh()
        child1.refresh()
        grandChild.refresh()

        when:
        ListAndCount subModels = modelService.getSubModels(parent1, [:])

        then:
        subModels.count ==3
        subModels.list.contains(parent1)
        subModels.list.contains(child1)
        subModels.list.contains(grandChild)

    }


    def "get data elements from multiple models"() {

        de1 = DataElement.findByName("DE_author1")
        de2 = DataElement.findByName("AUTHOR")
        de3 = DataElement.findByName("auth")

        when:
        ListAndCount dataElements = modelService.getDataElementsFromModels([parent1, child1, grandChild])

        then:
        dataElements.count ==3
        dataElements.list.contains(de1)
        dataElements.list.contains(de2)
        dataElements.list.contains(de3)

    }

    def "test infinite recursion"(){
        setup:
        parent1.refresh()
        child1.refresh()
        grandChild.refresh()
        grandChild.addToParentOf parent1

        when:
        ListAndCount subModels = modelService.getSubModels(parent1, [:])

        then:
        subModels.count ==3

        cleanup:
        grandChild.removeFromParentOf parent1
    }

}
