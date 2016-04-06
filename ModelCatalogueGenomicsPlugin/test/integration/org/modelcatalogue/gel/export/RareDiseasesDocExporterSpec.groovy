package org.modelcatalogue.gel.export

import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.ContextItem
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder

import static org.modelcatalogue.core.genomics.GenomicsController.*
import static RareDiseasesDocExporter.standardTemplate
import static org.modelcatalogue.core.util.test.FileOpener.open

/**
 * Created by rickrees on 10/03/2016.
 */
class RareDiseasesDocExporterSpec extends IntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
    }

    @Rule TemporaryFolder temporaryFolder


    def "export eligibility dataClasses to docx"() {
        when:
        boolean eligibilityMode = true;
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        buildTestModel(true)
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')

        new RareDiseasesDocExporter(dataClass, standardTemplate, DOC_IMAGE_PATH, eligibilityMode ).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }



    def "export Phenotypes and Clinical tests dataClasses to docx"() {
        when:
        boolean eligibilityMode = false;
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        buildTestModel(true)
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')

        new RareDiseasesDocExporter(dataClass, standardTemplate, DOC_IMAGE_PATH, eligibilityMode).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

    def "export Phenotypes and Clinical tests with no Phenotypes to docx"() {
        when:
        boolean eligibilityMode = false;
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        buildTestModel(false)
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')

        new RareDiseasesDocExporter(dataClass, standardTemplate, DOC_IMAGE_PATH, eligibilityMode).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

    // this model reflects the data mix of eligibility criteria, phenotypes & clinical tests that need to be extracted
    // by the two report generation methods it's a bit nasty looking but creates a fairly realistic model
    private DataModel buildTestModel(boolean createPhenotypes) {
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        builder.build {
            dataModel(name: 'Test Data Model') {
                description "This is a data model for testing Eligibility OR Phenotype and Clinicals tests exports"

                dataClass (name: 'Dataclass Top Level 1 Root') {
                    for (int i in 1..2) {
                        dataClass name: "Disorder >>>$i<<< Level2", {
                            description "This is a description for Model $i"

                            for (int j in 1..2) {
                                dataClass name: "Disorder >>>$i<<< SubCondition Level3 Model Data Element $j", {
                                    description "This is a description for Model $i Data Element $j"

                                    dataClass name: "Disorder >>$i<< heading Level4 Model Data Element $j", {
                                        description "Disorder >>$i<< heading Level4 description for Model Data Element $j"

                                        dataClass name: "Disorder >$i< Eligibility Level5 Model $i Data Element $j", {
                                            description "Disorder >$i< heading Level5 description for Model $i Data Element $j"

                                            dataClass name: "Inclusion criteria name $i $j", {
                                                description "Inclusion criteria description  $i $j"
                                            }
                                            dataClass name: "Exclusion criteria name $i $j", {
                                                description "Exclusion criteria description  $i $j"
                                            }
                                            dataClass name: "Prior Genetic testing name $i $j", {
                                                description "Prior Genetic testing description  $i $j"
                                            }
                                            dataClass name: "Prior testing genes name $i $j", {
                                                description "Prior testing genes description  $i $j"
                                            }
                                            dataClass name: "Closing statement name $i $j", {
                                                description "Closing statement description  $i $j"
                                            }
                                        }

                                        dataClass name: "Disorder >$i< Phenotypes Level5 Model $i Data Element $j", {
                                            description "Disorder >$i< heading Level5 description for Model $i Data Element $j"

                                            if (createPhenotypes) {
                                                for (int k in 1..15) {
                                                    dataClass name: "Phenotype ($k) name $i $j", {
                                                        //sets a value for OBO ID in the extension map - there must be a clearer way to do this
                                                        ContextItem contextElement = context.getContextElement(DataClass, 0)
                                                        contextElement.element.setExtension("OBO ID", "HP:" + (i + j + k))
                                                    }
                                                }
                                            }
                                        }

                                        dataClass name: "Disorder >$i< Clinical tests Level5 Model $i Data Element $j", {
                                            description "Disorder >$i< heading Level5 description for Model $i Data Element $j"

                                            for (int k in 1..5) {
                                                dataClass name: "Clinical tests ($k) name $i $j", {
                                                }
                                            }
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

}
