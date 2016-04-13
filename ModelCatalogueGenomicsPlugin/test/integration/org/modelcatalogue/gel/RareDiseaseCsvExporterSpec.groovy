package org.modelcatalogue.gel

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/**
 * Test for RD reports class using test model with similar format to expected data.
 */
class RareDiseaseCsvExporterSpec extends IntegrationSpec {

    public static final String DATA_MODEL_NAME = 'testDataModel1'
    public static final String ROOT_DCLASS_NAME = 'rare disease group 1'
    public static final String LEVEL_2_DCLASS_NAME = 'rare disease \' subgroup 1.1'
    public static final String LEVEL_3_DCLASS_NAME = 'rare disease " disorder 1.1.1'
    public static final String LEVEL_4_DCLASS_NAME = 'rare disease disorder 1.1.1.1'
    public static final String ELIGIBILITY_DCLASS = 'rare disease disorder 1.1.1.1 eligibility'
    public static final String PHENOTYPES_DCLASS = 'rare disease disorder 1.1.1.1 phenotypes'
    public static final String TESTS_DCLASS = 'rare disease disorder 1.1.1.1 clinical tests'

    static final String HPO_TERM_1 = 'test hpo terms 1'
    static final String HPO_TERM_2 = 'test hpo terms 2'
    static final String HPO_TERM_3 = 'test hpo terms 3'
    static final String HPO_TERM_4 = 'test hpo terms 4'

    static final String TEST_1 = 'clinical test1'
    static final String TEST_2 = 'clinical test2'
    static final String TEST_3 = 'clinical test3'
    static final String TEST_4 = 'clinical test4'

    static final String ELIG_1 = 'Test Inclusion Criteria'
    static final String ELIG_2 = 'Test Exclusion Criteria'
    static final String ELIG_3 = 'Prior Genetic Testing'
    static final String ELIG_4 = 'Closing statement:'

    static final String ELIG_1_DESC = 'Test Description 1'
    static final String ELIG_2_DESC = ''
    static final String ELIG_3_DESC = '''Test Description
Multiline'''
    static final String ELIG_4_DESC = '''Test, with,
commas,
and multiline'''

