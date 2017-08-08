package org.modelcatalogue.core.dataimport.excel.nt.uclh

import org.apache.commons.lang3.tuple.Pair
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.dataimport.excel.ExcelLoaderSpec
import org.modelcatalogue.core.export.inventory.DataModelToXlsxExporterSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.excel.nt.uclh.UCLHExcelLoader
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.nt.export.SummaryReportXlsxExporter
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery
import spock.lang.Shared

import java.nio.charset.StandardCharsets

/**
 * Created by david on 04/08/2017.
 */
class UCLHExcelLoaderSpec extends ExcelLoaderSpec {
    ElementService elementService
    DataModelService dataModelService
    DataClassService dataClassService
    GrailsApplication grailsApplication

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Shared DataModel sourceDataModel
    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
    @Shared CatalogueXmlLoader catalogueXmlLoader = new CatalogueXmlLoader(defaultCatalogueBuilder)
    @Shared List<String> testFiles = ['UCLHAriaTest.xlsx']
    def setupSpec() {

        InputStream tdm1 = getClass().getResourceAsStream('TestDataModelV1.xml')
        catalogueXmlLoader.load(tdm1)
        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV2.xml'))
        sourceDataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')
    }
    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        excelLoader = new UCLHExcelLoader(false)


    }
    List<String> uclhHeaders = ['L2',	'L3',	'L4',	'L5',	'Lowest level ID',	'Idno',	'Name',	'Description',	'Multiplicity',	'Value Domain / Data Type',	'Related To',	'Current Paper Document  or system name',	'Semantic Matching',	'Known issue',	'Immediate solution', 'Immediate solution Owner',	'Long term solution',	'Long term solution owner',	'Data Item', 'Unique Code',	'Related To',	'Part of standard data set',	'Data Completeness',	'Estimated quality',	'Timely?', 'Comments']

    /**
     * There are two relatedTo columns!!!
     */
    def "test expected output for UCLH #file"() {
        expect:
        similar excelLoaderXmlResult(file).left,//, uclhHeaders),
            getClass().getResourceAsStream('UCLHAriaTestExpected.xml').text
        where:
        file << testFiles

    }
    def "export model to excel"() {
        setup:
        File tempFile = temporaryFolder.newFile("ntSummaryReport${System.currentTimeMillis()}.xlsx")

        when:
        Pair<String, List<String>> xmlAndDataModelNames = excelLoaderXmlResult(file)
        catalogueXmlLoader.load(new ByteArrayInputStream(xmlAndDataModelNames.left.getBytes(StandardCharsets.UTF_8))) // load XML from Excel into catalogue
        excelLoader.addRelationshipsToModels(sourceDataModel, xmlAndDataModelNames.right)


        SummaryReportXlsxExporter.create(sourceDataModel, dataClassService, grailsApplication, 5).export(tempFile.newOutputStream())
        FileOpener.open(tempFile)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file)

        then:
        noExceptionThrown()
        where:
        file << testFiles
    }
    def "empty"() {
        expect:
        '' ? 1 : 2 == 2
        "" ? 1 : 2 == 2
    }



}
