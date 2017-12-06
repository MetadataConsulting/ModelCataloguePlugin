package org.modelcatalogue.core.dataimport.excel.nt.uclh

import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.util.test.FileOpener
import org.modelcatalogue.integration.xml.CatalogueXmlLoader
import org.modelcatalogue.spreadsheet.query.api.SpreadsheetCriteria
import org.modelcatalogue.spreadsheet.query.poi.PoiSpreadsheetQuery
import spock.lang.Shared

import java.nio.charset.StandardCharsets

/**
 * Created by david on 04/08/2017.
 */

class UCLHExcelLoaderSpec extends AbstractIntegrationSpec {

    @Shared ElementService elementService
    @Shared DataModelService dataModelService
    @Shared DataClassService dataClassService
    @Shared GrailsApplication grailsApplication
    @Shared StringWriter stringWriter
    @Shared UCLHExcelLoader excelLoader

    @Shared List<String> testFiles = ['UCLH_nt_rawimport_AriaTest.xlsx']

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()
    @Shared DefaultCatalogueBuilder defaultCatalogueBuilder

    @Shared CatalogueXmlLoader catalogueXmlLoader
    @Shared DataModel sourceDataModel


//    def setupSpec() {
//        initRelationshipTypes()
//        defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
//        catalogueXmlLoader = new CatalogueXmlLoader(defaultCatalogueBuilder)
//
//        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV1.xml'))
//        catalogueXmlLoader.load(getClass().getResourceAsStream('TestDataModelV2.xml'))
//        sourceDataModel = DataModel.findByNameAndSemanticVersion('TestDataModel', '2')
//    }
//
//    def setup() {
//        XMLUnit.ignoreWhitespace = true
//        XMLUnit.ignoreComments = true
//        XMLUnit.ignoreAttributeOrder = true
//        stringWriter = new StringWriter()
//        //builder = new XmlCatalogueBuilder(stringWriter, true)
//        excelLoader = new UCLHExcelLoader()
//    }
//    List<String> uclhHeaders = ['L2',	'L3',	'L4',	'L5',	'Lowest level ID',	'Idno',	'Name',	'Description',	'Multiplicity',	'Value Domain / Data Type',	'Related To',	'Current Paper Document  or system name',	'Semantic Matching',	'Known issue',	'Immediate solution', 'Immediate solution Owner',	'Long term solution',	'Long term solution owner',	'Data Item', 'Unique Code',	'Related To',	'Part of standard data set',	'Data Completeness',	'Estimated quality',	'Timely?', 'Comments']
//
//    /**
//     * There are two relatedTo columns!!!
//     */
//    def "test expected output for UCLH #file"() {
//        expect:
//        similar excelLoaderXmlResult(file).left, getClass().getResourceAsStream('UCLHAriaTestExpected.xml').text
//        where:
//        file << testFiles
//
//    }
//    /**
//     * The TestDataModelV2 file has been modified to include data elements, with model catalogue ids which are
//     * referenced in the UCLHAriaTest excel file, in the "source data model",
//     * so that relationships are created between those elements and their representatives in the
//     * imported model based on UCLHAriaTest.
//     * The created NTGridReport should have entries for Prescription, Plan, External Beam Type which
//     * have data source ARIA_UCLH.
//     */
//    def "import model from excel, create relationships, export NTGridReport"() {
//        setup:
//        File tempFile = temporaryFolder.newFile("ntSummaryReport${System.currentTimeMillis()}.xlsx")
//
//        when:
//        Pair<String, List<String>> xmlAndDataModelNames = excelLoaderXmlResult(file)
//        catalogueXmlLoader.load(new ByteArrayInputStream(xmlAndDataModelNames.left.getBytes(StandardCharsets.UTF_8))) // load XML from Excel into catalogue
//        excelLoader.addRelationshipsToModels(sourceDataModel, xmlAndDataModelNames.right)
//
//
//        GMCGridReportXlsxExporter.create(sourceDataModel, dataClassService, grailsApplication, 5).export(tempFile.newOutputStream())
//        FileOpener.open(tempFile)
//
//        SpreadsheetCriteria query = PoiSpreadsheetQuery.FACTORY.forFile(tempFile)
//
//        then:
//        noExceptionThrown()
//        where:
//        file << testFiles
//    }
//
//    Pair<String, List<String>> excelLoaderXmlResult(String sampleFile, int index=0) {
//        excelLoader.buildXmlFromWorkbookSheet(
//                new XSSFWorkbook(
//                        getClass().getResourceAsStream(sampleFile)),
//                index,
//                ExcelLoader.getOwnerAndGelModelFromFileName(sampleFile, '_nt_rawimport')
//        )
//    }
    def "ignoring UCLHExcelLoaderSpec test for now - FIXME"() {
        return true
    }


}
