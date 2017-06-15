package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass

/**
 * Created by rickrees on 07/04/2016.
 */
class CancerTypesCsvExporterSpec extends AbstractCancerTypesExporterSpec {


    def "export Cancer types to csv"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName(ROOT_CANCER_TYPE)
        new CancerTypesCsvExporter(out).exportCancerTypesAsCsv(model)

        def response = new String(out.toByteArray())
        println "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
        println response
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"

        then:
        noExceptionThrown()
        response == expectedCancerTypesCsv
    }


    //note SOME_CANCER_SUBTYPE_21 has a dataTypes that is not an enum so it's intentionally omitted
    private String getExpectedCancerTypesCsv() {
        return """Id,Cancer Types,Presentations,Id SubTypes,Cancer SubTypes,Id Enum,Enum SubType
$cancer_type_1_adult_glioma_combined_id,"Adult Glioma",,$cancer_type_1_adult_subType_combined_id,"Adult Glioma subtypes 1.1",$cancer_type_1_adult_glioma_enum_combined_id,"one"
$cancer_type_1_adult_glioma_combined_id,"Adult Glioma",,$cancer_type_1_adult_subType_combined_id,"Adult Glioma subtypes 1.1",$cancer_type_1_adult_glioma_enum_combined_id,"two"
$cancer_type_1_adult_glioma_combined_id,"Adult Glioma","Adult Glioma presentations 1",,,,
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2",,$cancer_type_2_some_cancer_subType22_combined_id,"some other cancer subtypes 2.2",$cancer_type_2_some_cancer_enum_combined_id,"first"
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2",,$cancer_type_2_some_cancer_subType22_combined_id,"some other cancer subtypes 2.2",$cancer_type_2_some_cancer_enum_combined_id,"second"
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2","Cancer presentations 21",,,,
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2","Cancer presentations 22",,,,"""
    }

}
