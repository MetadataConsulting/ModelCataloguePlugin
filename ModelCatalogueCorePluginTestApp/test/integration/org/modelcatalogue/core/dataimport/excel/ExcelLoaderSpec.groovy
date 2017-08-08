package org.modelcatalogue.core.dataimport.excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.integration.excel.HeadersMap
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ExcelLoaderSpec extends Specification {
    @Shared String resourcePath = (new File("test/unit/resources/org/modelcatalogue/integration/excel")).getAbsolutePath()
    StringWriter stringWriter
    XmlCatalogueBuilder builder
    ExcelLoader loader

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        //builder = new XmlCatalogueBuilder(stringWriter, true)
        loader = new ExcelLoader()
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
    String standardExcelLoaderXmlResult(String sampleFile, Map<String,String> headersMap, int index = 0) {
        loader.buildXmlFromStandardWorkbookSheet(headersMap,
            WorkbookFactory.create(
                (new FileInputStream(resourcePath + '/' + sampleFile))),
                //getClass().getResourceAsStream(sampleFile)),
            builder,
            index)
        return stringWriter.toString()

    }

    String excelLoaderXmlResult(String sampleFile, int index=0) {
        loader.buildXmlFromWorkbookSheet(
             new XSSFWorkbook(
            getClass().getResourceAsStream(sampleFile)),
            index)
        return stringWriter.toString()
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
