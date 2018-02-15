package org.modelcatalogue.gel.export

import spock.lang.IgnoreIf

import static RareDiseasesDocExporter.standardTemplate
import static org.modelcatalogue.core.util.test.FileOpener.open
import org.modelcatalogue.core.DataClass

/**
 * Created by rickrees on 10/03/2016.
 */
@IgnoreIf( { System.getProperty('spock.ignore.slow') || System.getProperty('IGNORE_OFFICE') })
class RareDiseasesDocExporterSpec extends AbstractRareDiseasesExporterSpec {

    def "export eligibility dataClasses to docx"() {
        when:
        boolean eligibilityMode = true;
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        buildTestModel(true)
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')

        new RareDiseasesDocExporter(dataClass, standardTemplate, genomicsService.DOC_IMAGE_PATH, eligibilityMode ).export(file.newOutputStream())

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

        new RareDiseasesDocExporter(dataClass, standardTemplate, genomicsService.DOC_IMAGE_PATH, eligibilityMode).export(file.newOutputStream())

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

        new RareDiseasesDocExporter(dataClass, standardTemplate, genomicsService.DOC_IMAGE_PATH, eligibilityMode).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

}
