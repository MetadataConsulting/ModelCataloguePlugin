package org.modelcatalogue.integration.obo

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import spock.lang.Specification

class OboLoaderSpec extends Specification {

    StringWriter stringWriter
    XmlCatalogueBuilder builder
    OboLoader loader

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        builder = new XmlCatalogueBuilder(stringWriter)
        loader = new OboLoader(builder)
    }


    def "test expected output"() {
        expect:
        similar 'test.obo', 'test.catalogue.xml'

    }

    boolean similar(String sampleFile, String xmlReference) {
        loader.load(getClass().getResourceAsStream(sampleFile), sampleFile)
        String xml = stringWriter.toString()
        String expected = getClass().getResourceAsStream(xmlReference).text

        println "==ACTUAL=="
        println xml

        println "==EXPECTED=="
        println expected



        Diff diff = new Diff(xml.replaceAll(/(?m)\s+/, ' '), expected.replaceAll(/(?m)\s+/, ' '))
        DetailedDiff detailedDiff = new DetailedDiff(diff)

        assert detailedDiff.similar(), detailedDiff.toString()
        return true
    }

}
