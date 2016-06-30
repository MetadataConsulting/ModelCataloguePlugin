package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import grails.transaction.Rollback

@Rollback
class CatalogueElementServiceIntegrationSpec extends IntegrationSpec {

    def sessionFactory
    def catalogueElementService

    def "test deletion of CatalogueElement"() {
        setup:
            def dataClass = new DataClass(name: "Freedom for China").save()

        when:
            catalogueElementService.delete(dataClass)

        then:
            DataClass.get(dataClass.id) == null
    }

    def "test if delete of CatalogueElement deletes its relationships also"() {
        setup:
            def northKoreaClass = new DataClass(name: "Freedom for North Korea").save(failOnError: true)
            def sovietUnionClass = new DataClass(name: "Vasilij Dzhugashvili").save(failOnError: true)
            def parentOf = new RelationshipType(name: 'parentOf', sourceToDestination: 'parentOf',
                                                destinationToSource: 'childOf', sourceClass: CatalogueElement,
                                                destinationClass: CatalogueElement).save(failOnError: true)
            // Soviet Union is parent of North Korea
            sovietUnionClass.createLinkTo(northKoreaClass, parentOf)

        when:
            // this actually happened: soviets are gone now!
            catalogueElementService.delete(sovietUnionClass)
            // flush session not to re-saved deleted objects
            def hibernateSession = sessionFactory.getCurrentSession()
            hibernateSession.flush()

        then:
            // but north korea is still here
            DataClass.get(northKoreaClass.id) != null
            DataClass.get(sovietUnionClass.id) == null
            DataClass.get(northKoreaClass.id).getIncomingRelations().size() == 0
    }
}
