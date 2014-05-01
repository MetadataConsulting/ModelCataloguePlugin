package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.util.ListAndCount

class ModelServiceIntegrationSpec extends IntegrationSpec {

    ModelService modelService
    InitCatalogueService initCatalogueService


    def "get top level elements"() {
        initCatalogueService.initDefaultRelationshipTypes()

        def parent1 = new Model(name: 'First Parent')
        def parent2 = new Model(name: 'Second Parent')
        def child1 = new Model(name: 'Child 1')
        def child2 = new Model(name: 'Child 2')
        def grandChild = new Model(name: 'Grand Child')

        [parent1, parent2, child1, child2, grandChild].each {
            it.status = PublishedElementStatus.FINALIZED
            assert it.save()
        }

        parent1.addToChildOf child1
        parent2.addToChildOf child2
        child1.addToChildOf grandChild

        ListAndCount topLevel = modelService.getTopLevelModels([:])

        expect:
        Model.count()           >= 5
        topLevel.count          >= 2
        topLevel.list.size()    == topLevel.count
        topLevel.list.each {
            assert !it.parentOf
        }

        cleanup:
        [parent1, parent2, child1, child2, grandChild].each {
            it.delete()
        }
    }

}
