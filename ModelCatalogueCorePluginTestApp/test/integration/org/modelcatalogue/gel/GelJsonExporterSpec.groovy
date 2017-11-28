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
    public static final String NESTED_LEVEL_1_DATA_CLASS_NAME = 'rare disease subgroup 1.1'
    public static final String NESTED_LEVEL_2_DATA_CLASS_NAME = 'rare disease disorder 1.1.1'
    public static final String NESTED_LEVEL_3_DATA_CLASS_NAME = 'rare disease 1.1.1.1'
    public static final String NESTED_LEVEL_4_1_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 eligibility'
    public static final String NESTED_LEVEL_4_2_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 phenotypes'
    public static final String NESTED_LEVEL_4_3_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 guidance'
    public static final String NESTED_LEVEL_4_4_DATA_CLASS_NAME = 'rare disease disorder 1.1.1 clinical tests'

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {
            skip draft
            dataModel(name: DATA_MODEL_NAME) {
                dataClass(name: ROOT_DATA_CLASS_NAME) {
                    dataClass(name: NESTED_LEVEL_1_DATA_CLASS_NAME) {
                        dataClass(name: NESTED_LEVEL_2_DATA_CLASS_NAME) {
                            dataClass(name: NESTED_LEVEL_3_DATA_CLASS_NAME) {
                                dataClass(name: NESTED_LEVEL_4_1_DATA_CLASS_NAME, lastUpdated: new Date())
                                dataClass(name: NESTED_LEVEL_4_2_DATA_CLASS_NAME, lastUpdated: new Date()) {
                                    dataClass(name: 'hpo terms 1') { ext "OBO ID", "HP:111" }
                                    dataClass(name: 'hpo terms 2') { ext "OBO ID", "HP:222" }
                                    dataClass(name: 'hpo terms 3') { ext "OBO ID", "HP:333" }
                                    dataClass(name: 'hpo terms 4') { ext "OBO ID", "HP:444" }
                                }
                                dataClass(name: NESTED_LEVEL_4_3_DATA_CLASS_NAME, lastUpdated: new Date())
                                dataClass(name: NESTED_LEVEL_4_4_DATA_CLASS_NAME, lastUpdated: new Date()) {
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
    }

    def "export model to json"() {
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
        final String NESTED_LEVEL_1_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_1_DATA_CLASS_NAME).getLatestVersionId() ?: DataClass.findByName(NESTED_LEVEL_1_DATA_CLASS_NAME).getId()
        final String NESTED_LEVEL_2_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_2_DATA_CLASS_NAME).getLatestVersionId() ?: DataClass.findByName(NESTED_LEVEL_2_DATA_CLASS_NAME).getId()
        final String NESTED_LEVEL_3_DATA_CLASS_NAME_ID = DataClass.findByName(NESTED_LEVEL_3_DATA_CLASS_NAME).getLatestVersionId() ?: DataClass.findByName(NESTED_LEVEL_3_DATA_CLASS_NAME).getId()
        final String clinical_test1_id = DataClass.findByName('clinical test1').getCombinedVersion()  ?: DataClass.findByName('clinical test1').getId()
        final String clinical_test2_id = DataClass.findByName('clinical test2').getCombinedVersion()  ?: DataClass.findByName('clinical test2').getId()
        final String clinical_test3_id = DataClass.findByName('clinical test3').getCombinedVersion()  ?: DataClass.findByName('clinical test3').getId()
        final String clinical_test4_id = DataClass.findByName('clinical test4').getCombinedVersion()  ?: DataClass.findByName('clinical test4').getId()

        return """
            {
               "DiseaseGroups":[
                  {
                     "id":$NESTED_LEVEL_1_DATA_CLASS_NAME_ID,
                     "name":"rare disease subgroup 1.1",
                     "subGroups":[
                        {
                           "id":$NESTED_LEVEL_2_DATA_CLASS_NAME_ID,
                           "name":"rare disease disorder 1.1.1",
                           "specificDisorders":[
                              {
                                 "id":$NESTED_LEVEL_3_DATA_CLASS_NAME_ID,
                                 "name":"rare disease 1.1.1.1",
                                 "eligibilityQuestion":{
                                    "date":"$TODAY",
                                    "version":"1"
                                 },
                                 "shallowPhenotypes": [
                                    {
                                      "name": "hpo terms 1",
                                      "id": "HP:111"
                                    },
                                    {
                                        "name": "hpo terms 2",
                                        "id": "HP:222"
                                    },
                                    {
                                        "name": "hpo terms 3",
                                        "id": "HP:333"
                                    },
                                    {
                                        "name": "hpo terms 4",
                                        "id": "HP:444"
                                    }
                                ],
                                "tests": [
                                    {
                                        "name": "clinical test1",
                                        "id": "$clinical_test1_id"
                                    },
                                    {
                                        "name": "clinical test2",
                                        "id": "$clinical_test2_id"
                                    },
                                    {
                                        "name": "clinical test3",
                                        "id": "$clinical_test3_id"
                                    },
                                    {
                                        "name": "clinical test4",
                                        "id": "$clinical_test4_id"
                                    }
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
