package org.modelcatalogue.core.dataimport.excel.loinc

import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.dataimport.excel.ExcelExporter
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.HeadersMap
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery
import spock.lang.Shared
import spock.lang.Unroll

class LoincExcelLoaderSpec extends AbstractIntegrationSpec {
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
        excelLoader = new LoincExcelLoader()
    }


    def "test default catalogue builder imports gosh dataset"(){

        when: "I load the Excel file"

        excelLoader.buildModelFromStandardWorkbookSheet(
            LoincHeadersMap.createForGoshExcelLoader(),
//            LoincHeadersMap.createForLoincExcelLoader(),
//            WorkbookFactory.create((new FileInputStream(resourcePath + '/' + 'loinc/loinc.xlsx'))),
            WorkbookFactory.create((new FileInputStream(resourcePath + '/' + 'goshTestCodes/GOSH_lab_test_codes100.xlsx'))),
            catalogueBuilder,
            0)

        then: "new model is created"

        DataModel.findByName("GOSH")


    }



}
