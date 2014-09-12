package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.ListWithTotal
import spock.lang.Shared
/**
 * Created by adammilward on 05/02/2014.
 */

class DataArchitectServiceSpec extends AbstractIntegrationSpec {
    @Shared
    def dataArchitectService, relationshipService, de1, de2, de3, de4, de5, vd, md


    def setupSpec(){
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("auth")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        vd = ValueDomain.findByName("value domain Celsius")
        md = new Model(name:"tsdfafsd").save()
        de1.refresh()
        de2.refresh()
        md.refresh()
        md.addToContains(de1)
        de2.valueDomain = vd
        de1.ext.put("localIdentifier", "test")
        de4.ext.put("test2", "test2")
        de4.ext.put("metadata", "test2")
        de4.ext.put("test3", "test2")
        de4.ext.put("test4", "test2")
        de1.ext.put("Data item No.", "C1031")  // used in def "find relationships"
        de2.ext.put("Optional_Local_Identifier", "C1031") // used in def "find relationships"
    }

    def cleanupSpec(){
        md.refresh()
        de1.refresh()
        md.removeFromContains(de1)
        md.delete(flush:true)
    }

    def "find relationships and action them"() {
        when:
        Map params = [:]
        params.put("max", 12)
        def relatedDataElements = dataArchitectService.findRelationsByMetadataKeys("Data item No.","Optional_Local_Identifier", params)

        then:
        relatedDataElements.each {row ->
            relatedDataElements.list.collect{it.source}contains(de1)
            relatedDataElements.list.collect{it.destination}contains(de2)
        }

        when:
        dataArchitectService.actionRelationshipList(relatedDataElements.list)


        then:
        de1.relations.contains(de2)

    }

    def "find data elements without particular extension key"(){
        when:
        Map params = [:]
        params.put("max", 12)
        params.put("key", "metadata")
        de1.refresh()
        de5.refresh()
        ListWithTotal dataElements = dataArchitectService.metadataKeyCheck(params)
        then:

        !dataElements.items.contains(de2)
        !dataElements.items.contains(de4)
        dataElements.items.contains(de1)
        dataElements.items.contains(de5)

    }

    def "find uninstantiatedDataElements"(){
        when:
        Map params = [:]
        params.put("max", 12)
        ListWithTotal dataElements = dataArchitectService.uninstantiatedDataElements(params)

        then:
        !dataElements.items.contains(DataElement.findByName('speed of Vauxhall'))
        dataElements.items.contains(DataElement.get(de1.id))
        dataElements.items.contains(DataElement.get(de3.id))

    }

}
