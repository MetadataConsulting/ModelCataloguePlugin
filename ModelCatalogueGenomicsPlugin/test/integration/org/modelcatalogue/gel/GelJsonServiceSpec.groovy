package org.modelcatalogue.gel

import grails.test.spock.IntegrationSpec
import groovy.json.JsonOutput
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder


class GelJsonServiceSpec extends IntegrationSpec {

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
                        dataClass(name: 'rare disease " disorder 1.1.1') {
                            dataClass(name: 'rare disease disorder 1.1.1 eligibility',lastUpdated: new Date())
                            dataClass(name: 'rare disease disorder 1.1.1 phenotypes',lastUpdated: new Date()) {
                                dataClass(name: 'test hpo terms 1')
                                dataClass(name: 'test hpo terms 2')
                                dataClass(name: 'test hpo terms 3')
                                dataClass(name: 'test hpo terms 4')
                            }
                            dataClass(name: 'rare disease disorder 1.1.1 clinical tests',lastUpdated: new Date()) {
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

    }


    def "export model to json"(){
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName('rare disease group 1')
        new GelJsonService().printDiseaseOntology(out, model)

        String json = new String(out.toByteArray())

        println json

        def response = JsonOutput.prettyPrint(json)
        def expected = JsonOutput.prettyPrint(expectedJSON)

        then:
        noExceptionThrown()
        response == expected
    }

    private static String getExpectedJSON() {
        def today = new Date().format("yyyy-MM-dd")
        return """
            {
               "DiseaseGroups":[
                  {
                     "id":"3",
                     "name":"rare disease ' subgroup 1.1",
                     "subGroups":[
                        {
                           "id":"4",
                           "name":"rare disease \\" disorder 1.1.1",
                           "specificDisorders":[
                              {
                                 "id":"5",
                                 "name":"rare disease disorder 1.1.1 eligibility",
                                 "eligibilityQuestion":{
                                    "date":"$today",
                                    "version":"1"
                                 },
                                 "shallowPhenotypes":[

                                 ],
                                 "tests":[

                                 ]
                              },
                              {
                                 "id":"6",
                                 "name":"rare disease disorder 1.1.1 phenotypes",
                                 "eligibilityQuestion":{
                                    "date":"$today",
                                    "version":"1"
                                 },
                                 "shallowPhenotypes":[

                                 ],
                                 "tests":[

                                 ]
                              },
                              {
                                 "id":"11",
                                 "name":"rare disease disorder 1.1.1 clinical tests",
                                 "eligibilityQuestion":{
                                    "date":"$today",
                                    "version":"1"
                                 },
                                 "shallowPhenotypes":[

                                 ],
                                 "tests":[

                                 ]
                              }
                           ]
                        }
                     ]
                  }
               ]
            }
        """
    }

}
