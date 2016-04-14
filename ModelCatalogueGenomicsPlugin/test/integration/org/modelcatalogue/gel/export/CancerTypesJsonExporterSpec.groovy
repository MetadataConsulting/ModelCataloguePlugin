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
        new CancerTypesJsonExporter(out).exportPresentationTypesAsJson(model)

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
