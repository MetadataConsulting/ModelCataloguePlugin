package org.modelcatalogue.gel

import grails.test.spock.IntegrationSpec
import groovy.json.JsonOutput
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder


class GelJsonExporterSpec extends IntegrationSpec {

    public static final String DATA_MODEL_NAME = 'testDataModel1'
    public static final String ROOT_DATA_CLASS_NAME = 'rare disease group 1'
    public static final String NESTED_LEVEL_1_DATA_CLASS_NAME = 'rare disease \' subgroup 1.1'
    public static final String NESTED_LEVEL_2_DATA_CLASS_NAME = 'rare disease " disorder 1.1.1'
    public static final String NESTED_LEVEL_3_1_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 eligibility'
    public static final String NESTED_LEVEL_3_2_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 phenotypes'
    public static final String NESTED_LEVEL_3_3_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 clinical tests'

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: DATA_MODEL_NAME) {
                dataClass(name: ROOT_DATA_CLASS_NAME) {
                    dataClass(name: NESTED_LEVEL_1_DATA_CLASS_NAME) {
                        dataClass(name: NESTED_LEVEL_2_DATA_CLASS_NAME) {
                            dataClass(name: NESTED_LEVEL_3_1_DATA_CLASS_NAME, lastUpdated: new Date())
                            dataClass(name: NESTED_LEVEL_3_2_DATA_CLASS_NAME, lastUpdated: new Date()) {
                                dataClass(name: 'test hpo terms 1')
                                dataClass(name: 'test hpo terms 2')
                                dataClass(name: 'test hpo terms 3')
                                dataClass(name: 'test hpo terms 4')
                            }
                            dataClass(name: NESTED_LEVEL_3_3_DATA_CLASS_NAME, lastUpdated: new Date())
                            dataClass(name: NESTED_LEVEL_3_3_DATA_CLASS_NAME, lastUpdated: new Date()) {
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
        DataClass model = DataClass.findByName(ROOT_DATA_CLASS_NAME)
        new GelJsonExporter(out).printDiseaseOntology(model)

        String json = new String(out.toByteArray())

        println json

        def response = JsonOutput.prettyPrint(json)
        def expected = JsonOutput.prettyPrint(expectedJSON)

        then:
        noExceptionThrown()
        response == expected
    }

    private static String getExpectedJSON() {
        final String TODAY = new Date().format("yyyy-MM-dd")
        final Long NESTED_LEVEL_1_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_1_DATA_CLASS_NAME).getId()
        final Long NESTED_LEVEL_2_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_2_DATA_CLASS_NAME).getId()
        final Long NESTED_LEVEL_3_1_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_3_1_DATA_CLASS_NAME).getId()
        final Long NESTED_LEVEL_3_2_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_3_2_DATA_CLASS_NAME).getId()
        final Long NESTED_LEVEL_3_3_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_3_3_DATA_CLASS_NAME).getId()

        return """
            {
               "DiseaseGroups":[
                  {
                     "id":"$NESTED_LEVEL_1_DATA_CLASS_NAME_ID",
                     "name":"rare disease ' subgroup 1.1",
                     "subGroups":[
                        {
                           "id":"$NESTED_LEVEL_2_DATA_CLASS_NAME_ID",
                           "name":"rare disease \\" disorder 1.1.1",
                           "specificDisorders":[
                              {
                                 "id":"$NESTED_LEVEL_3_1_DATA_CLASS_NAME_ID",
                                 "name":"rare disease disorder 1.1.1 eligibility",
                                 "eligibilityQuestion":{
                                    "date":"$TODAY",
                                    "version":"1"
                                 },
                                 "shallowPhenotypes":[

                                 ],
                                 "tests":[

                                 ]
                              },
                              {
                                 "id":"$NESTED_LEVEL_3_2_DATA_CLASS_NAME_ID",
                                 "name":"rare disease disorder 1.1.1 phenotypes",
                                 "eligibilityQuestion":{
                                    "date":"$TODAY",
                                    "version":"1"
                                 },
                                 "shallowPhenotypes":[

                                 ],
                                 "tests":[

                                 ]
                              },
                              {
                                 "id":"$NESTED_LEVEL_3_3_DATA_CLASS_NAME_ID",
                                 "name":"rare disease disorder 1.1.1 clinical tests",
                                 "eligibilityQuestion":{
                                    "date":"$TODAY",
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
