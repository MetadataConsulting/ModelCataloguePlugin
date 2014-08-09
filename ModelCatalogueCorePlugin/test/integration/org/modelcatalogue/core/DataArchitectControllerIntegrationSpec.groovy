package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.ListWithTotal
import spock.lang.Shared

/**
 * Created by adammilward on 27/02/2014.
 */
class DataArchitectControllerIntegrationSpec extends AbstractIntegrationSpec {


    @Shared
    def dataArchitectService, de1, de2, de3, de4, de5, vd, md,md2


    def setupSpec(){
        loadFixtures()
        de1 = DataElement.findByName("auth9")
        de2 = DataElement.findByName("auth8")
        de3 = DataElement.findByName("title")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        de1.ext.put("Data item No.", "C1031")
        de2.ext.put("Optional_Local_Identifier", "C1031")
        de2.ext.put("metadata", "blah")
        de1.save()
        vd = ValueDomain.findByName("value domain Celsius")
        md = new Model(name:"testModel1234").save(flush:true)
        md2 = new Model(name:"testModel2345").save(flush:true)
        md.addToContains(de1)
        md.addToContains(de3)
        de2.addToInstantiatedBy(vd)
        md.addToParentOf(md2)
        de2.save(flush:true)

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
        de2.refresh()
        de5.refresh()
        ListWithTotal dataElements = dataArchitectService.metadataKeyCheck(params)
        then:

        !dataElements.items.contains(de2)
        dataElements.items.contains(de1)
        dataElements.items.contains(de5)

    }

    def "find uninstantiatedDataElements"(){
        when:
        Map params = [:]
        params.put("max", 12)
        ListWithTotal dataElements = dataArchitectService.uninstantiatedDataElements(params)
        de1.refresh()
        de2.refresh()
        de5.refresh()

        then:
        !dataElements.items.find{ it.modelCatalogueId==de2.modelCatalogueId }
        dataElements.items.find{ it.modelCatalogueId==de1.modelCatalogueId }
        dataElements.items.find{ it.modelCatalogueId==de3.modelCatalogueId }

    }



    def "placeholder"(){

        expect:

        de1


    }


    def cleanupSpec(){
        md.refresh()
        md2.refresh()
        de1.refresh()
        de3.refresh()
        md.removeFromContains(de1)
        md.removeFromContains(de3)
        md.removeFromParentOf(md2)
        de2.refresh()
        de2.removeFromInstantiatedBy(vd)
        md.refresh()
        md.delete(flush:true)
        md2.refresh()
        md2.delete(flush:true)
    }

}
