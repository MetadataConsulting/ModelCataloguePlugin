package org.modelcatalogue.integration.mc

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import spock.lang.Specification

class ModelCatalogueLoaderSpec extends Specification {

    StringWriter stringWriter
    XmlCatalogueBuilder builder
    ModelCatalogueLoader loader

    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        builder = new XmlCatalogueBuilder(stringWriter)
        loader = ModelCatalogueLoader.build(builder).create()
    }


    def "test expected output"() {
        expect:
        similar 'test.mc', 'test.catalogue.xml'

    }


    boolean similar(String sampleFile, String xmlReference) {
        loader.load(getClass().getResourceAsStream(sampleFile))
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
