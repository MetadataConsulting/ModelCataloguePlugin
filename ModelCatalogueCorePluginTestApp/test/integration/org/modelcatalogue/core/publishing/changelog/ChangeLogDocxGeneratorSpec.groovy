package org.modelcatalogue.core.publishing.changelog

import static org.modelcatalogue.core.util.test.FileOpener.open
import org.modelcatalogue.core.PerformanceUtilService
import org.modelcatalogue.core.api.ElementStatus
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.ddl.DataDefinitionLanguage
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/*
*
* THE CHANGE LOG STUFF DOESN'T REALLY WORK SO NOT SURE IT's worth fixing this test
*
* */
class ChangeLogDocxGeneratorSpec extends AbstractIntegrationSpec {

    AuditService auditService
    DataClassService dataClassService
    DataModelService dataModelService
    ElementService elementService
    PerformanceUtilService performanceUtilService

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    def "test changelog export"() {
        DataClass draft = buildTestDataClass()

        when:
        File file = tmp.newFile('changelog1.docx')

        ChangeLogDocxGenerator generator = new ChangeLogDocxGenerator(auditService, dataClassService, performanceUtilService, elementService)

        generator.generateChangelog(draft, file.newOutputStream())

        open(file)

        then:
        noExceptionThrown()
    }

    private DataClass buildTestDataClass() {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        final String typesDataModelName = "Data Model ${System.currentTimeMillis()}"

        builder.build {
            skip draft
            dataModel(name: typesDataModelName, status: finalized) {
                for (int i in 1..3) {
                    dataType name: "Test Data Type ${i}"
                }
            }

            dataModel(name: 'C4C', status: finalized) {
                description "This is a data model for testing ChangeLogDocxGenerator"

                ext 'foo', 'bar'
                ext 'one', '1'

                dataClass name: 'Root Data Class', {
                    for (int i in 1..3) {
                        dataClass name: "Data Class $i", {
                            description "This is a description for Data Class $i"
                            ext 'foo', 'bar'
                            ext 'boo', 'cow'

                            for (int j in 1..3) {
                                dataElement name: "Data Class $i Data Element $j", {
                                    description "This is a description for Data Class $i Data Element $j"
                                    dataType name: "Test Data Type ${i}", dataModel: typesDataModelName

                                    relationship {
                                        ext 'Min Occurs': '0', 'Max Occurs': "$j"
                                    }
                                }
                            }
                            for (int j in 1..3) {
                                dataClass name: "Data Class $i Inner Data Class $j", {
                                    description "This is a description for Data Class $i Inner Data Class $j"

                                    for (int k in 1..3) {
                                        dataElement name: "Data Class $i Inner Data Class $j Data Element $k", {
                                            description "This is a description for Data Class $i Inner Data Class $j Data Element $k"
                                            dataType name: "Test Data Type ${i}", dataModel: typesDataModelName
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return makeChanges(DataClass.findByName('Root Data Class'))

    }

    private DataClass makeChanges(DataClass finalized) {
        elementService.createDraftVersion(finalized.dataModel, "1.0.2", DraftContext.userFriendly())

        DataClass model = DataClass.findByNameAndStatus('Root Data Class', ElementStatus.DRAFT)

        // update description of C4C to
        DataDefinitionLanguage.with('C4C') {
            update 'description' of 'C4C' to 'This is a classification for testing ClassificationToDocxExporter. And now it has been changed.'
            update 'foo' of 'C4C' to 'baz'
            update 'boo' of 'C4C' to 'dar'
            update 'one' of 'C4C' to null
            remove 'foo' of 'C4C'

            create DataClass called 'Data Class XYZ', description: 'This is Data Class XYZ'
            update 'containment' of 'Data Class XYZ' add 'Data Class 1 Data Element 2', 'Min Occurs': 0, 'Max Occurs': 2
            update 'containment' of 'Data Class XYZ' add 'Data Class 1 Data Element 3', 'Min Occurs': 0, 'Max Occurs': 3
            update 'containment' of 'Data Class XYZ' add 'Data Class 2 Inner Data Class 3 Data Element 1', Name: 'M2CH3DE1'

            create draft of 'Data Class 1 Inner Data Class 2'
            update 'hierarchy' of 'Data Class 1 Inner Data Class 2' add 'Data Class XYZ'
            update 'base' of  'Data Class 1 Inner Data Class 1' add 'Data Class 1 Inner Data Class 2' // 'Data Class 1 Inner Data Class 1' is base for 'Data Class 1 Inner Data Class 2'

            create draft of 'Data Class 1'
            update 'hierarchy' of 'Data Class 1' remove 'Data Class 1 Inner Data Class 1'
            update 'containment' of 'Data Class 1' remove 'Data Class 1 Data Element 2'

            update 'description' of 'Data Class 1 Inner Data Class 2 Data Element 1' to 'This is a description for Data Class 1 Inner Data Class 2 Data Element 1 And now it has been changed.'

            create DataType called 'New Data Type'

            create draft of 'Data Class 1 Inner Data Class 2 Data Element 1'
            update 'dataType' of 'Data Class 1 Inner Data Class 2 Data Element 1' to 'New Data Type'

            finalize 'C4C'

        }

        return model

    }

}
