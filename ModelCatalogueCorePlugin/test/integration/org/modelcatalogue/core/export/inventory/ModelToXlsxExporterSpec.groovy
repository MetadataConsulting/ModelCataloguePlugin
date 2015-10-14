package org.modelcatalogue.core.export.inventory

import static org.modelcatalogue.core.util.test.FileOpener.open

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

class ModelToXlsxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    ClassificationService classificationService
    ModelService modelService

    @Rule TemporaryFolder temporaryFolder

    def "export model to excel"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        Model model = buildTestModel()


        new ModelToXlsxExporter(model, modelService).export(file.newOutputStream())


        open file

        then:
        noExceptionThrown()

    }



    private Model buildTestModel() {
        org.modelcatalogue.core.util.test.TestDataHelper.initFreshDb(sessionFactory, 'excel_report.sql') {
            initRelationshipTypes()

            DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(classificationService, elementService)

            Random random = new Random()
            List<ValueDomain> domains = ValueDomain.list()

            if (!domains) {
                for (int i in 1..10) {
                    ValueDomain domain = new ValueDomain(name: "Test Value Domain #${i}").save(failOnError: true)
                    Classification classification = new Classification(name: "Classification ${System.currentTimeMillis()}").save(failOnError: true)
                    classification.addToClassifies domain
                }
                domains = ValueDomain.list()
            }

            builder.build {
                classification(name: 'C4CTXE') {
                    description "This is a classification for testing ClassificationToDocxExporter"

                    model (name: 'C4CTXE Root') {
                        for (int i in 1..10) {
                            model name: "Model $i", {
                                description "This is a description for Model $i"

                                for (int j in 1..10) {
                                    dataElement name: "Model $i Data Element $j", {
                                        description "This is a description for Model $i Data Element $j"
                                        ValueDomain domain = domains[random.nextInt(domains.size())]
                                        valueDomain name: domain.name, classification: domain.classifications ? domains.classifications.first().name : null
                                    }
                                }
                                for (int j in 1..3) {
                                    model name: "Model $i Child Model $j", {
                                        description "This is a description for Model $i Child Model $j"

                                        for (int k in 1..3) {
                                            dataElement name: "Model $i Child Model $j Data Element $k", {
                                                description "This is a description for Model $i Child Model $j Data Element $k"
                                                ValueDomain domain = domains[random.nextInt(domains.size())]
                                                valueDomain name: domain.name, classification: domain.classifications ? domains.classifications.first().name : null
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


        return Model.findByName('C4CTXE Root')

    }

}
