package org.modelcatalogue.core.export.inventory

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.test.FileOpener

class DataModelToXlsxExporterSpec extends AbstractIntegrationSpec {

    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService

    DataModel dataModel

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def setup() {
        dataModel = buildComplexModel(dataModelService, elementService)
    }

    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        when:
        CatalogueElementToXlsxExporter.forDataModel(dataModel, dataClassService, grailsApplication).export(file.newOutputStream())
        FileOpener.open(file)

        then:
        noExceptionThrown()
    }
}
