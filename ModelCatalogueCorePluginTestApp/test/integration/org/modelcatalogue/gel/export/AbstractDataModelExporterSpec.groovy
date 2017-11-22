package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import org.hibernate.SessionFactory
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.ddl.DataDefinitionLanguage
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.TestDataHelper
import org.modelcatalogue.gel.RareDiseaseCsvExporter

/**
 * Abstract test class for Data Specification models (i.e. Rare Disease Models, Cancer Models)
 */
class AbstractDataModelExporterSpec extends AbstractIntegrationSpec {

    public static final String DATA_MODEL_EXPORTER_DATA_MODEL_NAME = 'Data Exporter FirstTestSpec Data Model'
    AuditService auditService
    DataClassService dataClassService
    ElementService elementService
    DataModelService dataModelService
    PerformanceUtilService performanceUtilService

    def setup() {
        initRelationshipTypes()
    }

    @Rule
    TemporaryFolder temporaryFolder

    String level1_id, level2_id_1, level2_id_2
    String level3_id_1, level3_id_2, level3_id_3, level3_id_4
    String level4_id_1, level4_id_2, level4_id_3, level4_id_4
    String elem1_id, elem2_id, elem3_id, elem4_id, elem5_id, elem6_id
    String level5_id1,level5_id2,level5_id3,level5_id4
    String newParticipant_id, newParticipant_id2

    // Model Based on Strcture of RD and Cancer Models
    DataModel buildTestModel() {
        TestDataHelper.initFreshDb(sessionFactory, 'data-model-exporter.sql') {
            initRelationshipTypes()
            DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

            builder.build {
                skip draft
                dataModel(name: DATA_MODEL_EXPORTER_DATA_MODEL_NAME) { //e.g. Cancer Model
                    description "This is a data model for testing Data Specification change log exports"

                    dataClass(name: 'Dataclass Top Level 1 Root') { //e.g. Cancer
                        for (int i in 1..2) {
                            dataClass name: "Info >>>$i<<< Level2", {
                                description "This is a description for Model $i" //E.g. Essential Sample Metadata

                                for (int j in 1..2) {
                                    dataClass name: "Info >>>$i<<< subCategory Level3 Model Data Element $j", {//e.g. Patient, Contact Details, Consent...
                                        description "This is a description for Model $i Data Element $j"

                                        dataElement name: "Form Element 1" //e.g. Surname
                                        dataElement name: "Form Element 2" //e.g. Gender
                                        dataElement name: "Form Element 3" //e.g. GCM ID

                                        dataClass name: "Info >>$i<< heading Level4 Model Data Element $j", { //e.g. Participant Identifiers
                                            description "Disorder >$i< heading Level4 description for Model $i Data Element $j"

                                            dataElement name: "Form Element 4" //e.g. Participant ID
                                            dataElement name: "Form Element 5" //e.g. DOB
                                            dataElement name: "Form Element 6" //e.g. NHS Number

                                            dataClass name: ">>$i<< Level5 Model Data Element $j", { //e.g. ??
                                                description ">$i< Level5 description for Model $i Data item $j"
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

        return DataModel.findByName(DATA_MODEL_EXPORTER_DATA_MODEL_NAME)

    }


    void makeChanges() {

        DataDefinitionLanguage.with(DATA_MODEL_EXPORTER_DATA_MODEL_NAME) {

            update 'Min Occurs' of 'Info >>1<< heading Level4 Model Data Element 2' to '1'
            update 'Max Occurs' of 'Info >>2<< heading Level4 Model Data Element 1' to '3'

            update 'description' of 'Info >>>1<<< subCategory Level3 Model Data Element 2' to 'new textual description replaces old'

            create DataClass called 'New Participant Info class'
            update 'description' of 'New Participant Info class' to 'brand new description'
            update 'hierarchy' of 'Info >>1<< heading Level4 Model Data Element 2' add 'New Participant Info class'

            create DataClass called 'New Participant Info class2'
            update 'description' of 'New Participant Info class2' to 'new description2'
            update 'hierarchy' of '>>1<< Level5 Model Data Element 2' add 'New Participant Info class2'
        }

    }

    def findDataIds() {
        level1_id = DataClass.findByName("Dataclass Top Level 1 Root").id
        level2_id_1 = DataClass.findByNameIlike("Info%1%Level2").id
        level2_id_2 = DataClass.findByNameIlike("Info%2%Level2").id

        level3_id_1 = DataClass.findByNameIlike("Info%1%Level3%1").id
        level3_id_2 = DataClass.findByNameIlike("Info%1%Level3%2").id
        level3_id_3 = DataClass.findByNameIlike("Info%2%Level3%1").id
        level3_id_4 = DataClass.findByNameIlike("Info%2%Level3%2").id

        level4_id_1 = DataClass.findByNameIlike("Info%1%Level4%1").id
        level4_id_2 = DataClass.findByNameIlike("Info%1%Level4%2").id
        level4_id_3 = DataClass.findByNameIlike("Info%2%Level4%1").id
        level4_id_4 = DataClass.findByNameIlike("Info%2%Level4%2").id
        newParticipant_id = DataClass.findByName("New Participant Info class").id
        newParticipant_id2 = DataClass.findByName("New Participant Info class2").id

        elem1_id = DataElement.findByName("Form Element 1").id
        elem2_id = DataElement.findByName("Form Element 2").id
        elem3_id = DataElement.findByName("Form Element 3").id
        elem4_id = DataElement.findByName("Form Element 4").id
        elem5_id = DataElement.findByName("Form Element 5").id
        elem6_id = DataElement.findByName("Form Element 6").id

        level5_id1 = DataClass.findByNameIlike(">>1<< Level5 Model Data Element 1").id
        level5_id2 = DataClass.findByNameIlike(">>1<< Level5 Model Data Element 2").id
        level5_id3 = DataClass.findByNameIlike(">>2<< Level5 Model Data Element 1").id
        level5_id4 = DataClass.findByNameIlike(">>2<< Level5 Model Data Element 2").id

    }

    String getLevel2Id(def pos) {
        return DataClass.findByNameIlike("Info%$pos%Level2").id
    }

    String getLevel3Id(def level2pos, def level3pos) {
        return DataClass.findByNameIlike("Info%$level2pos%Level3%$level3pos").id
    }

    String getLevel4Id(def level2pos, def level3pos) {
        return DataClass.findByNameIlike("Info%$level2pos%Level4%$level3pos").id
    }

    String getLevel2Name(def pos) {
        return "Info >>>$pos<<< Level2"
    }

    String getLevel3Name(def level2pos, def level3pos) {
        return "Info >>>$level2pos<<< subCategory Level3 Model Data Element $level3pos"
    }

    String getLevel4Name(def level2pos, def level3pos) {
        return "Info >>$level2pos<< heading Level4 Model Data Element $level3pos"
    }

    String getClinicalTestId(def pos, def level2pos, def level3pos) {
        return RareDiseaseCsvExporter.getVersionId(DataClass.findByNameIlike("Clinical tests ($pos) name $level2pos $level3pos"))
    }

}
