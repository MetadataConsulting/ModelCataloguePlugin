package org.modelcatalogue.core.specs

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.InitCatalogueService
import spock.lang.Ignore

@Ignore
class InheritanceSpec extends IntegrationSpec  {

    InitCatalogueService initCatalogueService
    CatalogueBuilder catalogueBuilder

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    def "expected inheritance behaviour"() {
        catalogueBuilder.build {
            dataClass name: 'Test Parent Class', {
                dataElement name: 'Test Data Element 1'
                dataElement name: 'Test Data Element 2'
                dataElement name: 'Test Data Element 3'
                ext 'one', '1'
                ext 'two', '2'
                ext 'three', '3'
            }
            dataClass name: 'Test Child Class', {
                dataElement name: 'Test Data Element 4'
                ext 'four', '4'
            }
        }

        when:
        DataClass parentClass = DataClass.findByName('Test Parent Class')
        DataClass childClass = DataClass.findByName('Test Child Class')

        then:
        parentClass.countContains() == 3
        parentClass.ext.size() == 3
        childClass.countContains() == 1
        childClass.extensions.size() == 1

        when:
        childClass.addToIsBasedOn parentClass

        then:
        parentClass.countContains() == 3
        parentClass.ext.size() == 3
        childClass.countContains() == 4
        childClass.extensions.size() == 1

    }


}
