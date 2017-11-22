package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import org.hibernate.SessionFactory
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.ddl.DataDefinitionLanguage
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.TestDataHelper
import org.modelcatalogue.gel.GenomicsService
import org.modelcatalogue.gel.RareDiseaseCsvExporter

/**
 * Created by rickrees on 10/03/2016.
 */
class AbstractRareDiseasesExporterSpec extends AbstractIntegrationSpec {

    public static final String RARE_DISEASE_DATA_MODEL_NAME = 'Rare Diseases FirstTestSpec Data Model'
    AuditService auditService
    DataClassService dataClassService
    ElementService elementService
    DataModelService dataModelService
    PerformanceUtilService performanceUtilService
    GenomicsService genomicsService

    def setup() {
        initRelationshipTypes()
    }

    @Rule TemporaryFolder temporaryFolder

    String level1_id
    String level2_id_1,level2_id_2
    String level3_id_1, level3_id_2, level3_id_3, level3_id_4
    String level4_id_1, level4_id_2, level4_id_3, level4_id_4
    String level5_id_1, level5_id_2, level5_id_3, level5_id_4
    String level6_inclusion_1,level6_inclusion_2,level6_inclusion_3,level6_inclusion_4
    String level6_exclusion_1,level6_exclusion_2,level6_exclusion_3,level6_exclusion_4
    String level6_priorGenetic_1,level6_priorGenetic_2,level6_priorGenetic_3,level6_priorGenetic_4
    String level6_prior_genes_1,level6_prior_genes_2,level6_prior_genes_3,level6_prior_genes_4
    String level6_closing_1,level6_closing_2,level6_closing_3,level6_closing_4
    String level6_guidance_1,level6_guidance_2,level6_guidance_3,level6_guidance_4
    String[][][] phenotypeIds = new String[2][2][15]
    String[][][] clinicalTestIds = new String[2][2][5]
    String phenotypeLevel5Id_1, phenotypeLevel5Id_2, phenotypeLevel5Id_3, phenotypeLevel5Id_4
    String eligibLevel5Id_1, eligibLevel5Id_2, eligibLevel5Id_3, eligibLevel5Id_4
    String clinicalLevel5Id_1, clinicalLevel5Id_2, clinicalLevel5Id_3, clinicalLevel5Id_4
    String level5_guidanceid_1, level5_guidanceid_2, level5_guidanceid_3, level5_guidanceid_4
    String new_guidance_id

