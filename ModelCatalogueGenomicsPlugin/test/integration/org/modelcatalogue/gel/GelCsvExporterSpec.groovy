package org.modelcatalogue.gel

import grails.test.spock.IntegrationSpec
import groovy.json.JsonOutput
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/**
 * Created by dexterawoyemi on 21/03/2016.
 */
class GelCsvExporterSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: 'testDataModel1') {
                dataClass(name: 'rare disease group 1') {
                    dataClass(name: 'rare disease \' subgroup 1.1') {
                        dataClass(name: 'rare disease disorder 1.1.1 Eligibility', lastUpdated: new Date())
                        dataClass(name: 'rare disease disorder 1.1.1 Phenotypes', lastUpdated: new Date()) {
                            dataClass(name: 'test hpo terms 1')
                            dataClass(name: 'test hpo terms 2')
                            dataClass(name: 'test hpo terms 3')
                            dataClass(name: 'test hpo terms 4')
                        }
                        dataClass(name: 'rare disease disorder 1.1.1 clinical Tests', lastUpdated: new Date()) {
                            dataClass(name: 'clinical test1')
                            dataClass(name: 'clinical test2')
                            dataClass(name: 'clinical test3')
                            dataClass(name: 'clinical test4')
                        }
                    }
                }
            }
        }

    }


    def "export model to csv"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName('rare disease group 1')
        new GelCsvExporter(out).printDiseaseOntology(model)

        String csv = new String(out.toByteArray())

//        csv.eachLine {
//            s -> println(s + '\n')
//        }

        then:
        noExceptionThrown()
        csv ==~ expectedCSV
    }

    private static getExpectedCSV() {
        def today = new Date().format("yyyy-MM-dd")
        def pattern = /id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Phenotype,Phenotype ID,Test,Test ID
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 Eligibility,,,,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 Phenotypes,.*,test hpo terms 1,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 Phenotypes,.*,test hpo terms 2,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 Phenotypes,.*,test hpo terms 3,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 Phenotypes,.*,test hpo terms 4,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 clinical Tests,.*,clinical test1,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 clinical Tests,.*,clinical test2,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 clinical Tests,.*,clinical test3,$today,,,,,
.*,rare disease ' subgroup 1.1,.*,rare disease disorder 1.1.1 clinical Tests,.*,clinical test4,$today,,,,,/
        return pattern
    }

}
