package org.modelcatalogue.core.gel

import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

class DataModelToDocxExporterSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    @Rule TemporaryFolder temporaryFolder

    def "export model to docx"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        DataModel dataModel = buildTestDataModel()


        new DataModelToDocxExporter(dataModel).export(file.newOutputStream())


        println file.absolutePath

        then:
        noExceptionThrown()

    }



    private DataModel buildTestDataModel() {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        Random random = new Random()
        List<DataType> dataTypes = DataType.list()

        if (!dataTypes) {
            for (int i in 1..10) {
                DataType domain = new DataType(name: "Test Data Type #${i}").save(failOnError: true)
                DataModel classification = new DataModel(name: "Data Model ${System.currentTimeMillis()}").save(failOnError: true)
                classification.addToDeclares domain
            }
            dataTypes = DataType.list()
        }

        builder.build {
            classification(name: 'C4CTDE') {
                description "This is a classification for testing ClassificationToDocxExporter"

                for (int i in 1..10) {
                    model name: "Model $i", {
                        description "This is a description for Model $i"

                        for (int j in 1..10) {
                            dataElement name: "Model $i Data Element $j", {
                                description "This is a description for Model $i Data Element $j"
                                DataType domain = dataTypes[random.nextInt(dataTypes.size())]
                                while (!domain.dataModels) {
                                    domain = dataTypes[random.nextInt(dataTypes.size())]
                                }
                                dataType name: domain.name, dataModel: domain.dataModels.first().name
                            }
                        }
                        for (int j in 1..3) {
                            model name: "Model $i Child Model $j", {
                                description "This is a description for Model $i Child Model $j"

                                for (int k in 1..3) {
                                    dataElement name: "Model $i Child Model $j Data Element $k", {
                                        description "This is a description for Model $i Child Model $j Data Element $k"
                                        DataType domain = dataTypes[random.nextInt(dataTypes.size())]
                                        while (!domain.dataModels) {
                                            domain = dataTypes[random.nextInt(dataTypes.size())]
                                        }
                                        dataType name: domain.name, dataModel: domain.dataModels.first().name
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }

        return DataModel.findByName('C4CTDE')

    }

}
