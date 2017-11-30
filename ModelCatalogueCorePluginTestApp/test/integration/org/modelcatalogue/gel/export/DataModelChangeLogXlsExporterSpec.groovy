package org.modelcatalogue.gel.export

import static junit.framework.Assert.assertEquals
import static org.modelcatalogue.core.util.test.FileOpener.open
import org.hibernate.SessionFactory
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.DataModelFilter
import spock.lang.Ignore

/**
 * Test for Data Spec change log reports class.
 */
@Ignore
class DataModelChangeLogXlsExporterSpec extends AbstractDataModelExporterSpec {

    def "Export data spec change logs to xls"() {
        when:
        DataModel dataModel = buildTestModel()
        makeChanges()
        findDataIds()

        //test generated content
        DataModelChangeLogXlsExporter exporter = new DataModelChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 5, false)
        List<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes((DataModel) dataModel)).items
        DataClass dataClass = dataClasses?.get(0)

        List<String> rows = exporter.buildContentRows(dataClass)

        //need to prime with header as we're not running the excel part of the exporter here
        String allRows = 'Change reference,Data Category,Section (model cat ref),Data Item Name,Change Type,Current version details\n'

        rows.each { row ->
            allRows += "${row.join(',')}\n"
        }

        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        //test excel generation
        new DataModelChangeLogXlsExporter(auditService, dataClassService, performanceUtilService, 5, false).export(dataModel, file.newOutputStream())

        open file

        println '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
        println "expectedRDPhenotypesCsv:\n$expectedRDPhenotypesCsv"
        println '>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<'
        println "allRows:\n$allRows"
        println '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'

        then:
        noExceptionThrown()
        assertEquals(expectedRDPhenotypesCsv, allRows) //need to use junit assert as Spock blows up badly on failure (OutOfMem)
    }

    private String getExpectedRDPhenotypesCsv() {
        return """Change reference,Data Category,Section (model cat ref),Data Item Name,Change Type,Current version details
,,,Dataclass Top Level 1 Root ($level1_id),New Data Item,,Info >>>1<<< Level2 ($level2_id_1)
,,,Dataclass Top Level 1 Root ($level1_id),New Data Item,,Info >>>2<<< Level2 ($level2_id_2)
,Info >>>1<<< Level2 ($level2_id_1),,Info >>>1<<< Level2 ($level2_id_1),New Data Item,,Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1)
,Info >>>1<<< Level2 ($level2_id_1),,Info >>>1<<< Level2 ($level2_id_1),New Data Item,,Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),New Data Item,,Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),New Data Item,,Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),New Data Item,,Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),New Data Item,,Info >>1<< heading Level4 Model Data Element 1 ($level4_id_1)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Form Element 4 ($elem4_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Form Element 5 ($elem5_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,Form Element 6 ($elem6_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1 ($level4_id_1),New Data Item,,>>1<< Level5 Model Data Element 1 ($level5_id1)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Data Item,,Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Data Item,,Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Data Item,,Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Data Item,,Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Change Description,This is a description for Model 1 Data Element 2,new textual description replaces old
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Form Element 4 ($elem4_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Form Element 5 ($elem5_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,Form Element 6 ($elem6_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,>>1<< Level5 Model Data Element 2 ($level5_id2)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Metadata,,Min Occurs: 1
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2 ($level4_id_2),New Data Item,,New Participant Info class ($newParticipant_id)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),>>1<< Level5 Model Data Element 2 ($level5_id2),New Data Item,,New Participant Info class2 ($newParticipant_id2)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Participant Info class2 ($newParticipant_id2),Change Description,,new description2
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Participant Info class ($newParticipant_id),Change Description,,brand new description
,Info >>>2<<< Level2 ($level2_id_2),,Info >>>2<<< Level2 ($level2_id_2),New Data Item,,Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3)
,Info >>>2<<< Level2 ($level2_id_2),,Info >>>2<<< Level2 ($level2_id_2),New Data Item,,Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),New Data Item,,Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),New Data Item,,Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),New Data Item,,Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),New Data Item,,Info >>2<< heading Level4 Model Data Element 1 ($level4_id_3)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Form Element 4 ($elem4_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Form Element 5 ($elem5_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,Form Element 6 ($elem6_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Data Item,,>>2<< Level5 Model Data Element 1 ($level5_id3)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1 ($level4_id_3),New Metadata,,Max Occurs: 3
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),New Data Item,,Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),New Data Item,,Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),New Data Item,,Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),New Data Item,,Info >>2<< heading Level4 Model Data Element 2 ($level4_id_4)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1 ($elem1_id),New Data Item,,contained in: Form Element 1 ($elem1_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2 ($elem2_id),New Data Item,,contained in: Form Element 2 ($elem2_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3 ($elem3_id),New Data Item,,contained in: Form Element 3 ($elem3_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Form Element 4 ($elem4_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Form Element 5 ($elem5_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,Form Element 6 ($elem6_id)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2 ($level4_id_4),New Data Item,,>>2<< Level5 Model Data Element 2 ($level5_id4)
"""
    }

}
