package org.modelcatalogue.core

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.util.ListAndCount
import spock.lang.Shared

/**
* Created by adammilward on 05/02/2014.
*/

class DataArchitectSpec extends AbstractIntegrationSpec{
    @Shared
    def dataArchitectService, relationshipService, de1, de2, de3, de4, de5, vd, md


    def setupSpec(){
        //domainModellerService.modelDomains()
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("DE_author1")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        vd = ValueDomain.findByName("value domain Celsius")
        md = Model.findByName("book")
        de1.addToContainedIn(md)
        de2.addToInstantiatedBy(vd)
        relationshipService.link(de3, de2, RelationshipType.findByName("supersession"))
        de1.ext.put("localIdentifier", "test")
        de4.ext.put("test2", "test2")
        de4.ext.put("metadata", "test2")
        de4.ext.put("test3", "test2")
        de4.ext.put("test4", "test2")
    }

    def cleanupSpec(){
        de1.removeFromContainedIn(md)
        de2.removeFromInstantiatedBy(vd)
    }

    def "find data elements without particular extension key"(){
        when:
        Map params = [:]
        params.put("max", 12)
        params.put("key", "metadata")
        ListAndCount dataElements = dataArchitectService.metadataKeyCheck(params)

        then:
        !dataElements.list.contains(de2)
        !dataElements.list.contains(de4)
        dataElements.list.contains(de1)
        dataElements.list.contains(de5)

    }

    def "find uninstantiatedDataElements"(){
        when:
        Map params = [:]
        params.put("max", 12)
        ListAndCount dataElements = dataArchitectService.uninstantiatedDataElements(params)

        then:
        !dataElements.list.contains(DataElement.get(de2.id))
        dataElements.list.contains(DataElement.get(de1.id))
        dataElements.list.contains(DataElement.get(de3.id))

    }




}
