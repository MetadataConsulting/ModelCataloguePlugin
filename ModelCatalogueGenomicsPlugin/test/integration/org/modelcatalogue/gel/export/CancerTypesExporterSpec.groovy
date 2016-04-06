package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import groovy.json.JsonOutput
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/**
 * Created by rickrees on 31/03/2016.
 */
class CancerTypesExporterSpec extends IntegrationSpec {
    public static final String DATA_MODEL_NAME = 'testDataModel1'
    public static final String ROOT_CANCER_TYPE = 'cancer types'

    public static final String ADULT_GLIOMA_TYPE1 = 'Adult Glioma'
    public static final String ADULT_GLIOMA_SUBTYPE_1 = 'Adult Glioma subtypes 1.1'
    public static final String ADULT_GLIOMA_PRESENTATION_1 = 'Adult Glioma presentations 1'

    public static final String SOME_CANCER_TYPE2 = 'Some other Cancer Type 2'
    public static final String SOME_CANCER_SUBTYPE_21 = 'some other cancer subtypes 2.1'
    public static final String SOME_CANCER_SUBTYPE_22 = 'some other cancer subtypes 2.2'
    public static final String SOME_CANCER_PRESENTATION_2 = 'Cancer presentations 2'
    public static final String SOME_CANCER_PRESENTATION_22 = 'Cancer presentations 22'

    Long cancer_type_1_adult_glioma_id
    Long cancer_type_2_some_cancer_id

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: DATA_MODEL_NAME) {
                dataClass(name: ROOT_CANCER_TYPE) {
                    dataClass(name: ADULT_GLIOMA_TYPE1) {
                        dataElement name: ADULT_GLIOMA_SUBTYPE_1, {
                            description "adult glioma description"
                            dataType name: "Data Element11", enumerations: [    //shouldn't display this level
                                'one': '1',
                                'two': '2',
                            ]

                        }

                        dataElement(name: ADULT_GLIOMA_PRESENTATION_1) {
                            description "adult glioma presentation description"
                            dataType name: "Data Element12"
                        }
                    }
                    dataClass(name: SOME_CANCER_TYPE2) {
                        dataElement(name: SOME_CANCER_SUBTYPE_21) {
                            description "some cancer description subtype21"
                            dataType name: "Data Element21"
                        }
                        dataElement(name: SOME_CANCER_SUBTYPE_22) {
                            description "some cancer description subtype22"
                            dataType name: "Data Element22"
                        }
                        dataElement(name: SOME_CANCER_PRESENTATION_2) {
                            description "some cancer presentation description2"
                            dataType name: "Data Element23"
                        }
                        dataElement(name: SOME_CANCER_PRESENTATION_22) {
                            description "some cancer presentation description22"
                            dataType name: "Data Element24"
                        }
                    }
                }
            }
        }

        cancer_type_1_adult_glioma_id = DataClass.findByName(ADULT_GLIOMA_TYPE1).getId()
        cancer_type_2_some_cancer_id = DataClass.findByName(SOME_CANCER_TYPE2).getId()
    }


    def "export Cancer types to json"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName(ROOT_CANCER_TYPE)
        new CancerTypesExporter(out).exportCancerTypesAsJson(model)

        def response = JsonOutput.prettyPrint(new String(out.toByteArray()))
        println "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
        println response
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        def expected = JsonOutput.prettyPrint(expectedSubtypesJson)

        then:
        noExceptionThrown()
        response == expected
    }

    def "export Presentation types to json"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName(ROOT_CANCER_TYPE)
        new CancerTypesExporter(out).exportPresentationTypesAsJson(model)

        def response = JsonOutput.prettyPrint(new String(out.toByteArray()))
        println "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
        println response
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        def expected = JsonOutput.prettyPrint(expectedPresentationsJson)

        then:
        noExceptionThrown()
        response == expected
    }

    private String getExpectedSubtypesJson() {
        return """
            {
               "CancerTypes":[
                    {
                        "id":"$cancer_type_1_adult_glioma_id",
                        "type": "Adult Glioma",
                        "subTypes": [
                            {
                                "subType": "Adult Glioma subtypes 1.1",
                                "description": "adult glioma description"
                            }
                        ]
                    },
                    {
                        "id":"$cancer_type_2_some_cancer_id",
                        "type": "Some other Cancer Type 2",
                        "subTypes": [
                            {
                                "subType": "some other cancer subtypes 2.1",
                                "description": "some cancer description subtype21"
                            },
                            {
                                "subType": "some other cancer subtypes 2.2",
                                "description": "some cancer description subtype22"
                            }
                        ]
                    }
                ]
            }
        """
    }

    //don't know what this should look like - this is just a guess...
    private String getExpectedPresentationsJson() {
        return """
            {
               "CancerTypes":[
                    {
                        "id":"$cancer_type_1_adult_glioma_id",
                        "type": "Adult Glioma",
                        "presentations": [
                            {
                                "presentation": "Adult Glioma presentations 1",
                                "description": "adult glioma presentation description"
                            }
                        ]
                    },
                    {
                        "id":"$cancer_type_2_some_cancer_id",
                        "type": "Some other Cancer Type 2",
                        "presentations": [
                            {
                                "presentation": "Cancer presentations 2",
                                "description": "some cancer presentation description2"
                            },                            {
                                "presentation": "Cancer presentations 22",
                                "description": "some cancer presentation description22"
                            }
                        ]
                    }
                ]
            }
        """
    }

}
