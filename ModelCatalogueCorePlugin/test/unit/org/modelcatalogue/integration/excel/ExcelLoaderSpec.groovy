package org.modelcatalogue.integration.excel

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import spock.lang.Specification
import spock.lang.Unroll

class ExcelLoaderSpec extends Specification {

    StringWriter stringWriter
    XmlCatalogueBuilder builder
    ExcelLoader loader

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        builder = new XmlCatalogueBuilder(stringWriter, true)
        loader = new ExcelLoader(builder)
    }


    @Unroll
    def "test expected output for #file"() {
        expect:
        similar file, 'test.catalogue.xml'

        where:
        file << ['test.xlsx', 'legacy.xlsx']

    }


    boolean similar(String sampleFile, String xmlReference) {
        loader.importData(HeadersMap.create(), getClass().getResourceAsStream(sampleFile))
        String xml = stringWriter.toString()
        String expected = getClass().getResourceAsStream(xmlReference).text

        println "==ACTUAL=="
        println xml

        println "==EXPECTED=="
        println expected



        Diff diff = new Diff(xml, expected)
        DetailedDiff detailedDiff = new DetailedDiff(diff)

        assert detailedDiff.similar(), detailedDiff.toString()
        return true
    }

    String build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        builder.build cl
        stringWriter.toString()
    }

}
