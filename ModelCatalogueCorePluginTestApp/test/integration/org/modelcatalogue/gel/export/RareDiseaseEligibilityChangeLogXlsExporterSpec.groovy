package org.modelcatalogue.gel.export

import static junit.framework.Assert.assertEquals
import static org.modelcatalogue.core.util.test.FileOpener.open
import org.modelcatalogue.core.DataClass

/**
 * Test for RD eligibility criteria change log reports class using test model with similar format to expected data.
 */
class RareDiseaseEligibilityChangeLogXlsExporterSpec extends AbstractRareDiseasesExporterSpec {


    def "Export rare disease eligibility change logs to xls"() {
        when:
        buildTestModel(true)
        findDataIds()
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')
        makeChanges(dataClass)

        new_guidance_id = DataClass.findByName("New Guidance class").id
        //test generated content
        RareDiseaseEligibilityChangeLogXlsExporter exporter = new RareDiseaseEligibilityChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 5, false)
        List<String> rows = exporter.buildContentRows(dataClass)

        //need to prime with header as we're not running the excel part of the exporter here
        String allRows = 'Change reference,Level 2 Disease Group (ID),Level 3 Disease Subtype (ID),Level 4 Specific Disorder (ID),Affected Data Item,Change Type,Current version details,New version details\n'

        rows.each{ row ->
            allRows+= "${row.join(',')}\n"
        }

        println "EXPECTED<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
        println expectedRDEligibilityCsv
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        println "GOT<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
        println allRows
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"


        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        //test excel generation
        new RareDiseaseEligibilityChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 5, false).export(dataClass, file.newOutputStream())

        open file

        then:
        noExceptionThrown()
        assertEquals(expectedRDEligibilityCsv, allRows) //need to use junit assert as Spock blows up badly on failure (OutOfMem)
    }

    private String getExpectedRDEligibilityCsv() {
        return """Change reference,Level 2 Disease Group (ID),Level 3 Disease Subtype (ID),Level 4 Specific Disorder (ID),Affected Data Item,Change Type,Current version details,New version details
,,,,Dataclass Top Level 1 Root ($level1_id),New Data Item,,Disorder >>>1<<< Level2 ($level2_id_1)
,,,,Dataclass Top Level 1 Root ($level1_id),New Data Item,,Disorder >>>2<<< Level2 ($level2_id_2)
,Disorder >>>1<<< Level2 ($level2_id_1),,,Disorder >>>1<<< Level2 ($level2_id_1),New Data Item,,Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1)
,Disorder >>>1<<< Level2 ($level2_id_1),,,Disorder >>>1<<< Level2 ($level2_id_1),New Data Item,,Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),,Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),New Data Item,,Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Disorder >1< Phenotypes Level5 Model 1 Data Element 1 ($phenotypeLevel5Id_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Disorder >1< Clinical tests Level5 Model 1 Data Element 1 ($clinicalLevel5Id_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Disorder >1< Guidance name 1 1 ($level5_guidanceid_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1),New Data Item,,Inclusion criteria name 1 1 ($level6_inclusion_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1),New Data Item,,Exclusion criteria name 1 1 ($level6_exclusion_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1),New Data Item,,Prior Genetic testing name 1 1 ($level6_priorGenetic_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1),New Data Item,,Prior testing genes name 1 1 ($level6_prior_genes_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1),New Data Item,,Closing statement name 1 1 ($level6_closing_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1 ($level5_id_1),New Data Item,,Guidance name 1 1 ($level6_guidance_1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),,Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),New Data Item,,Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Disorder >1< Phenotypes Level5 Model 1 Data Element 2 ($phenotypeLevel5Id_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Disorder >1< Clinical tests Level5 Model 1 Data Element 2 ($clinicalLevel5Id_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Disorder >1< Guidance name 1 2 ($level5_guidanceid_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,New Guidance class ($new_guidance_id)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2),New Data Item,,Inclusion criteria name 1 2 ($level6_inclusion_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2),New Data Item,,Exclusion criteria name 1 2 ($level6_exclusion_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2),New Data Item,,Prior Genetic testing name 1 2 ($level6_priorGenetic_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2),New Data Item,,Prior testing genes name 1 2 ($level6_prior_genes_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2),New Data Item,,Closing statement name 1 2 ($level6_closing_2)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2 ($level5_id_2),New Data Item,,Guidance name 1 2 ($level6_guidance_2)
,Disorder >>>2<<< Level2 ($level2_id_2),,,Disorder >>>2<<< Level2 ($level2_id_2),New Data Item,,Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3)
,Disorder >>>2<<< Level2 ($level2_id_2),,,Disorder >>>2<<< Level2 ($level2_id_2),New Data Item,,Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),,Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),New Data Item,,Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Disorder >2< Phenotypes Level5 Model 2 Data Element 1 ($phenotypeLevel5Id_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Disorder >2< Clinical tests Level5 Model 2 Data Element 1 ($clinicalLevel5Id_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Disorder >2< Guidance name 2 1 ($level5_guidanceid_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3),New Data Item,,Inclusion criteria name 2 1 ($level6_inclusion_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3),New Data Item,,Exclusion criteria name 2 1 ($level6_exclusion_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3),New Data Item,,Prior Genetic testing name 2 1 ($level6_priorGenetic_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3),New Data Item,,Prior testing genes name 2 1 ($level6_prior_genes_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3),New Data Item,,Closing statement name 2 1 ($level6_closing_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1 ($level5_id_3),New Data Item,,Guidance name 2 1 ($level6_guidance_3)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),,Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),New Data Item,,Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Disorder >2< Phenotypes Level5 Model 2 Data Element 2 ($phenotypeLevel5Id_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Disorder >2< Clinical tests Level5 Model 2 Data Element 2 ($clinicalLevel5Id_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Disorder >2< Guidance name 2 2 ($level5_guidanceid_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4),New Data Item,,Inclusion criteria name 2 2 ($level6_inclusion_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4),New Data Item,,Exclusion criteria name 2 2 ($level6_exclusion_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4),New Data Item,,Prior Genetic testing name 2 2 ($level6_priorGenetic_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4),New Data Item,,Prior testing genes name 2 2 ($level6_prior_genes_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4),New Data Item,,Closing statement name 2 2 ($level6_closing_4)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2 ($level5_id_4),New Data Item,,Guidance name 2 2 ($level6_guidance_4)
"""
    }

}