    // this model reflects the data mix of eligibility criteria, phenotypes & clinical tests that need to be extracted
    // by the two report generation methods it's a bit nasty looking but creates a fairly realistic model
    DataModel buildTestModel(boolean createPhenotypes) {
        TestDataHelper.initFreshDb(sessionFactory, 'rare-diseases.sql') {
            initRelationshipTypes()
            DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

            builder.build {
                skip draft
                dataModel(name: RARE_DISEASE_DATA_MODEL_NAME) {
                    description "This is a data model for testing Eligibility OR Phenotype and Clinicals tests exports"

                    dataClass (name: 'Dataclass Top Level 1 Root') {
                        for (int i in 1..2) {
                            dataClass name: "Disorder >>>$i<<< Level2", {
                                description "This is a description for Model $i"

                                for (int j in 1..2) {
                                    dataClass name: "Disorder >>>$i<<< SubCondition Level3 Model Data Element $j", {
                                        description "This is a description for Model $i Data Element $j"

                                        dataClass name: "Disorder >>$i<< heading Level4 Model Data Element $j", {
                                            description "Disorder >>$i<< heading Level4 description for Model Data Element $j"

                                            dataClass name: "Disorder >$i< Eligibility Level5 Model $i Data Element $j", {
                                                description "Disorder >$i< heading Level5 description for Model $i Data Element $j"

                                                dataClass name: "Inclusion criteria name $i $j", {
                                                    description "Inclusion criteria description  $i $j"
                                                }
                                                dataClass name: "Exclusion criteria name $i $j", {
                                                    description "Exclusion criteria description  $i $j"
                                                }
                                                dataClass name: "Prior Genetic testing name $i $j", {
                                                    description "Prior Genetic testing description  $i $j"
                                                }
                                                dataClass name: "Prior testing genes name $i $j", {
                                                    description "Prior testing genes description  $i $j"
                                                }
                                                dataClass name: "Closing statement name $i $j", {
                                                    description "Closing statement description  $i $j"
                                                }
                                                dataClass name: "Guidance name $i $j", {
                                                    description "Guidance description  $i $j"
                                                }
                                            }

                                            dataClass name: "Disorder >$i< Phenotypes Level5 Model $i Data Element $j", {
                                                description "Disorder >$i< heading Level5 description for Model $i Data Element $j"

                                                if (createPhenotypes) {
                                                    for (int k in 1..15) {
                                                        dataClass name: "Phenotype ($k) name $i $j", {
                                                            ext "OBO ID", "HP:" + (i + j + k)
                                                        }
                                                    }
                                                }
                                            }

                                            dataClass name: "Disorder >$i< Clinical tests Level5 Model $i Data Element $j", {
                                                description "Disorder >$i< heading Level5 description for Model $i Data Element $j"

                                                for (int k in 1..5) {
                                                    dataClass name: "Clinical tests ($k) name $i $j", {
                                                    }
                                                }
                                            }

                                            dataClass name: "Disorder >$i< Guidance name $i $j", {
                                                description "Guidance description  $i $j"
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                    ext Metadata.OWNER, 'The Owner'
                    ext Metadata.ORGANISATION, 'The Organisation'
                    ext Metadata.AUTHORS, 'Author One, Author Two, Author Three'
                    ext Metadata.REVIEWERS, 'Reviewer One, Reviewer Two, Reviewer Three'

                }
            }
        }

        return DataModel.findByName(RARE_DISEASE_DATA_MODEL_NAME)

    }


    //sleeps are only necessary to ensure changes to same object happen in different millisecs for order of changelogs to be consistent in test
    DataClass makeChanges(DataClass finalized) {
        println "status= $finalized.status"

        DataClass model = DataClass.findByNameAndStatus('Dataclass Top Level 1 Root', ElementStatus.DRAFT)

        DataDefinitionLanguage.with(RARE_DISEASE_DATA_MODEL_NAME) {

            update 'hierarchy' of 'Disorder >1< Phenotypes Level5 Model 1 Data Element 1' remove 'Phenotype (2) name 1 1'

            update 'name' of 'Phenotype (5) name 1 1' to 'Phenotype (5) changed name'   //PROPERTY_CHANGED

            update 'OBO ID' of 'Phenotype (6) name 1 1' to 'modified OBO ID'            //METADATA_UPDATED

            remove 'OBO ID' of 'Phenotype (7) name 1 1'            //METADATA_UPDATED  - cehck this again - nothing found for any of the change types

            create DataElement called 'New Phenotype DataElement'                       //NEW_ELEMENT_CREATED
            update 'containment' of 'Phenotype (8) name 1 1' add 'New Phenotype DataElement'    //RELATIONSHIP_CREATED - check, none found

            create DataElement called '2nd New Phenotype DataElement'                       //NEW_ELEMENT_CREATED
            update 'containment' of 'Phenotype (9) name 1 1' add '2nd New Phenotype DataElement', 'Min Occurs': 0, 'Max Occurs': 2    //RELATIONSHIP_CREATED
                                                                                                        //RELATIONSHIP_METADATA_CREATED * 2
            sleep(1)
            update 'Min Occurs' of 'Phenotype (9) name 1 1' to '1'              //METADATA_CREATED
            sleep(1)
            update 'Max Occurs' of 'Phenotype (9) name 1 1' to '3'
            sleep(1)

            update 'Min Occurs' of 'Phenotype (9) name 1 1' to '4'              //RELATIONSHIP_METADATA_UPDATED
            sleep(1)
            update 'Max Occurs' of 'Phenotype (9) name 1 1' to '5'

            update 'description' of 'Disorder >1< Guidance name 1 1' to 'new textual description replaces old'

            update 'description' of 'Clinical tests (5) name 1 2' to 'description for Clinical tests (5) name 1 2  has been changed'

            create DataClass called 'New Guidance class'
            update 'description' of 'New Guidance class' to 'brand new description'
            update 'hierarchy' of 'Disorder >>1<< heading Level4 Model Data Element 2' add 'New Guidance class'

            update 'description' of 'Phenotype (2) name 2 2' to 'description for Phenotype (2) name 2 2 has been changed also'

            create DataClass called 'Nested 1st level Phenotype'
            create DataClass called 'Nested 2nd level Phenotype'
            create DataClass called 'Nested 3rd level Phenotype'

            update 'hierarchy' of 'Phenotype (5) name 2 2' add 'Nested 1st level Phenotype'
            update 'hierarchy' of 'Nested 1st level Phenotype' add 'Nested 2nd level Phenotype'
            update 'hierarchy' of 'Nested 2nd level Phenotype' add 'Nested 3rd level Phenotype'

        }

        return model

    }

    //this is painful...why did I make the test data so big?
    def findDataIds() {
        level1_id = DataClass.findByName('Dataclass Top Level 1 Root').id
        level2_id_1 = DataClass.findByNameIlike("Disorder%1%Level2").id
        level2_id_2 = DataClass.findByNameIlike("Disorder%2%Level2").id

        level3_id_1 = DataClass.findByNameIlike("Disorder%1%Level3%1").id
        level3_id_2 = DataClass.findByNameIlike("Disorder%1%Level3%2").id
        level3_id_3 = DataClass.findByNameIlike("Disorder%2%Level3%1").id
        level3_id_4 = DataClass.findByNameIlike("Disorder%2%Level3%2").id

        level4_id_1 = DataClass.findByNameIlike("Disorder%1%Level4%1").id
        level4_id_2 = DataClass.findByNameIlike("Disorder%1%Level4%2").id
        level4_id_3 = DataClass.findByNameIlike("Disorder%2%Level4%1").id
        level4_id_4 = DataClass.findByNameIlike("Disorder%2%Level4%2").id

        level5_id_1 = DataClass.findByNameIlike("Disorder%1%Level5%1").id
        level5_id_2 = DataClass.findByNameIlike("Disorder%1%Level5%2").id
        level5_id_3 = DataClass.findByNameIlike("Disorder%2%Level5%1").id
        level5_id_4 = DataClass.findByNameIlike("Disorder%2%Level5%2").id

        level5_guidanceid_1 = DataClass.findByName("Disorder >1< Guidance name 1 1").id
        level5_guidanceid_2 = DataClass.findByName("Disorder >1< Guidance name 1 2").id
        level5_guidanceid_3 = DataClass.findByName("Disorder >2< Guidance name 2 1").id
        level5_guidanceid_4 = DataClass.findByName("Disorder >2< Guidance name 2 2").id

        level6_inclusion_1 = DataClass.findByNameIlike("Inclusion%1 1").id
        level6_inclusion_2 = DataClass.findByNameIlike("Inclusion%1 2").id
        level6_inclusion_3 = DataClass.findByNameIlike("Inclusion%2 1").id
        level6_inclusion_4 = DataClass.findByNameIlike("Inclusion%2 2").id

        level6_exclusion_1 = DataClass.findByNameIlike("Exclusion%1 1").id
        level6_exclusion_2 = DataClass.findByNameIlike("Exclusion%1 2").id
        level6_exclusion_3 = DataClass.findByNameIlike("Exclusion%2 1").id
        level6_exclusion_4 = DataClass.findByNameIlike("Exclusion%2 2").id

        level6_priorGenetic_1 = DataClass.findByNameIlike("Prior%Genetic%1 1").id
        level6_priorGenetic_2 = DataClass.findByNameIlike("Prior%Genetic%1 2").id
        level6_priorGenetic_3 = DataClass.findByNameIlike("Prior%Genetic%2 1").id
        level6_priorGenetic_4 = DataClass.findByNameIlike("Prior%Genetic%2 2").id

        level6_prior_genes_1 = DataClass.findByNameIlike("Prior%genes%1 1").id
        level6_prior_genes_2 = DataClass.findByNameIlike("Prior%genes%1 2").id
        level6_prior_genes_3 = DataClass.findByNameIlike("Prior%genes%2 1").id
        level6_prior_genes_4 = DataClass.findByNameIlike("Prior%genes%2 2").id

        level6_closing_1 = DataClass.findByNameIlike("Closing%1 1").id
        level6_closing_2 = DataClass.findByNameIlike("Closing%1 2").id
        level6_closing_3 = DataClass.findByNameIlike("Closing%2 1").id
        level6_closing_4 = DataClass.findByNameIlike("Closing%2 2").id

        level6_guidance_1 = DataClass.findByNameIlike("Guidance%1 1").id
        level6_guidance_2 = DataClass.findByNameIlike("Guidance%1 2").id
        level6_guidance_3 = DataClass.findByNameIlike("Guidance%2 1").id
        level6_guidance_4 = DataClass.findByNameIlike("Guidance%2 2").id


        for (int i in 1..2) {
            for (int j in 1..2) {
                    for (int k in 1..15) {
                        def version = DataClass.findByName("Phenotype ($k) name $i $j").id
                        phenotypeIds[i-1][j-1][k-1] = version
                    }
            }
        }
        for (int i in 1..2) {
            for (int j in 1..2) {
                    for (int k in 1..5) {
                        def version = DataClass.findByName("Clinical tests ($k) name $i $j").id
                        clinicalTestIds[i-1][j-1][k-1] = version
                    }
            }
        }

        phenotypeLevel5Id_1 = DataClass.findByNameIlike("%Phenotypes Level5 Model 1 Data Element 1").id
        phenotypeLevel5Id_2 = DataClass.findByNameIlike("%Phenotypes Level5 Model 1 Data Element 2").id
        phenotypeLevel5Id_3 = DataClass.findByNameIlike("%Phenotypes Level5 Model 2 Data Element 1").id
        phenotypeLevel5Id_4 = DataClass.findByNameIlike("%Phenotypes Level5 Model 2 Data Element 2").id

        eligibLevel5Id_1 = DataClass.findByNameIlike("%Eligibility Level5 Model 1 Data Element 1").id
        eligibLevel5Id_2 = DataClass.findByNameIlike("%Eligibility Level5 Model 1 Data Element 2").id
        eligibLevel5Id_3 = DataClass.findByNameIlike("%Eligibility Level5 Model 2 Data Element 1").id
        eligibLevel5Id_4 = DataClass.findByNameIlike("%Eligibility Level5 Model 2 Data Element 2").id

        clinicalLevel5Id_1 = DataClass.findByNameIlike("%Clinical tests Level5 Model 1 Data Element 1").id
        clinicalLevel5Id_2 = DataClass.findByNameIlike("%Clinical tests Level5 Model 1 Data Element 2").id
        clinicalLevel5Id_3 = DataClass.findByNameIlike("%Clinical tests Level5 Model 2 Data Element 1").id
        clinicalLevel5Id_4 = DataClass.findByNameIlike("%Clinical tests Level5 Model 2 Data Element 2").id
    }

    String getLevel2Id(def pos){
        return DataClass.findByNameIlike("Disorder%$pos%Level2").id
    }

    String getLevel3Id(def level2pos, def level3pos){
        return DataClass.findByNameIlike("Disorder%$level2pos%Level3%$level3pos").id
    }

    String getLevel4Id(def level2pos, def level3pos){
        return DataClass.findByNameIlike("Disorder%$level2pos%Level4%$level3pos").id
    }

    String getLevel2Name(def pos){
        return "Disorder >>>$pos<<< Level2"
    }

    String getLevel3Name(def level2pos, def level3pos){
        return "Disorder >>>$level2pos<<< SubCondition Level3 Model Data Element $level3pos"
    }

    String getLevel4Name(def level2pos, def level3pos){
        return "Disorder >>$level2pos<< heading Level4 Model Data Element $level3pos"
    }

    String getClinicalTestId(def pos, def level2pos, def level3pos){
        return RareDiseaseCsvExporter.getVersionId(DataClass.findByNameIlike("Clinical tests ($pos) name $level2pos $level3pos"))
    }

}
