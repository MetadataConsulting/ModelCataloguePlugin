package org.modelcatalogue.core.export.inventory

import grails.test.spock.IntegrationSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
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
        def exporter = new DataModelToXlsxExporter(dataModel: dataModel, dataClassService: dataClassService)
        exporter.export(file.newOutputStream())
        FileOpener.open(file)

        then:
        noExceptionThrown()
    }
}
