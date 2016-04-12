package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

/**
 * Created by rickrees on 07/04/2016.
 */
class AbstractCancerTypesExporterSpec extends IntegrationSpec{
    public static final String DATA_MODEL_NAME = 'testDataModel1'
    public static final String ROOT_CANCER_TYPE = 'cancer types'
    public static final String ADULT_GLIOMA_TYPE1 = 'Adult Glioma'
    public static final String ADULT_GLIOMA_SUBTYPE_1 = 'Adult Glioma subtypes 1.1'
    public static final String ADULT_GLIOMA_PRESENTATION_1 = 'Adult Glioma presentations 1'
    public static final String SOME_CANCER_TYPE2 = 'Some other Cancer Type 2'
    public static final String SOME_CANCER_SUBTYPE_21 = 'some other cancer subtypes 2.1'
    public static final String SOME_CANCER_SUBTYPE_22 = 'some other cancer subtypes 2.2'
    public static final String SOME_CANCER_PRESENTATION_2 = 'Cancer presentations 2'
    public static final String SOME_CANCER_PRESENTATION_22 = 'Cancer presentations 22'
    protected Long cancer_type_1_adult_glioma_id
    protected Long cancer_type_2_some_cancer_id
    protected String cancer_type_1_adult_glioma_combined_id
    protected String cancer_type_1_adult_subType_combined_id
    protected String cancer_type_1_adult_presentation_combined_id
    protected String cancer_type_2_some_cancer_combined_id
    protected String cancer_type_2_some_cancer_subType21_combined_id
    protected String cancer_type_2_some_cancer_subType22_combined_id
    protected String cancer_type_2_some_cancer_presentation2_combined_id
    protected String cancer_type_2_some_cancer_presentation22_combined_id
    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()

        DefaultCatalogueBuilder catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)

        catalogueBuilder.build {

            dataModel(name: DATA_MODEL_NAME) {
                dataClass(name: ROOT_CANCER_TYPE) {
                    dataClass(name: ADULT_GLIOMA_TYPE1) {
                        dataElement name: ADULT_GLIOMA_SUBTYPE_1, {
                            description "adult glioma description"
                            dataType name: "Data Element11", enumerations: [    //shouldn't display this level
                                'one': '1',
                                'two': '2',
                            ]

                        }

                        dataElement(name: ADULT_GLIOMA_PRESENTATION_1) {
                            description "adult glioma presentation description"
                            dataType name: "Data Element12"
                        }
                    }
                    dataClass(name: SOME_CANCER_TYPE2) {
                        dataElement(name: SOME_CANCER_SUBTYPE_21) {
                            description "some cancer description subtype21"
                            dataType name: "Data Element21"
                        }
                        dataElement(name: SOME_CANCER_SUBTYPE_22) {
                            description "some cancer description subtype22"
                            dataType name: "Data Element22"
                        }
                        dataElement(name: SOME_CANCER_PRESENTATION_2) {
                            description "some cancer presentation description2"
                            dataType name: "Data Element23"
                        }
                        dataElement(name: SOME_CANCER_PRESENTATION_22) {
                            description "some cancer presentation description22"
                            dataType name: "Data Element24"
                        }
                    }
                }
            }
        }


        def adultType = DataClass.findByName(ADULT_GLIOMA_TYPE1)
        cancer_type_1_adult_glioma_id = adultType.getId()
        cancer_type_1_adult_glioma_combined_id = adultType.getCombinedVersion()

        cancer_type_1_adult_subType_combined_id = DataElement.findByName(ADULT_GLIOMA_SUBTYPE_1).getCombinedVersion()
        cancer_type_1_adult_presentation_combined_id = DataElement.findByName(ADULT_GLIOMA_PRESENTATION_1).getCombinedVersion()

        def someCancerType = DataClass.findByName(SOME_CANCER_TYPE2)
        cancer_type_2_some_cancer_id = someCancerType.getId()
        cancer_type_2_some_cancer_combined_id = someCancerType.getCombinedVersion()

        cancer_type_2_some_cancer_subType21_combined_id = DataElement.findByName(SOME_CANCER_SUBTYPE_21).getCombinedVersion()
        cancer_type_2_some_cancer_subType22_combined_id = DataElement.findByName(SOME_CANCER_SUBTYPE_22).getCombinedVersion()
        cancer_type_2_some_cancer_presentation2_combined_id = DataElement.findByName(SOME_CANCER_PRESENTATION_2).getCombinedVersion()
        cancer_type_2_some_cancer_presentation22_combined_id = DataElement.findByName(SOME_CANCER_PRESENTATION_22).getCombinedVersion()
    }

    protected void printOutput(String response) {
        println "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
        println response
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    }
}
