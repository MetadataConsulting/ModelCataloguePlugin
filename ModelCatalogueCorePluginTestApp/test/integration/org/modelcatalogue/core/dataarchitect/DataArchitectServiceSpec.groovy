package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.actions.CreateMatch
import org.modelcatalogue.core.util.lists.ListWithTotal

class DataArchitectServiceSpec extends AbstractIntegrationSpec {

    def dataArchitectService, relationshipService, de1, de2, de3, de4, de5, md, dm1, dm2, actionService


    def setup() {
        loadFixtures()
        de1 = DataElement.findByName("DE_author")
        de2 = DataElement.findByName("auth")
        de3 = DataElement.findByName("AUTHOR")
        de4 = DataElement.findByName("auth4")
        de5 = DataElement.findByName("auth5")
        dm1 = DataModel.findByName("data set a")
        dm2 = DataModel.findByName("data set 2")
        md = new DataClass(name: "tsdfafsd").save()
        md.addToContains(de1)
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
        ListWithTotal dataElements = dataArchitectService.metadataKeyCheck(max:12, key: 'metadata')

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
            dataArchitectService.generateSuggestions("Data Element Fuzzy Match", "$dm1.id", "$dm2.id")
            Batch batch = Batch.findByName("Suggested Fuzzy DataElement Relations for '${dm1.name} (${dm1.dataModelSemanticVersion})' and '${dm2.name} (${dm2.dataModelSemanticVersion})'")
            Map<String, String> params = new HashMap<String, String>()
            RelationshipType type = RelationshipType.readByName("relatedTo")
            params.put("""source""", """gorm://org.modelcatalogue.core.DataElement:$de1.id""")
            params.put("""destination""", """gorm://org.modelcatalogue.core.DataElement:$de2.id""")
            params.put("""type""", """gorm://org.modelcatalogue.core.RelationshipType:$type.id""")
            params.put("""matchScore""", """98""")
            params.put("""matchOn""", """ElementName""")
            params.put("""message""", """test match""")


            actionService.create(params, batch, CreateMatch)
        then:
            batch
            batch.actions

    }

}
