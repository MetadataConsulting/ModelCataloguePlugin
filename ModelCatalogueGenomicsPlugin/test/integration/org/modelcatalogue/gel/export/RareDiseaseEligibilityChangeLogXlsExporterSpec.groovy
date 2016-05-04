package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass

import static junit.framework.Assert.assertEquals
import static org.modelcatalogue.core.util.test.FileOpener.open

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

        //test generated content
        RareDiseasePhenotypeChangeLogXlsExporter exporter = new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 5, false)
        exporter.setIsEligibilityReport(true)
        List<String> rows = exporter.buildContentRows(dataClass)

        //need to prime with header as we're not running the excel part of the exporter here
        String allRows = 'Change reference,Level 2 Disease Group (ID),Level 3 Disease Subtype (ID),Level 4 Specific Disorder (ID),Affected Data Item,Change Type,Current version details,New version details\n'

        rows.each{ row ->
            allRows+= "${row.join(',')}\n"
        }
        println allRows


        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        //test excel generation
        new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 5, false).exportEligibilityCriteria(dataClass, file.newOutputStream())

        open file

        then:
        noExceptionThrown()
        assertEquals(expectedRDPhenotypesCsv, allRows) //need to use junit assert as Spock blows up badly on failure (OutOfMem)
    }

    private String getExpectedRDPhenotypesCsv() {
        return """Change reference,Level 2 Disease Group (ID),Level 3 Disease Subtype (ID),Level 4 Specific Disorder (ID),Affected Data Item,Change Type,Current version details,New version details
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Disorder >1< Eligibility Level5 Model 1 Data Element 1,New Data Item,,Disorder >1< Eligibility Level5 Model 1 Data Element 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Inclusion criteria name 1 1,New Data Item,,Inclusion criteria name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Exclusion criteria name 1 1,New Data Item,,Exclusion criteria name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Prior Genetic testing name 1 1,New Data Item,,Prior Genetic testing name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Prior testing genes name 1 1,New Data Item,,Prior testing genes name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Closing statement name 1 1,New Data Item,,Closing statement name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 ($level3_id_1),Disorder >>1<< heading Level4 Model Data Element 1 ($level4_id_1),Guidance name 1 1,New Data Item,,Guidance name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Disorder >1< Eligibility Level5 Model 1 Data Element 2,New Data Item,,Disorder >1< Eligibility Level5 Model 1 Data Element 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Inclusion criteria name 1 2,New Data Item,,Inclusion criteria name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Exclusion criteria name 1 2,New Data Item,,Exclusion criteria name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Prior Genetic testing name 1 2,New Data Item,,Prior Genetic testing name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Prior testing genes name 1 2,New Data Item,,Prior testing genes name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Closing statement name 1 2,New Data Item,,Closing statement name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 ($level2_id_1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 ($level3_id_2),Disorder >>1<< heading Level4 Model Data Element 2 ($level4_id_2),Guidance name 1 2,New Data Item,,Guidance name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Disorder >2< Eligibility Level5 Model 2 Data Element 1,New Data Item,,Disorder >2< Eligibility Level5 Model 2 Data Element 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Inclusion criteria name 2 1,New Data Item,,Inclusion criteria name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Exclusion criteria name 2 1,New Data Item,,Exclusion criteria name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Prior Genetic testing name 2 1,New Data Item,,Prior Genetic testing name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Prior testing genes name 2 1,New Data Item,,Prior testing genes name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Closing statement name 2 1,New Data Item,,Closing statement name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 ($level3_id_3),Disorder >>2<< heading Level4 Model Data Element 1 ($level4_id_3),Guidance name 2 1,New Data Item,,Guidance name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Disorder >2< Eligibility Level5 Model 2 Data Element 2,New Data Item,,Disorder >2< Eligibility Level5 Model 2 Data Element 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Inclusion criteria name 2 2,New Data Item,,Inclusion criteria name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Exclusion criteria name 2 2,New Data Item,,Exclusion criteria name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Prior Genetic testing name 2 2,New Data Item,,Prior Genetic testing name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Prior testing genes name 2 2,New Data Item,,Prior testing genes name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Closing statement name 2 2,New Data Item,,Closing statement name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 ($level2_id_2),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 ($level3_id_4),Disorder >>2<< heading Level4 Model Data Element 2 ($level4_id_4),Guidance name 2 2,New Data Item,,Guidance name 2 2 (Test Data Model) (0.0.1)
"""
    }

}
