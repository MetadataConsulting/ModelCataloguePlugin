package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.ListWithTotal

/**
 * Created by adammilward on 05/02/2014.
 */

class DataArchitectServiceSpec extends AbstractIntegrationSpec {

    def dataArchitectService, relationshipService, de1, de2, de3, de4, de5, vd, md


    def setup() {
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("auth")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        vd = ValueDomain.findByName("value domain Celsius")
        md = new Model(name: "tsdfafsd").save()
        md.addToContains(de1)
        de2.valueDomain = vd
        de2.save()
        de1.ext.put("localIdentifier", "test")
        de4.ext.put("test2", "test2")
        de4.ext.put("metadata", "test2")
        de4.ext.put("test3", "test2")
        de4.ext.put("test4", "test2")
        de1.ext.put("Data item No.", "C1031")  // used in def "find relationships"
        de2.ext.put("Optional_Local_Identifier", "C1031") // used in def "find relationships"

        sessionFactory.currentSession.flush()
    }

    def "find relationships and action them"() {
        when:
        Map params = [:]
        params.put("max", 12)
        def relatedDataElements = dataArchitectService.findRelationsByMetadataKeys("Data item No.", "Optional_Local_Identifier", params)

        then:
        relatedDataElements.each { row ->
            relatedDataElements.list.collect { it.source } contains(de1)
            relatedDataElements.list.collect { it.destination } contains(de2)
        }

        when:
        dataArchitectService.actionRelationshipList(relatedDataElements.list)


        then:
        de1.relations.contains(de2)

    }

    def "find data elements without particular extension key"() {
        when:
        ListWithTotal dataElements = dataArchitectService.metadataKeyCheck(max:12, key: 'metadata')

        then:
        dataElements.items.contains(de2)
        !dataElements.items.contains(de4)
        dataElements.items.contains(de1)
        dataElements.items.contains(de5)

    }

    def "find uninstantiatedDataElements"() {
        when:
        Map params = [:]
        params.put("max", 12)
        ListWithTotal dataElements = dataArchitectService.uninstantiatedDataElements(params)

        then:
        !dataElements.items.contains(DataElement.findByName('speed of Vauxhall'))
        dataElements.items.contains(DataElement.get(de1.id))
        dataElements.items.contains(DataElement.get(de3.id))

    }

    def "transform csv"() {
        when:
        CsvTransformation transformation = CsvTransformation.findByName("Example")

        StringReader reader = new StringReader("writer;patient temperature uk;speed of Opel;auth5\nAgata Christie;38.5;120;ABC\nDick Francis;36.7;90;DEF")
        StringWriter writer = new StringWriter()

        dataArchitectService.transformData(transformation, reader, writer)

        CSVReader csvReader = new CSVReader(new StringReader(writer.toString()), ';'.charAt(0))

        println writer.toString()

        List<String> newHeaders = csvReader.readNext().toList()

        then:
        newHeaders.contains 'creator'
        newHeaders.contains 'patient temperature us'
        newHeaders.contains 'speed of Vauxhall'
        newHeaders.contains 'co-author'


        when:
        List<String> firstDataRow = csvReader.readNext().toList()

        then:
        firstDataRow[newHeaders.indexOf("creator")] == "Agata Christie"
        firstDataRow[newHeaders.indexOf("speed of Vauxhall")] == "74.56454304"
        firstDataRow[newHeaders.indexOf("patient temperature us")] == "101.3"
        firstDataRow[newHeaders.indexOf("co-author")] == "ABC"
    }

}
