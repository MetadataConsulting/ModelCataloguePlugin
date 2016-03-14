package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.core.util.Metadata

import static org.modelcatalogue.core.util.test.FileOpener.open

import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

class DataModelToDocxExporterSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService
    DataClassService dataClassService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "export model to docx"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        DataModel model = buildTestModel()

        new DataModelToDocxExporter(DataModel.get(model.id), dataClassService).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

    def "export model to docx with image"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        DataModel model = buildTestModel()

        def testTemplate = {
            'document' font: [color: '#000000']
        }

        new DataModelToDocxExporter(DataModel.get(model.id), dataClassService, testTemplate,
                "http://www.genomicsengland.co.uk/wp-content/uploads/2015/11/Genomics-England-logo-2015.png").export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }



    private DataModel buildTestModel() {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        Random random = new Random()
        List<DataType> domains = DataType.list()

        if (!domains) {
            for (int i in 1..10) {
                DataModel classification = new DataModel(name: "C4CTDE Classification ${System.currentTimeMillis()}").save(failOnError: true)
                new DataType(name: "C4CTDE Test Value Domain #${i}", dataModel: classification).save(failOnError: true)
            }
            domains = DataType.list()
        }

        builder.build {
            dataModel(name: 'C4CTDE') {
                description "This is a data model for testing DataModelToDocxExporter"

                dataClass (name: 'C4CTDE Root') {
                    for (int i in 1..10) {
                        dataClass name: "C4CTDE Model $i", {
                            description "This is a description for Model $i"

                            for (int j in 1..10) {
                                dataElement name: "C4CTDE Model $i Data Element $j", {
                                    description "This is a description for Model $i Data Element $j"
                                    DataType data = domains[random.nextInt(domains.size())]
                                    dataClass name: data.name, dataModel: data.dataModel ? data.dataModel.name : null
                                }
                            }
                            for (int j in 1..3) {
                                dataClass name: "C4CTDE Model $i Child Model $j", {
                                    description "This is a description for Model $i Child Model $j"

                                    for (int k in 1..3) {
                                        dataElement name: "C4CTDE Model $i Child Model $j Data Element $k", {
                                            description "This is a description for Model $i Child Model $j Data Element $k"
                                            DataType domain = domains[random.nextInt(domains.size())]
                                            dataType name: domain.name, dataModel: domain.dataModel ? domain.dataModel.name : null
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ext Metadata.OWNER, 'The Owner'
                ext Metadata.ORGANISATION, 'The Organisation'
                ext Metadata.AUTHORS, 'Author One, Author Two, Author Three'
                ext Metadata.REVIEWERS, 'Reviewer One, Reviewer Two, Reviewer Three'

            }
        }

        return DataModel.findByName('C4CTDE')

    }
}
