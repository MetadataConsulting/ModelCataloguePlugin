package org.modelcatalogue.gel.export

import static org.modelcatalogue.core.util.test.FileOpener.open
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.TestDataHelper
import org.modelcatalogue.gel.GenomicsService

/**
 * Created by amilward on 11/07/2017.
 */
class RareDiseaseMaintenanceSplitDocsExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService

    static final String DOC_IMAGE_PATH = GenomicsService.getResource('Genomics-England-logo-2015.png')?.toExternalForm()

    DataClass dataClass

    def setup() {
        dataClass = buildRDModel(dataModelService, elementService)

    }

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "export model to docx"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")

        new RareDiseaseMaintenanceSplitDocsExporter(DataClass.get(dataClass.id), RareDiseaseMaintenanceSplitDocsExporter.standardTemplate, DOC_IMAGE_PATH, 1).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }



    /**
     * Builds a complex data model and stashes it in SQL for later calls.
     *
     * You have to call `./catalogue clean` script to clean the cached database script if you make any change.
     *
     * @param dataModelService
     * @param elementService
     * @return
     */
    DataClass buildRDModel(DataModelService dataModelService, ElementService elementService) {
            initRelationshipTypes()
            DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

            builder.build {
                skip draft
                dataModel(name: "Rare Disease conditions, phenotypes") {
                    description "This is a data model for testing Rare Disease Split Docs"

                    dataClass (name: "Rare Disease conditions, phenotypes and eligibility criteria") {
                        dataClass (name: "Cardiovascular disorders") {
                            dataClass (name: "Arteriopathies") {
                                dataClass (name: "Familial cerebral small vessel disease") {
                                    dataClass (name: "Familial cerebral small vessel disease eligibility") {
                                        dataClass (name: "Familial cerebral small vessel disease inclusion criteria") {
                                            description "Clinical features consistent with cerebral small vessel disease: either lacunar stroke or vascular"
                                        }
                                        dataClass (name: "Familial cerebral small vessel disease exclusion criteria") {
                                            description "Causes of white matter disease other than cerebral small vessel disease (e.g. multiple sclerosis, .."
                                        }
                                        dataClass (name: "Prior genetic testing guidance") {
                                            description "Results should have been reviewed for all genetic tests undertaken, including disease-relevant"
                                        }
                                        dataClass (name: "Familial cerebral small vessel disease prior genetic testing genes") {
                                            description "Testing of the following genes should be carried out PRIOR TO RECRUITMENT where this is in line with"
                                        }
                                        dataClass (name: "Closing statement") {
                                            description "These requirements will be kept under continual review during the main programme and may be subject ..."
                                        }



                                    }

                                    dataClass (name: "Familial cerebral small vessel disease phenotypes") {

                                        dataClass (name: "Migraine with aura") {
                                        }
                                        dataClass (name: "Repeated headache attacks") {
                                        }
                                        dataClass (name: "Stroke") {
                                        }
                                        dataClass (name: "Migraine without aura") {
                                        }

                                    }


                                    dataClass (name: "Familial cerebral small vessel disease clinical tests") {
                                        dataClass (name: "clinical test 1") {
                                        }
                                        dataClass (name: "clinical test 2") {
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }

        return notNull(DataClass.findByName("Rare Disease conditions, phenotypes and eligibility criteria"))
    }


}
