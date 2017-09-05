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
import org.modelcatalogue.core.dataimport.excel.gmcGridReport.GMCGridReportXlsxExporter
import org.modelcatalogue.core.dataimport.excel.uclh.UCLHExcelLoaderSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery
import spock.lang.Shared

import java.nio.charset.StandardCharsets

/**
 * Created by davidmilward on 31/08/2017.
 */
class OpenEhrExcelLoaderSpec extends ExcelLoaderSpec{

    @Shared ElementService elementService
    @Shared DataModelService dataModelService
    @Shared DataClassService dataClassService
    @Shared GrailsApplication grailsApplication

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()
    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder

    @Shared CatalogueXmlLoader catalogueXmlLoader
    @Shared DataModel sourceDataModel


    @Shared List<String> testOpenEhrFiles = ['GEL_openEHR_Cross_references_test.xlsx']


    def setupSpec(){
        initRelationshipTypes()
        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        catalogueXmlLoader = new CatalogueXmlLoader(defaultCatalogueBuilder)

        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV1.xml'))
        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV2.xml'))
        sourceDataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')
    }

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        excelLoader = new org.modelcatalogue.core.dataimport.excel.nt.uclh.OpenEhrExcelLoader(true)
        List<String> openEHRHeaders = ['GEL Dataset Identifier', 'GEL Dataset Name', 'Archetype Path Query Statement']
    }

    def "test expected output for OpenEHR #file"() {
        expect:
        similar excelLoaderXmlResult(file, '_openEHR_Cross_references_test').left, getClass().getResourceAsStream('OpenEhrTest.xml').text
        where:
        file << testOpenEhrFiles

    }























}
