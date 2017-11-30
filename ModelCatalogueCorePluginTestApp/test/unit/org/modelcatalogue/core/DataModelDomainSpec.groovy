package org.modelcatalogue.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@Mock([DataElement, DataClass, CatalogueElement])
@TestFor(DataModel)
class DataModelDomainSpec extends Specification {
    
    void 'test name cannot be null'() {
        given:
        DataModel domain = new DataModel()

        when:
        domain.name = null

        then:
        !domain.validate(['name'])
        domain.errors['name'].code == 'nullable'
    }

    void 'test name cannot be blank'() {
        given:
        DataModel domain = new DataModel()

        when:
        domain.name = ''

        then:
        !domain.validate(['name'])
    }

    void 'test name can have a maximum of 255 characters'() {
        given:
        DataModel domain = new DataModel()

        when: 'for a string of 256 characters'
        String str = 'a' * 256
        domain.name = str

        then: 'name validation fails'
        !domain.validate(['name'])
        domain.errors['name'].code == 'size.toobig'

        when: 'for a string of 256 characters'
        str = 'a' * 255
        domain.name = str

        then: 'name validation passes'
        domain.validate(['name'])
    }

    void 'test description can be null'() {
        given:
        DataModel domain = new DataModel()

        when:
        domain.description = null

        then:
        domain.validate(['description'])
    }

    void 'test description can be blank'() {
        given:
        DataModel domain = new DataModel()

        when:
        domain.description = ''

        then:
        domain.validate(['description'])
    }


    void 'test description can have a maximum of 20000 characters'() {
        given:
        DataModel domain = new DataModel()

        when: 'for a string of 256 characters'
        String str = 'a' * 20001
        domain.description = str

        then: 'description validation fails'
        !domain.validate(['description'])
        domain.errors['description'].code == 'maxSize.exceeded'

        when: 'for a string of 20000 characters'
        str = 'a' * 20000
        domain.description = str

        then: 'description validation passes'
        domain.validate(['description'])
    }
}
