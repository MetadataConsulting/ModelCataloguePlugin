package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.ddl.DataDefinitionLanguage
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.gel.RareDiseaseCsvExporter

/**
 * Abstract test class for Data Specification models (i.e. Rare Disease Models, Cancer Models)
 */
class AbstractDataModelExporterSpec extends IntegrationSpec {

    AuditService auditService
    DataClassService dataClassService
    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    @Rule
    TemporaryFolder temporaryFolder

    String level2_id_1, level2_id_2
    String level3_id_1, level3_id_2, level3_id_3, level3_id_4
    String level4_id_1, level4_id_2, level4_id_3, level4_id_4
    String elem1_id, elem2_id, elem3_id, elem4_id, elem5_id, elem6_id
    String newParticipant_id

    // Model Based on Strcture of RD and Cancer Models
    DataModel buildTestModel() {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        DataModel testModel = builder.build {
            dataModel(name: 'Test Data Model') { //e.g. Cancer Model
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

        return testModel

    }


    void makeChanges() {

        DataDefinitionLanguage.with('Test Data Model') {

            update 'Min Occurs' of 'Info >>1<< heading Level4 Model Data Element 2' to '1'
            update 'Max Occurs' of 'Info >>2<< heading Level4 Model Data Element 1' to '3'

            update 'description' of 'Info >>>1<<< subCategory Level3 Model Data Element 2' to 'new textual description replaces old'

            create DataClass called 'New Participant Info class'
            update 'description' of 'New Participant Info class' to 'brand new description'
            update 'hierarchy' of 'Info >>>1<<< subCategory Level3 Model Data Element 2' add 'New Participant Info class'

            finalize 'Test Data Model'

        }

    }

    def findDataIds() {
        level2_id_1 = DataClass.findByNameIlike("Info%1%Level2").combinedVersion
        level2_id_2 = DataClass.findByNameIlike("Info%2%Level2").combinedVersion

        level3_id_1 = DataClass.findByNameIlike("Info%1%Level3%1").combinedVersion
        level3_id_2 = DataClass.findByNameIlike("Info%1%Level3%2").combinedVersion
        level3_id_3 = DataClass.findByNameIlike("Info%2%Level3%1").combinedVersion
        level3_id_4 = DataClass.findByNameIlike("Info%2%Level3%2").combinedVersion

        level4_id_1 = DataClass.findByNameIlike("Info%1%Level4%1").combinedVersion
        level4_id_2 = DataClass.findByNameIlike("Info%1%Level4%2").combinedVersion
        level4_id_3 = DataClass.findByNameIlike("Info%2%Level4%1").combinedVersion
        level4_id_4 = DataClass.findByNameIlike("Info%2%Level4%2").combinedVersion
        newParticipant_id = DataClass.findByName("New Participant Info class").combinedVersion

        elem1_id = DataElement.findByName("Form Element 1").combinedVersion
        elem2_id = DataElement.findByName("Form Element 2").combinedVersion
        elem3_id = DataElement.findByName("Form Element 3").combinedVersion
        elem4_id = DataElement.findByName("Form Element 4").combinedVersion
        elem5_id = DataElement.findByName("Form Element 5").combinedVersion
        elem6_id = DataElement.findByName("Form Element 6").combinedVersion

    }

    String getLevel2Id(def pos) {
        return DataClass.findByNameIlike("Info%$pos%Level2").combinedVersion
    }

    String getLevel3Id(def level2pos, def level3pos) {
        return DataClass.findByNameIlike("Info%$level2pos%Level3%$level3pos").combinedVersion
    }

    String getLevel4Id(def level2pos, def level3pos) {
        return DataClass.findByNameIlike("Info%$level2pos%Level4%$level3pos").combinedVersion
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
