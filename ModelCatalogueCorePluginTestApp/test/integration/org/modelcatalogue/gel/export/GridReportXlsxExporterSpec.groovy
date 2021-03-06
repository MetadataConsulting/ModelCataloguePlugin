package org.modelcatalogue.gel.export

import builders.dsl.spreadsheet.query.api.SpreadsheetCriteria
import builders.dsl.spreadsheet.query.poi.PoiSpreadsheetCriteria
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.export.inventory.DataModelToXlsxExporterSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import spock.lang.IgnoreIf

@IgnoreIf( {
    System.getProperty('spock.ignore.slow') ||
    System.getenv('JENKINS_IGNORE') ||
    System.getProperty('IGNORE_OFFICE')
})


class GridReportXlsxExporterSpec extends AbstractIntegrationSpec {

    public static final String ROOT_DATA_MODEL_NAME = 'Grid Report Data Model'
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    DataModel dataModel

    def setup() {
        initRelationshipTypes()
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)

        CatalogueXmlLoader loader = new CatalogueXmlLoader(builder)
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV1.xml'))
        loader.load(DataModelToXlsxExporterSpec.getResourceAsStream('TestDataModelV2.xml'))
        dataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')

    }

    def "export model to excel"() {
        setup:
        def file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        def outputStream = file.newOutputStream()

        when:
        GridReportXlsxExporter.create(dataModel, dataClassService, grailsApplication, 5).export(outputStream)
        FileOpener.open(file)

        SpreadsheetCriteria query = PoiSpreadsheetCriteria.FACTORY.forFile(file)

        then:
        noExceptionThrown()
    }
}

