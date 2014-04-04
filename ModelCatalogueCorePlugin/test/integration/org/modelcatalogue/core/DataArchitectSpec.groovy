package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

/**
* Created by adammilward on 05/02/2014.
*/

class DataArchitectSpec extends AbstractIntegrationSpec{
    @Shared
    def dataArchitectService, relationshipService, de1, de2, de3, vd, md


    def setupSpec(){
        //domainModellerService.modelDomains()
        loadFixtures()
        RelationshipType.initDefaultRelationshipTypes()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("DE_author1")
        de3 = DataElement.findByName("AUTHOR")
        vd = ValueDomain.findByName("value domain Celsius")
        md = Model.findByName("book")
        de1.addToContainedIn(md)
        de2.addToInstantiatedBy(vd)
        relationshipService.link(de3, de2, RelationshipType.findByName("supersession"))
    }

    def cleanupSpec(){
        de1.removeFromContainedIn(md)
        de2.removeFromInstantiatedBy(vd)
    }

    def "marshall domain models"(){
        when:
        def dataElements = dataArchitectService.uninstantiatedDataElements()

        then:
        !dataElements.contains(de2)
        dataElements.contains(de1)
        dataElements.contains(de3)

    }


}