    static GString hpoAndClinicalTestsCSV
    static GString eligibilityCSV

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: DATA_MODEL_NAME) {
                dataClass(name: ROOT_DCLASS_NAME) {
                    dataClass(name: LEVEL_2_DCLASS_NAME) {
                        dataClass(name: LEVEL_3_DCLASS_NAME) {
                            dataClass(name: LEVEL_4_DCLASS_NAME) {
                                dataClass(name: ELIGIBILITY_DCLASS, lastUpdated: new Date()) {
                                    dataClass(name: ELIG_1, description: ELIG_1_DESC)
                                    dataClass(name: ELIG_2, description: ELIG_2_DESC)
                                    dataClass(name: ELIG_3, description: ELIG_3_DESC)
                                    dataClass(name: ELIG_4, description: ELIG_4_DESC)
                                }
                                dataClass(name: PHENOTYPES_DCLASS, lastUpdated: new Date()) {
                                    dataClass(name: HPO_TERM_1)
                                    dataClass(name: HPO_TERM_2)
                                    dataClass(name: HPO_TERM_3)
                                    dataClass(name: HPO_TERM_4)
                                }
                                dataClass(name: TESTS_DCLASS, lastUpdated: new Date()) {
                                    dataClass(name: TEST_1)
                                    dataClass(name: TEST_2)
                                    dataClass(name: TEST_3)
                                    dataClass(name: TEST_4)
                                }
                            }
                        }
                    }
                }
            }
        }

        DataClass.findByName(HPO_TERM_1).addExtension(Metadata.OBO_ID,'1234567')
        DataClass.findByName(HPO_TERM_2).addExtension(Metadata.OBO_ID,'2345678')
        DataClass.findByName(HPO_TERM_3).addExtension(Metadata.OBO_ID,'3456789')
        DataClass.findByName(HPO_TERM_4).addExtension(Metadata.OBO_ID,'4567890')

        generateCSVs()

    }

    private static generateCSVs() {
        def today = new Date().format("yyyy-MM-dd")

        final String LEVEL_2_DCLASS_ID = DataClass.findByName(LEVEL_2_DCLASS_NAME).getCombinedVersion()
        final String LEVEL_3_DCLASS_ID = DataClass.findByName(LEVEL_3_DCLASS_NAME).getCombinedVersion()
        final String LEVEL_4_DCLASS_ID = DataClass.findByName(LEVEL_4_DCLASS_NAME).getCombinedVersion()

        final String HPO_1_OBO_ID = DataClass.findByName(HPO_TERM_1).ext.get(Metadata.OBO_ID)
        final String HPO_2_OBO_ID = DataClass.findByName(HPO_TERM_2).ext.get(Metadata.OBO_ID)
        final String HPO_3_OBO_ID = DataClass.findByName(HPO_TERM_3).ext.get(Metadata.OBO_ID)
        final String HPO_4_OBO_ID = DataClass.findByName(HPO_TERM_4).ext.get(Metadata.OBO_ID)

        final String TEST_1_ID = RareDiseaseCsvExporter.getVersionId(DataClass.findByName(TEST_1))
        final String TEST_2_ID = RareDiseaseCsvExporter.getVersionId(DataClass.findByName(TEST_2))
        final String TEST_3_ID = RareDiseaseCsvExporter.getVersionId(DataClass.findByName(TEST_3))
        final String TEST_4_ID = RareDiseaseCsvExporter.getVersionId(DataClass.findByName(TEST_4))

        final String ELIG_1_ID = DataClass.findByName(ELIG_1).getCombinedVersion()
        final String ELIG_2_ID = DataClass.findByName(ELIG_2).getCombinedVersion()
        final String ELIG_3_ID = DataClass.findByName(ELIG_3).getCombinedVersion()
        final String ELIG_4_ID = DataClass.findByName(ELIG_4).getCombinedVersion()

        hpoAndClinicalTestsCSV = """id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Phenotype,Phenotype ID,Test,Test ID
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$HPO_TERM_1,$HPO_1_OBO_ID,,,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$HPO_TERM_2,$HPO_2_OBO_ID,,,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$HPO_TERM_3,$HPO_3_OBO_ID,,,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$HPO_TERM_4,$HPO_4_OBO_ID,,,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,,,$TEST_1,$TEST_1_ID,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,,,$TEST_2,$TEST_2_ID,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,,,$TEST_3,$TEST_3_ID,
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,,,$TEST_4,$TEST_4_ID,"""

        eligibilityCSV = """id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Eligibility Criteria,Criteria ID,Description
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$ELIG_1,$ELIG_1_ID,"$ELIG_1_DESC",
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$ELIG_2,$ELIG_2_ID,"$ELIG_2_DESC",
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$ELIG_3,$ELIG_3_ID,"$ELIG_3_DESC",
$LEVEL_2_DCLASS_ID,$LEVEL_2_DCLASS_NAME,$LEVEL_3_DCLASS_ID,$LEVEL_3_DCLASS_NAME,$LEVEL_4_DCLASS_ID,$LEVEL_4_DCLASS_NAME,$today,$ELIG_4,$ELIG_4_ID,"$ELIG_4_DESC","""


    }

    def "export HPO and clinical test data from RD model to csv"() {
        OutputStream out = new ByteArrayOutputStream()

        when:
        DataClass model = DataClass.findByName(ROOT_DCLASS_NAME)
        new RareDiseaseCsvExporter(out, RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS).printReport(model)

        String csv = new String(out.toByteArray())

//        csv.eachLine {
//            s -> println(s + '\n')
//        }

        then:
        noExceptionThrown()
        csv ==~ hpoAndClinicalTestsCSV
    }

    def "export eligibility data from RD model to csv"() {
        OutputStream out = new ByteArrayOutputStream()

        when:
        DataClass model = DataClass.findByName(ROOT_DCLASS_NAME)
        new RareDiseaseCsvExporter(out, RareDiseaseCsvExporter.ELIGIBILITY).printReport(model)

        String csv = new String(out.toByteArray())

//        csv.eachLine {
//            s -> println(s + '\n')
//        }

        then:
        noExceptionThrown()
        csv ==~ eligibilityCSV
    }

}
