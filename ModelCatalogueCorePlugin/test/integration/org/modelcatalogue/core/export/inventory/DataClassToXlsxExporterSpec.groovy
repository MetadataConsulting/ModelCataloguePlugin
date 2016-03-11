package org.modelcatalogue.core.export.inventory

import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

import static org.modelcatalogue.core.util.test.FileOpener.open

class DataClassToXlsxExporterSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    InitCatalogueService initCatalogueService

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    def "export model to excel"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        DataClass model = buildTestModel()
        
        new DataClassToXlsxExporter(model, dataClassService).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

    private DataClass buildTestModel() {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        Random random = new Random()
        List<DataType> domains = DataType.list()

        if (!domains) {
            for (int i in 1..10) {
                DataModel data = new DataModel(name: "Data Model ${System.currentTimeMillis()}").save(failOnError: true)
                DataType domain = new DataType(name: "Test Data Type #${i}", dataModel: data).save(failOnError: true)
            }
            domains = DataType.list()
        }

        builder.build {
            dataModel(name: 'C4CTXE') {
                description "This is a data model for testing DataClassToXlsxExporter"

                dataClass(name: 'C4CTXE Root') {
                    for (int i in 1..10) {
                        dataClass name: "C4CTXE Model $i", {
                            description "This is a description for Model $i"

                            for (int j in 1..10) {
                                dataElement name: "C4CTXE Model $i Data Element $j", {
                                    description "This is a description for Model $i Data Element $j"
                                    DataType type = domains[random.nextInt(domains.size())]
                                    dataType name: type.name, dataModel: type.dataModel ? type.dataModel.name : null
                                }
                            }
                            for (int j in 1..3) {
                                dataClass name: "C4CTXE Model $i Child Model $j", {
                                    description "This is a description for Model $i Child Model $j"

                                    for (int k in 1..3) {
                                        dataElement name: "C4CTXE Model $i Child Model $j Data Element $k", {
                                            description "This is a description for Model $i Child Model $j Data Element $k"
                                            DataType type = domains[random.nextInt(domains.size())]
                                            dataType name: type.name, dataModel: type.dataModel ? type.dataModel.name : null
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return DataClass.findByName('C4CTXE Root')
    }
}
