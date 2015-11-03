package org.modelcatalogue.core.export.inventory

import grails.test.spock.IntegrationSpec

import static org.modelcatalogue.core.util.test.FileOpener.open

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

class ModelToXlsxExporterSpec extends IntegrationSpec {

    ElementService elementService
    ClassificationService classificationService
    ModelService modelService
    InitCatalogueService initCatalogueService

    @Rule TemporaryFolder temporaryFolder

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

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
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(classificationService, elementService)

        Random random = new Random()
        List<ValueDomain> domains = ValueDomain.list()

        if (!domains) {
            for (int i in 1..10) {
                ValueDomain domain = new ValueDomain(name: "C4CTXE Test Value Domain #${i}").save(failOnError: true)
                Classification classification = new Classification(name: "C4CTXE Classification ${System.currentTimeMillis()}").save(failOnError: true)
                classification.addToClassifies domain
            }
            domains = ValueDomain.list()
        }

        builder.build {
            classification(name: 'C4CTXE') {
                description "This is a classification for testing ClassificationToDocxExporter"

                model(name: 'C4CTXE Root') {
                    for (int i in 1..10) {
                        model name: "C4CTXE Model $i", {
                            description "This is a description for Model $i"

                            for (int j in 1..10) {
                                dataElement name: "C4CTXE Model $i Data Element $j", {
                                    description "This is a description for Model $i Data Element $j"
                                    ValueDomain domain = domains[random.nextInt(domains.size())]
                                    valueDomain name: domain.name, classification: domain.classifications ? domains.classifications.first().name : null
                                }
                            }
                            for (int j in 1..3) {
                                model name: "C4CTXE Model $i Child Model $j", {
                                    description "This is a description for Model $i Child Model $j"

                                    for (int k in 1..3) {
                                        dataElement name: "C4CTXE Model $i Child Model $j Data Element $k", {
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

        return Model.findByName('C4CTXE Root')

    }

}
