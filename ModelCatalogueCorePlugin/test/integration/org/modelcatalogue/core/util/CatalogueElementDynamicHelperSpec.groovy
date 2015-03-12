package org.modelcatalogue.core.util

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Model
import spock.lang.Unroll

class CatalogueElementDynamicHelperSpec extends IntegrationSpec {

    def initCatalogueService

    def "Relationships are added to the transients"() {
        expect:
        CatalogueElement.transients
        CatalogueElement.transients.contains('isBaseFor')
    }

    @Unroll
    def "Relationship #prop is added to #clazz"() {

        initCatalogueService.initDefaultRelationshipTypes()
        def instance = clazz.newInstance()

        expect:
        instance.hasProperty(prop)
        instance[prop]                          == []
        instance[prop + 'Relationships']        == []
        instance."count${prop.capitalize()}"()  == 0


        where:
        clazz                   | prop
        Model                   | 'parentOf'
    }

}