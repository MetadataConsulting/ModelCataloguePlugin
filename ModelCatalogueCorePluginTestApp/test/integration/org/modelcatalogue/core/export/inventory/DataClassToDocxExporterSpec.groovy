package org.modelcatalogue.core.export.inventory

import org.modelcatalogue.core.AbstractIntegrationSpec
import static org.modelcatalogue.core.util.test.FileOpener.open
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService

class DataClassToDocxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService

    DataClass model

    def setup() {
        buildComplexModel(dataModelService, elementService)
        model = DataClass.findByName('C4CTDE Root')
    }

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "export model to docx"() {
        when:
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")


        new DataClassToDocxExporter(model, dataClassService, elementService).export(file.newOutputStream())


        open file

        then:
        noExceptionThrown()
    }
}
