package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import groovy.transform.TypeChecked
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/**
 * Created by rickrees on 24/03/2016.
 */
class RareDiseaseDisorderListCsvExporterSpec extends IntegrationSpec {

    public static final String LEVEL2_GROUP1 = 'disease group 1,with comma'
    public static final String LEVEL3_1_GROUP1 = 'disease subgroup 1.1'
    public static final String LEVEL3_2_GROUP1 = 'disease subgroup 1.2,with comma'
    public static final String LEVEL4_1_GROUP1 = 'disease disorder A'
    public static final String LEVEL4_2_GROUP1 = 'disease disorder B,with comma'
    public static final String LEVEL4_3_GROUP1 = 'disease disorder C'
    public static final String LEVEL2_GROUP2 = '"disease group 2"'
    public static final String LEVEL3_GROUP2 = '"disease subgroup 2.1"'
    public static final String LEVEL4_1_GROUP2 = '"disease disorder D"'
    public static final String LEVEL4_2_GROUP2 = 'disease disorder E'
    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    String level2_group1_id
    String level3_1_group1_id
    String level3_2_group1_id
    String level4_1_group1_id
    String level4_2_group1_id
    String level4_3_group1_id
    String level2_group2_id
    String level3_group2_id
    String level4_1_group2_id
    String level4_2_group2_id

    void setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: 'testDataModel1') {
                dataClass(name: 'rare diseases') {
                    dataClass(name: LEVEL2_GROUP1) {     //with comma in names
                        dataClass(name: LEVEL3_1_GROUP1) {
                            dataClass(name: LEVEL4_1_GROUP1) {}
                        }
                        dataClass(name: LEVEL3_2_GROUP1) {
                            dataClass(name: LEVEL4_2_GROUP1) {}
                            dataClass(name: LEVEL4_3_GROUP1) {}
                        }
                    }
                    dataClass(name: LEVEL2_GROUP2) {             //with double quotes around names
                        dataClass(name: LEVEL3_GROUP2) {
                            dataClass(name: LEVEL4_1_GROUP2) {}
                            dataClass(name: LEVEL4_2_GROUP2) {}
                        }
                    }
                }
            }
        }

        level2_group1_id = DataClass.findByName(LEVEL2_GROUP1).getCombinedVersion()
        level3_1_group1_id =  DataClass.findByName(LEVEL3_1_GROUP1).getCombinedVersion()
        level3_2_group1_id = DataClass.findByName(LEVEL3_2_GROUP1).getCombinedVersion()
        level4_1_group1_id = DataClass.findByName(LEVEL4_1_GROUP1).getCombinedVersion()
        level4_2_group1_id = DataClass.findByName(LEVEL4_2_GROUP1).getCombinedVersion()
        level4_3_group1_id = DataClass.findByName(LEVEL4_3_GROUP1).getCombinedVersion()
        level2_group2_id = DataClass.findByName(LEVEL2_GROUP2).getCombinedVersion()
        level3_group2_id = DataClass.findByName(LEVEL3_GROUP2).getCombinedVersion()
        level4_1_group2_id = DataClass.findByName(LEVEL4_1_GROUP2).getCombinedVersion()
        level4_2_group2_id = DataClass.findByName(LEVEL4_2_GROUP2).getCombinedVersion()
    }

    def "Export model to csv"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName('rare diseases')
        new RareDiseaseDisorderListCsvExporter(out).export(model)

        String csv = new String(out.toByteArray())

        println csv
        then:
        noExceptionThrown()
        csv == this.expectedCSV
    }



    private String getExpectedCSV() {
        return """id,Level 2 DiseaseGroup,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder
$level2_group1_id,"disease group 1,with comma",$level3_1_group1_id,"disease subgroup 1.1",$level4_1_group1_id,"disease disorder A"
$level2_group1_id,"disease group 1,with comma",$level3_2_group1_id,"disease subgroup 1.2,with comma",$level4_2_group1_id,"disease disorder B,with comma"
$level2_group1_id,"disease group 1,with comma",$level3_2_group1_id,"disease subgroup 1.2,with comma",$level4_3_group1_id,"disease disorder C"
$level2_group2_id,"disease group 2",$level3_group2_id,"disease subgroup 2.1",$level4_1_group2_id,"disease disorder D"
$level2_group2_id,"disease group 2",$level3_group2_id,"disease subgroup 2.1",$level4_2_group2_id,"disease disorder E\""""
    }
}
