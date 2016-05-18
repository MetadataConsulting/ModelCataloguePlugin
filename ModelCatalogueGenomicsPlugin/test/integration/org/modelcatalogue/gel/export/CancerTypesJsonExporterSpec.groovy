package org.modelcatalogue.gel.export

import groovy.json.JsonOutput
import org.modelcatalogue.core.DataClass

/**
 * Created by rickrees on 31/03/2016.
 */
class CancerTypesJsonExporterSpec extends AbstractCancerTypesExporterSpec {


    def "export Cancer types to json"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName(ROOT_CANCER_TYPE)
        new CancerTypesJsonExporter(out).exportCancerTypesAsJson(model)

        def response = JsonOutput.prettyPrint(new String(out.toByteArray()))
        printOutput response

        def expected = JsonOutput.prettyPrint(expectedSubtypesJson)
        printOutput expected

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
                                "id":"$cancer_type_1_adult_subType_id",
                                "subType": "Adult Glioma subtypes 1.1",
                                "subTypes": [
                                {
                                    "id":"$cancer_type_1_adult_glioma_enum_id",
                                    "enumeratedType": "Adult Glioma Enum",
                                    "enums": [
                                        {
                                            "enum": "one",
                                            "description": "1"
                                        },
                                        {
                                            "enum": "two",
                                            "description": "2"
                                        }
                                    ]
                                }
                            ]
                            }
                        ],
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
                        "subTypes": [
                          {
                            "id": "$cancer_type_2_some_cancer_subType21_id",
                            "subType": "some other cancer subtypes 2.1",
                            "subTypes": [
                            ]
                          },
                            {
                                "id":"$cancer_type_2_some_cancer_subType22_id",
                                "subType": "some other cancer subtypes 2.2",
                                "subTypes": [
                                {
                                    "id":"$cancer_type_2_some_cancer_enum_id",
                                    "enumeratedType": "Some Cancer Enum",
                                    "enums": [
                                        {
                                            "enum": "first",
                                            "description": "a"
                                        },
                                        {
                                            "enum": "second",
                                            "description": "b"
                                        }
                                    ]
                                }
                             ]
                             }
                        ],
                        "presentations": [
                            {
                                "presentation": "Cancer presentations 21",
                                "description": "some cancer presentation description21"
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
