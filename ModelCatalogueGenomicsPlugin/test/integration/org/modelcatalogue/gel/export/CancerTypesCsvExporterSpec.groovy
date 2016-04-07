package org.modelcatalogue.gel.export

import groovy.json.JsonOutput
import org.modelcatalogue.core.DataClass
import spock.lang.Specification

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


    private String getExpectedCancerTypesCsv() {
        return """id,Cancer Types,id,Cancer SubTypes
$cancer_type_1_adult_glioma_combined_id,"Adult Glioma",$cancer_type_1_adult_subType_combined_id,"Adult Glioma subtypes 1.1"
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2",$cancer_type_2_some_cancer_subType21_combined_id,"some other cancer subtypes 2.1"
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2",$cancer_type_2_some_cancer_subType22_combined_id,"some other cancer subtypes 2.2\""""
    }



    def "export Presentation types to csv"() {
        OutputStream out = new ByteArrayOutputStream()
        when:
        DataClass model = DataClass.findByName(ROOT_CANCER_TYPE)
        new CancerTypesCsvExporter(out).exportPresentationTypesAsCsv(model)

        def response = new String(out.toByteArray())
        printOutput(response)

        then:
        noExceptionThrown()
        response == expectedPresentationTypesCsv
    }


    private String getExpectedPresentationTypesCsv() {
        return """id,Cancer Types,id,Cancer Presentations
$cancer_type_1_adult_glioma_combined_id,"Adult Glioma",$cancer_type_1_adult_presentation_combined_id,"Adult Glioma presentations 1"
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2",$cancer_type_2_some_cancer_presentation2_combined_id,"Cancer presentations 2"
$cancer_type_2_some_cancer_combined_id,"Some other Cancer Type 2",$cancer_type_2_some_cancer_presentation22_combined_id,"Cancer presentations 22\""""
    }


}
