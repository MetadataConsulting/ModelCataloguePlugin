package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass
import spock.lang.Ignore

/**
 * Created by rickrees on 10/03/2016.
 */
@Ignore

class RareDiseasesJsonExporterSpec extends AbstractRareDiseasesExporterSpec {

    def "export eligibility criteria to json"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        buildTestModel(true)
        findDataIds()
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')

        new RareDiseasesJsonExporter(out).exportEligibilityCriteriaAsJson(dataClass)

        def json = out.toString()

        println json
        then:
        noExceptionThrown()
        json == expectedJson
    }


    private String getExpectedJson() {
        return """{
    "DiseaseGroups": [
        {
            "id": "$level2_id_1",
            "name": "Disorder >>>1<<< Level2",
            "subGroups": [
                {
                    "id": "$level3_id_1",
                    "name": "Disorder >>>1<<< SubCondition Level3 Model Data Element 1",
                    "specificDisorders": [
                        {
                            "id": "$level4_id_1",
                            "name": "Disorder >>1<< heading Level4 Model Data Element 1",
                            "disorderCriteria": [
                                {
                                    "id": "$level5_id_1",
                                    "name": "Disorder >1< Eligibility Level5 Model 1 Data Element 1",
                                    "eligibilityCriteria": [
                                        {
                                            "id": "$level6_inclusion_1",
                                            "name": "Inclusion criteria name 1 1",
                                            "description": "Inclusion criteria description  1 1"
                                        },
                                        {
                                            "id": "$level6_exclusion_1",
                                            "name": "Exclusion criteria name 1 1",
                                            "description": "Exclusion criteria description  1 1"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_1",
                                            "name": "Prior Genetic testing name 1 1",
                                            "description": "Prior Genetic testing description  1 1"
                                        },
                                        {
                                            "id": "$level6_prior_genes_1",
                                            "name": "Prior testing genes name 1 1",
                                            "description": "Prior testing genes description  1 1"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    "id": "$level3_id_2",
                    "name": "Disorder >>>1<<< SubCondition Level3 Model Data Element 2",
                    "specificDisorders": [
                        {
                            "id": "$level4_id_2",
                            "name": "Disorder >>1<< heading Level4 Model Data Element 2",
                            "disorderCriteria": [
                                {
                                    "id": "$level5_id_2",
                                    "name": "Disorder >1< Eligibility Level5 Model 1 Data Element 2",
                                    "eligibilityCriteria": [
                                        {
                                            "id": "$level6_inclusion_2",
                                            "name": "Inclusion criteria name 1 2",
                                            "description": "Inclusion criteria description  1 2"
                                        },
                                        {
                                            "id": "$level6_exclusion_2",
                                            "name": "Exclusion criteria name 1 2",
                                            "description": "Exclusion criteria description  1 2"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_2",
                                            "name": "Prior Genetic testing name 1 2",
                                            "description": "Prior Genetic testing description  1 2"
                                        },
                                        {
                                            "id": "$level6_prior_genes_2",
                                            "name": "Prior testing genes name 1 2",
                                            "description": "Prior testing genes description  1 2"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            "id": "$level2_id_2",
            "name": "Disorder >>>2<<< Level2",
            "subGroups": [
                {
                    "id": "$level3_id_3",
                    "name": "Disorder >>>2<<< SubCondition Level3 Model Data Element 1",
                    "specificDisorders": [
                        {
                            "id": "$level4_id_3",
                            "name": "Disorder >>2<< heading Level4 Model Data Element 1",
                            "disorderCriteria": [
                                {
                                    "id": "$level5_id_3",
                                    "name": "Disorder >2< Eligibility Level5 Model 2 Data Element 1",
                                    "eligibilityCriteria": [
                                        {
                                            "id": "$level6_inclusion_3",
                                            "name": "Inclusion criteria name 2 1",
                                            "description": "Inclusion criteria description  2 1"
                                        },
                                        {
                                            "id": "$level6_exclusion_3",
                                            "name": "Exclusion criteria name 2 1",
                                            "description": "Exclusion criteria description  2 1"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_3",
                                            "name": "Prior Genetic testing name 2 1",
                                            "description": "Prior Genetic testing description  2 1"
                                        },
                                        {
                                            "id": "$level6_prior_genes_3",
                                            "name": "Prior testing genes name 2 1",
                                            "description": "Prior testing genes description  2 1"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    "id": "$level3_id_4",
                    "name": "Disorder >>>2<<< SubCondition Level3 Model Data Element 2",
                    "specificDisorders": [
                        {
                            "id": "$level4_id_4",
                            "name": "Disorder >>2<< heading Level4 Model Data Element 2",
                            "disorderCriteria": [
                                {
                                    "id": "$level5_id_4",
                                    "name": "Disorder >2< Eligibility Level5 Model 2 Data Element 2",
                                    "eligibilityCriteria": [
                                        {
                                            "id": "$level6_inclusion_4",
                                            "name": "Inclusion criteria name 2 2",
                                            "description": "Inclusion criteria description  2 2"
                                        },
                                        {
                                            "id": "$level6_exclusion_4",
                                            "name": "Exclusion criteria name 2 2",
                                            "description": "Exclusion criteria description  2 2"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_4",
                                            "name": "Prior Genetic testing name 2 2",
                                            "description": "Prior Genetic testing description  2 2"
                                        },
                                        {
                                            "id": "$level6_prior_genes_4",
                                            "name": "Prior testing genes name 2 2",
                                            "description": "Prior testing genes description  2 2"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}"""
    }

}
