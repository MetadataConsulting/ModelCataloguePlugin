package org.modelcatalogue.core.dataimport.excel

import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Ignore
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery
import spock.lang.Shared
import spock.lang.Unroll

class ExcelLoaderSpec extends AbstractIntegrationSpec {
    @Shared String resourcePath = (new File("test/integration/resources/org/modelcatalogue/integration/excel")).getAbsolutePath()
    StringWriter stringWriter
    XmlCatalogueBuilder builder
    ExcelLoader excelLoader
    CatalogueBuilder catalogueBuilder
    def dataModelService, elementService

    DataClassService dataClassService
    GrailsApplication grailsApplication
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        catalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        excelLoader = new ExcelLoader()
    }



    @Unroll
    def "test expected output for #file"() {
        expect:
        similar standardExcelLoaderXmlResult(file,
            HeadersMap.createForStandardExcelLoader()),
            (new FileInputStream (new File (resourcePath + '/test.catalogue.xml'))).text
        //getClass().getResourceAsStream('test.catalogue.xml').text

        where:
        file << ['test.xlsx', 'legacy.xlsx']

    }

    def "test expected output loading spreadsheet produced by ExcelExporter"() {
        expect: similar excelLoader.buildXmlFromSpreadsheetFromExcelExporter(
                HeadersMap.createForSpreadsheetFromExcelExporter(),
                WorkbookFactory.create(
                        (new FileInputStream(resourcePath + '/' + 'excel_exporter_rule_measurement_unit.xls'))),
                0,
                'Fresh Model Imported From ExcelExporter Spreadsheet'),

                (new FileInputStream(new File(resourcePath + '/test_expected_xml_from_load_spreadsheet_from_excel_exporter.xml'))).text
    }

    def "test Exporter/Loader round trip: create model1 -> use ExcelExporter -> Load spreadsheet, creating model2 -> use Excel Exporter"() {
        setup: "Create model1"
        CatalogueXmlLoader loader = new CatalogueXmlLoader(catalogueBuilder)
        loader.load(getClass().getResourceAsStream('TestDataModelV1.xml'))
        loader.load(getClass().getResourceAsStream('TestDataModelV2.xml'))
        DataModel dataModel1 = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')

        File file1 = temporaryFolder.newFile("fromRoundTripModel1${System.currentTimeMillis()}.xlsx")
        File file2 = temporaryFolder.newFile("fromRoundTripModel2${System.currentTimeMillis()}.xlsx")
        String model2Name = 'Fresh Model Imported From ExcelExporter Spreadsheet'


        when: "use ExcelExporter"
        ExcelExporter.create(dataModel1, dataClassService, grailsApplication, 5).export(file1.newOutputStream())
        FileOpener.open(file1)

        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(file1)

        then:
        noExceptionThrown()

        when: "Load spreadsheet, creating model2"
        excelLoader.buildModelFromSpreadsheetFromExcelExporter(
                HeadersMap.createForSpreadsheetFromExcelExporter(),
                WorkbookFactory.create(file1),
                0,
                catalogueBuilder,
                model2Name
        )
        DataModel dataModel2 = DataModel.findByNameAndSemanticVersion(model2Name, '0.0.1')

        then:
        noExceptionThrown()

        when: "use ExcelExporter on model2"
        ExcelExporter.create(dataModel2, dataClassService, grailsApplication, 5).export(file2.newOutputStream())
        FileOpener.open(file2)

        SpreadsheetCriteria query2 = PoiSpreadsheetQuery.FACTORY.forFile(file2)

        then:
        noExceptionThrown()
    }

    def "test default catalogue builder imports dataset"() {
        when: "I load the Excel file"

        excelLoader.buildModelFromStandardWorkbookSheet(
                HeadersMap.createForStandardExcelLoader(),
                WorkbookFactory.create((new FileInputStream(resourcePath + '/' + 'test.xlsx'))),
                catalogueBuilder,
                0)

        then: "new model is created"

        DataModel.findByName("MET-522")
    }

    String standardExcelLoaderXmlResult(String sampleFile, Map<String,String> headersMap, int index = 0) {
        return excelLoader.buildXmlFromStandardWorkbookSheet(headersMap,
            WorkbookFactory.create(
                (new FileInputStream(resourcePath + '/' + sampleFile))),
            //getClass().getResourceAsStream(sampleFile)),
            index)
    }

    Pair<String, List<String>> excelLoaderXmlResult(String sampleFile, int index=0) {
        excelLoader.buildXmlFromWorkbookSheet(
            new XSSFWorkbook(
                getClass().getResourceAsStream(sampleFile)),
            index,
            ExcelLoader.getOwnerAndGelModelFromFileName(sampleFile, '_nt_rawimport')
        )
    }
    Pair<String, List<String>> excelLoaderXmlResult(String sampleFile, String bitInBetween, int index=0) {
        excelLoader.buildXmlFromWorkbookSheet(
            new XSSFWorkbook(
                getClass().getResourceAsStream(sampleFile)),
            index,
            ExcelLoader.getOwnerAndGelModelFromFileName(sampleFile, bitInBetween)
        )
    }


    boolean similar(String sampleXml, String expectedXml) {

        println "==ACTUAL=="
        println sampleXml

        println "==EXPECTED=="
        println expectedXml

        Diff diff = new Diff(sampleXml, expectedXml)
        DetailedDiff detailedDiff = new DetailedDiff(diff)

        assert detailedDiff.similar(), detailedDiff.toString()
        return true
    }

}
