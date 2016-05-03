package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.core.AbstractIntegrationSpec

import static org.modelcatalogue.core.util.test.FileOpener.open

import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService

class DataModelToDocxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService

    DataModel dataModel

    def setup() {
        dataModel = buildComplexModel(dataModelService, elementService)

    }

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "export model to docx"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")

        new DataModelToDocxExporter(DataModel.get(dataModel.id), dataClassService).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

    def "export model to docx with image"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")

        def testTemplate = {
            'document' font: [color: '#000000']
        }

        new DataModelToDocxExporter(DataModel.get(dataModel.id), dataClassService, testTemplate, DataModelToDocxExporterSpec.getResource('gel-logo.png').toExternalForm()).export(file.newOutputStream())

        open file

        then:
        noExceptionThrown()

    }

}
