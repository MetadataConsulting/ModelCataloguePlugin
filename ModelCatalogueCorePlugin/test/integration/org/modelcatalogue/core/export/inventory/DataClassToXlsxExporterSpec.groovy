package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.core.util.test.TestDataHelper

import static org.modelcatalogue.core.util.test.FileOpener.open

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

class DataClassToXlsxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService

    @Rule TemporaryFolder temporaryFolder

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
        TestDataHelper.initFreshDb(sessionFactory, 'excel_report.sql') {
            initRelationshipTypes()

            DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

            Random random = new Random()
            List<DataType> domains = DataType.list()

            if (!domains) {
                for (int i in 1..10) {
                    DataType domain = new DataType(name: "Test Data Type #${i}").save(failOnError: true)
                    DataModel data = new DataModel(name: "Data Model ${System.currentTimeMillis()}").save(failOnError: true)
                    data.addToDeclares domain
                }
                domains = DataType.list()
            }

            builder.build {
                dataModel(name: 'C4CTXE') {
                    description "This is a data model for testing DataClassToXlsxExporter"

                    dataClass (name: 'C4CTXE Root') {
                        for (int i in 1..10) {
                            dataClass name: "Model $i", {
                                description "This is a description for Model $i"

                                for (int j in 1..10) {
                                    dataElement name: "Model $i Data Element $j", {
                                        description "This is a description for Model $i Data Element $j"
                                        DataType type = domains[random.nextInt(domains.size())]
                                        while (!type.dataModels) {
                                            type = domains[random.nextInt(domains.size())]
                                        }
                                        dataType name: type.name, dataModel: type.dataModels.first().name
                                    }
                                }
                                for (int j in 1..3) {
                                    dataClass name: "Model $i Child Model $j", {
                                        description "This is a description for Model $i Child Model $j"

                                        for (int k in 1..3) {
                                            dataElement name: "Model $i Child Model $j Data Element $k", {
                                                description "This is a description for Model $i Child Model $j Data Element $k"
                                                DataType type = domains[random.nextInt(domains.size())]
                                                while (!type.dataModels) {
                                                    type = domains[random.nextInt(domains.size())]
                                                }
                                                dataType name: type.name, dataModel: type.dataModels.first().name
                                            }
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
