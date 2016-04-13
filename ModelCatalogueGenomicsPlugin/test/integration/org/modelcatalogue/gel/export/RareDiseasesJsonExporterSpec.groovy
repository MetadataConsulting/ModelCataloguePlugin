package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass

/**
 * Created by rickrees on 10/03/2016.
 */
class RareDiseasesJsonExporterSpec extends AbstractRareDiseasesExporterSpec {

    String level2_id_1,level2_id_2
    String level3_id_1, level3_id_2, level3_id_3, level3_id_4
    String level4_id_1, level4_id_2, level4_id_3, level4_id_4
    String level5_id_1, level5_id_2, level5_id_3, level5_id_4
    String level6_inclusion_1,level6_inclusion_2,level6_inclusion_3,level6_inclusion_4
    String level6_exclusion_1,level6_exclusion_2,level6_exclusion_3,level6_exclusion_4
    String level6_priorGenetic_1,level6_priorGenetic_2,level6_priorGenetic_3,level6_priorGenetic_4
    String level6_prior_genes_1,level6_prior_genes_2,level6_prior_genes_3,level6_prior_genes_4


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

    //this is painful...why did I make the test data so big?
    def findDataIds() {
        level2_id_1 = DataClass.findByNameIlike("Disorder%1%Level2").combinedVersion
        level2_id_2 = DataClass.findByNameIlike("Disorder%2%Level2").combinedVersion

        level3_id_1 = DataClass.findByNameIlike("Disorder%1%Level3%1").combinedVersion
        level3_id_2 = DataClass.findByNameIlike("Disorder%1%Level3%2").combinedVersion
        level3_id_3 = DataClass.findByNameIlike("Disorder%2%Level3%1").combinedVersion
        level3_id_4 = DataClass.findByNameIlike("Disorder%2%Level3%2").combinedVersion

        level4_id_1 = DataClass.findByNameIlike("Disorder%1%Level4%1").combinedVersion
        level4_id_2 = DataClass.findByNameIlike("Disorder%1%Level4%2").combinedVersion
        level4_id_3 = DataClass.findByNameIlike("Disorder%2%Level4%1").combinedVersion
        level4_id_4 = DataClass.findByNameIlike("Disorder%2%Level4%2").combinedVersion

        level5_id_1 = DataClass.findByNameIlike("Disorder%1%Level5%1").combinedVersion
        level5_id_2 = DataClass.findByNameIlike("Disorder%1%Level5%2").combinedVersion
        level5_id_3 = DataClass.findByNameIlike("Disorder%2%Level5%1").combinedVersion
        level5_id_4 = DataClass.findByNameIlike("Disorder%2%Level5%2").combinedVersion

        level6_inclusion_1 = DataClass.findByNameIlike("Inclusion%1 1").combinedVersion
        level6_inclusion_2 = DataClass.findByNameIlike("Inclusion%1 2").combinedVersion
        level6_inclusion_3 = DataClass.findByNameIlike("Inclusion%2 1").combinedVersion
        level6_inclusion_4 = DataClass.findByNameIlike("Inclusion%2 2").combinedVersion

        level6_exclusion_1 = DataClass.findByNameIlike("Exclusion%1 1").combinedVersion
        level6_exclusion_2 = DataClass.findByNameIlike("Exclusion%1 2").combinedVersion
        level6_exclusion_3 = DataClass.findByNameIlike("Exclusion%2 1").combinedVersion
        level6_exclusion_4 = DataClass.findByNameIlike("Exclusion%2 2").combinedVersion

        level6_priorGenetic_1 = DataClass.findByNameIlike("Prior%Genetic%1 1").combinedVersion
        level6_priorGenetic_2 = DataClass.findByNameIlike("Prior%Genetic%1 2").combinedVersion
        level6_priorGenetic_3 = DataClass.findByNameIlike("Prior%Genetic%2 1").combinedVersion
        level6_priorGenetic_4 = DataClass.findByNameIlike("Prior%Genetic%2 2").combinedVersion

        level6_prior_genes_1 = DataClass.findByNameIlike("Prior%genes%1 1").combinedVersion
        level6_prior_genes_2 = DataClass.findByNameIlike("Prior%genes%1 2").combinedVersion
        level6_prior_genes_3 = DataClass.findByNameIlike("Prior%genes%2 1").combinedVersion
        level6_prior_genes_4 = DataClass.findByNameIlike("Prior%genes%2 2").combinedVersion
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
                                            "name": "Inclusion criteria name 1 1"
                                        },
                                        {
                                            "id": "$level6_exclusion_1",
                                            "name": "Exclusion criteria name 1 1"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_1",
                                            "name": "Prior Genetic testing name 1 1"
                                        },
                                        {
                                            "id": "$level6_prior_genes_1",
                                            "name": "Prior testing genes name 1 1"
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
                                            "name": "Inclusion criteria name 1 2"
                                        },
                                        {
                                            "id": "$level6_exclusion_2",
                                            "name": "Exclusion criteria name 1 2"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_2",
                                            "name": "Prior Genetic testing name 1 2"
                                        },
                                        {
                                            "id": "$level6_prior_genes_2",
                                            "name": "Prior testing genes name 1 2"
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
                                            "name": "Inclusion criteria name 2 1"
                                        },
                                        {
                                            "id": "$level6_exclusion_3",
                                            "name": "Exclusion criteria name 2 1"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_3",
                                            "name": "Prior Genetic testing name 2 1"
                                        },
                                        {
                                            "id": "$level6_prior_genes_3",
                                            "name": "Prior testing genes name 2 1"
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
                                            "name": "Inclusion criteria name 2 2"
                                        },
                                        {
                                            "id": "$level6_exclusion_4",
                                            "name": "Exclusion criteria name 2 2"
                                        },
                                        {
                                            "id": "$level6_priorGenetic_4",
                                            "name": "Prior Genetic testing name 2 2"
                                        },
                                        {
                                            "id": "$level6_prior_genes_4",
                                            "name": "Prior testing genes name 2 2"
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
