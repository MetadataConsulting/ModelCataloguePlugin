package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.util.lists.ListWithTotal

class DataArchitectServiceSpec extends AbstractIntegrationSpec {

    def dataArchitectService, relationshipService, de1, de2, de3, de4, de5, md


    def setup() {
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("auth")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        md = new DataClass(name: "tsdfafsd").save()
        md.addToContains(de1)
        de2.save()
        de1.ext.put("localIdentifier", "test")
        de4.ext.put("test2", "test2")
        de4.ext.put("metadataStep", "test2")
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
            relatedDataElements.items.collect { it.source } contains(de1)
            relatedDataElements.items.collect { it.destination } contains(de2)
        }

        when:
        dataArchitectService.actionRelationshipList(relatedDataElements.items)


        then:
        de1.relations.contains(de2)

    }

    def "find data elements without particular extension key"() {
        when:
        ListWithTotal dataElements = dataArchitectService.metadataKeyCheck(max:12, key: 'metadataStep')

        then:
        dataElements.items.contains(de2)
        !dataElements.items.contains(de4)
        dataElements.items.contains(de1)
        dataElements.items.contains(de5)

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

    def "can add suggestion"() {
        boolean test = false
        dataArchitectService.addSuggestion('TEST') {
            test = true
        }

        expect:
        !test

        when:
        dataArchitectService.generateSuggestions()

        then:
        test
    }

}
