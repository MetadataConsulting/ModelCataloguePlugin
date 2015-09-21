package org.modelcatalogue.core.gel

import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ClassificationService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

class ClassificationToDocxExporterSpec extends IntegrationSpec {

    ElementService elementService
    ClassificationService classificationService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    @Rule TemporaryFolder temporaryFolder

    def "export model to docx"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        Classification classification = buildTestClassification()


        new ClassificationToDocxExporter(classification).export(file.newOutputStream())


        println file.absolutePath

        then:
        noExceptionThrown()

    }



    private Classification buildTestClassification() {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(classificationService, elementService)

        Random random = new Random()
        List<ValueDomain> domains = ValueDomain.list()

        if (!domains) {
            for (int i in 1.10) {
                new ValueDomain(name: "Test Value Domain #${i}").save(failOnError: true)
            }
            domains = ValueDomain.list()
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
                                ValueDomain domain = domains[random.nextInt(domains.size())]
                                while (!domain.classifications) {
                                    domain = domains[random.nextInt(domains.size())]
                                }
                                valueDomain name: domain.name, classification: domain.classifications.first().name
                            }
                        }
                        for (int j in 1..3) {
                            model name: "Model $i Child Model $j", {
                                description "This is a description for Model $i Child Model $j"

                                for (int k in 1..3) {
                                    dataElement name: "Model $i Child Model $j Data Element $k", {
                                        description "This is a description for Model $i Child Model $j Data Element $k"
                                        ValueDomain domain = domains[random.nextInt(domains.size())]
                                        while (!domain.classifications) {
                                            domain = domains[random.nextInt(domains.size())]
                                        }
                                        valueDomain name: domain.name, classification: domain.classifications.first().name
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }

        return Classification.findByName('C4CTDE')

    }

}
