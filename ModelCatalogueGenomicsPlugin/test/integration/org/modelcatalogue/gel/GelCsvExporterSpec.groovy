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
        csv == expectedCSV
    }

    private static String getExpectedCSV() {
        return """id,name,subGroup_id,subGroup_name,subGroup_specificDisorder_id,subGroup_specificDisorder_name,subGroup_specificDisorder_eligibilityQuestion_date,subGroup_specificDisorder_eligibilityQuestion_version,subGroup_specificDisorder_shallowPhenotype_name,subGroup_specificDisorder_shallowPhenotype_id,subGroup_specificDisorder_test_name,subGroup_specificDisorder_test_id
3,rare disease ' subgroup 1.1,4,rare disease disorder 1.1.1 Eligibility,,,,,,,,,
3,rare disease ' subgroup 1.1,5,rare disease disorder 1.1.1 Phenotypes,6,test hpo terms 1,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,5,rare disease disorder 1.1.1 Phenotypes,7,test hpo terms 2,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,5,rare disease disorder 1.1.1 Phenotypes,8,test hpo terms 3,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,5,rare disease disorder 1.1.1 Phenotypes,9,test hpo terms 4,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,10,rare disease disorder 1.1.1 clinical Tests,11,clinical test1,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,10,rare disease disorder 1.1.1 clinical Tests,12,clinical test2,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,10,rare disease disorder 1.1.1 clinical Tests,13,clinical test3,2016-03-22,1,,,,,
3,rare disease ' subgroup 1.1,10,rare disease disorder 1.1.1 clinical Tests,14,clinical test4,2016-03-22,1,,,,,"""
    }

}
