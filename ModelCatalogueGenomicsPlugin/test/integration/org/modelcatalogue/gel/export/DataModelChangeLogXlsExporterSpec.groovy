package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.DataModelFilter

import static junit.framework.Assert.assertEquals
import static org.modelcatalogue.core.util.test.FileOpener.open

/**
 * Test for Data Spec change log reports class.
 */
class DataModelChangeLogXlsExporterSpec extends AbstractDataModelExporterSpec {

    def "Export data spec change logs to xls"() {
        when:
        DataModel dataModel = buildTestModel()
        findDataIds()
        makeChanges()

        //test generated content
        DataModelChangeLogXlsExporter exporter = new DataModelChangeLogXlsExporter(auditService, dataClassService, 5, false)
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
        new DataModelChangeLogXlsExporter(auditService, dataClassService, 5, false).export(dataModel, file.newOutputStream())

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
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1,New Data Item,,Info >>1<< heading Level4 Model Data Element 1 (Test Data Model) (0.0.1)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1,New Data Item,,Form Element 4 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1,New Data Item,,Form Element 5 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 1 ($level3_id_1),Info >>1<< heading Level4 Model Data Element 1,New Data Item,,Form Element 6 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2,New Data Item,,Form Element 4 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2,New Data Item,,Form Element 5 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2,New Data Item,,Form Element 6 (Test Data Model) ([, ])
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),Info >>1<< heading Level4 Model Data Element 2,New Data Item,,Info >>1<< heading Level4 Model Data Element 2 (Test Data Model) (0.0.1)
,Info >>>1<<< Level2 ($level2_id_1),Info >>>1<<< subCategory Level3 Model Data Element 2 ($level3_id_2),New Participant Info class,New Data Item,,New Participant Info class (Test Data Model) (0.0.1)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1,New Data Item,,Form Element 4 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1,New Data Item,,Form Element 5 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1,New Data Item,,Form Element 6 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 1 ($level3_id_3),Info >>2<< heading Level4 Model Data Element 1,New Data Item,,Info >>2<< heading Level4 Model Data Element 1 (Test Data Model) (0.0.1)
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 1,New Data Item,,contained in: Form Element 1 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 2,New Data Item,,contained in: Form Element 2 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Form Element 3,New Data Item,,contained in: Form Element 3 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2,New Data Item,,Form Element 4 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2,New Data Item,,Form Element 5 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2,New Data Item,,Form Element 6 (Test Data Model) ([, ])
,Info >>>2<<< Level2 ($level2_id_2),Info >>>2<<< subCategory Level3 Model Data Element 2 ($level3_id_4),Info >>2<< heading Level4 Model Data Element 2,New Data Item,,Info >>2<< heading Level4 Model Data Element 2 (Test Data Model) (0.0.1)
"""
    }

}
